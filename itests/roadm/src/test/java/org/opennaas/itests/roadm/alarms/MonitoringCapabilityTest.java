package org.opennaas.itests.roadm.alarms;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.events.EventFilter;
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.protocols.sessionmanager.ProtocolManager;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.ILifecycle;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ResourceIdentifier;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.alarms.CapabilityAlarm;
import org.opennaas.core.resources.alarms.IAlarmsRepository;
import org.opennaas.core.resources.alarms.ResourceAlarm;
import org.opennaas.core.resources.alarms.SessionAlarm;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.mock.MockProtocolSessionFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionFactory;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.roadm.wonesys.protocols.WonesysProtocolSession;
import org.opennaas.extensions.roadm.wonesys.protocols.alarms.WonesysAlarm;
import org.opennaas.extensions.roadm.wonesys.transports.rawsocket.RawSocketTransport;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class MonitoringCapabilityTest implements EventHandler
{

	private final static Log		log							= LogFactory.getLog(MonitoringCapabilityTest.class);

	@Inject
	private BundleContext			bundleContext;

	@Inject
	private IEventManager			eventManager;

	@Inject
	private IResourceManager		resourceManager;

	@Inject
	private IProtocolManager		protocolManager;

	@Inject
	private IAlarmsRepository		alarmRepo;

	@Inject
	@Filter("(capability=monitoring)")
	private ICapabilityFactory		monitoringFactory;

	@Inject
	@Filter("(&(actionset.name=proteus)(actionset.capability=monitoring)(actionset.version=1.0))")
	private IActionSet				actionSet;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.roadm.repository)")
	private BlueprintContainer		roadmRepositoryService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.roadm.protocols.wonesys)")
	private BlueprintContainer		wonesysProtocolService;

	private IProtocolSessionManager	sessionManager;

	private IResource				resource;

	private boolean					alarmReceived				= false;
	List<Event>						receivedEvents				= new ArrayList<Event>();

	private final Object			lock						= new Object();
	private int						alarmCounter				= 0;
	private List<WonesysAlarm>		receivedAlarms				= new ArrayList<WonesysAlarm>();
	public static final String		MSG_RCVD_EVENT_TOPIC		= RawSocketTransport.MSG_RCVD_EVENT_TOPIC;
	public static final String		MESSAGE_PROPERTY_NAME		= RawSocketTransport.MESSAGE_PROPERTY_NAME;
	public static final String		TRANSPORT_ID_PROPERTY_NAME	= RawSocketTransport.TRANSPORT_ID_PROPERTY_NAME;
	public static final String		ARRIVAL_TIME_PROPERTY_NAME	= RawSocketTransport.ARRIVAL_TIME_PROPERTY_NAME;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-luminis", "opennaas-roadm-proteus"),
				noConsole(),
				keepRuntimeFolder());
	}

	@Before
	public void initResource() throws ResourceException, ProtocolException {

		removeResourcesFromRepo();

		resource = resourceManager.createResource(createResourceDescriptorWithConnectionsAndMonitoring());

		try {
			sessionManager = protocolManager.getProtocolSessionManagerWithContext(resource.getResourceIdentifier().getId(),
					newWonesysSessionContextMock());

			resourceManager.startResource(resource.getResourceIdentifier());

		} catch (ProtocolException e) {
			resourceManager.removeResource(resource.getResourceIdentifier());
			throw e;
		}

	}

	@After
	public void removeResourcesFromRepo() throws ResourceException {

		List<IResource> resources = resourceManager.listResourcesByType("roadm");
		for (int i = resources.size() - 1; i >= 0; i--) {
			IResource resource = resources.get(i);
			if (resource.getState().equals(ILifecycle.State.ACTIVE))
				resourceManager.stopResource(resource.getResourceIdentifier());
			resourceManager.removeResource(resource.getResourceIdentifier());
		}
	}

	@Test
	public void CapabilityTest() throws Exception {

		int registrationNum = registerAsResourceAlarmListener(this);

		alarmReceived = false;

		// generate WonesysAlarm
		generateWonesysAlarm(sessionManager.obtainSessionByProtocol("wonesys", false));

		// check ResourceAlarm is generated (monitoringCapability generates it)
		synchronized (lock) {
			log.debug("Waiting for alarm");
			lock.wait(10000);
			log.debug("Finished waiting");
			Assert.assertTrue(alarmReceived);
		}

		unregisterAsListener(registrationNum);

		resourceManager.stopResource(resource.getResourceIdentifier());
		resourceManager.removeResource(resource.getResourceIdentifier());
	}

	@Test
	public void alarmsRepositoryTest() throws Exception {

		String resourceId = new ResourceIdentifier("proteus").getId();

		ResourceAlarm alarm = generateResourceAlarm(resourceId);

		Thread.sleep(15000);

		List<ResourceAlarm> alarms = alarmRepo.getResourceAlarms(resourceId);

		Assert.assertTrue(alarms.contains(alarm));

		// boolean isFound = false;
		// for (ResourceAlarm alarmToCompare : alarms) {
		// if (alarmToCompare.getProperty(ResourceAlarm.RESOURCE_ID_PROPERTY).equals(resourceId)) {
		// isFound = true;
		// break;
		// }
		// }
		// Assert.assertTrue(isFound);

		alarms = alarmRepo.getAlarms();

		Assert.assertTrue(alarms.contains(alarm));

		// boolean isFound = false;
		// for (ResourceAlarm alarmToCompare : alarms) {
		// if (alarmToCompare.getProperty(ResourceAlarm.RESOURCE_ID_PROPERTY).equals(resourceId)) {
		// isFound = true;
		// break;
		// }
		// }

		alarmRepo.clear();

		Assert.assertTrue(alarmRepo.getResourceAlarms(resourceId).isEmpty());
		Assert.assertTrue(alarmRepo.getAlarms().isEmpty());
	}

	@Test
	public void repoReceivesAlarmsCausedByAlarmsInSession() throws Exception {

		IProtocolSessionManager sessionManager =
				protocolManager.getProtocolSessionManagerWithContext(resource.getResourceIdentifier().getId(),
						newWonesysSessionContextMock());

		alarmRepo.clear();

		// generate WonesysAlarm
		generateWonesysAlarm(sessionManager.obtainSessionByProtocol("wonesys", false));

		Thread.sleep(10000);

		// Check alarmRepo has an alarm (only one)
		List<ResourceAlarm> alarms = alarmRepo.getResourceAlarms(resource.getResourceIdentifier().getId());
		Assert.assertFalse(alarms.isEmpty());
		Assert.assertTrue(alarms.size() == 1);

	}

	@Test
	public void checkHandleSessionAlarmTriggersCapabilityAlarmTest() throws Exception {

		String resourceId = "TestResourceId";

		IProtocolSessionFactory factory = new MockProtocolSessionFactory();
		Map serviceProperties = new HashMap<String, String>();
		serviceProperties.put(ProtocolSessionContext.PROTOCOL, "mock");

		((ProtocolManager) protocolManager).sessionFactoryAdded(factory, serviceProperties);

		// create session
		ProtocolSessionContext sessionContext = newWonesysSessionContextMock();

		ProtocolSessionManager sessionManager =
				(ProtocolSessionManager) protocolManager.getProtocolSessionManagerWithContext(resourceId, sessionContext);
		IProtocolSession session = sessionManager.obtainSession(sessionContext, false);

		// register this as ResourceAlarm listener
		int registrationNum = registerAsCapabilityAlarmListener(this);

		receivedEvents.clear();

		// generate Session Alarm
		Event newSessionAlarm = generateAlarm(session);

		// sessionManager should handle SessionAlarm and generate a CapabilityAlarm
		sessionManager.handleEvent(newSessionAlarm);

		// check CapabilityAlarm has arrived in less than 30s
		synchronized (lock) {
			lock.wait(30000);
			Assert.assertFalse(receivedEvents.isEmpty());
		}

		for (Event alarm : receivedEvents) {
			Assert.assertEquals(resourceId, (String) alarm.getProperty(CapabilityAlarm.RESOURCE_ID_PROPERTY));
			Event sessionAlarm = (Event) alarm.getProperty(CapabilityAlarm.CAUSE_PROPERTY);
			Assert.assertTrue(sessionAlarm instanceof SessionAlarm);
			Assert.assertEquals(session.getSessionId(), sessionAlarm.getProperty(SessionAlarm.SESSION_ID_PROPERTY));
		}

		unregisterAsListener(registrationNum);

		sessionManager.destroyProtocolSession(session.getSessionId());
	}

	/**
	 * Check a TransportAlarm is transformed into a ResourceAlarm
	 */
	@Test
	public void checkSessionAlarmTriggersCapabilityAlarmTest2() throws Exception {

		String resourceId = "TestResourceId";

		IProtocolSessionFactory factory = new MockProtocolSessionFactory();
		Map serviceProperties = new HashMap<String, String>();
		serviceProperties.put(ProtocolSessionContext.PROTOCOL, "mock");

		((ProtocolManager) protocolManager).sessionFactoryAdded(factory, serviceProperties);

		// create session
		ProtocolSessionContext sessionContext = newWonesysSessionContextMock();

		IProtocolSessionManager sessionManager =
				protocolManager.getProtocolSessionManagerWithContext(resourceId, sessionContext);
		IProtocolSession session = sessionManager.obtainSession(sessionContext, false);

		// register this as ResourceAlarm listener
		int registrationNum = registerAsCapabilityAlarmListener(this);

		receivedEvents.clear();

		// generate Session Alarm
		generateAndPublishAlarm(session);

		// check CapabilityAlarm has arrived in less than 30s
		// sessionManager should handle SessionAlarm and generate a CapabilityAlarm
		synchronized (lock) {
			lock.wait(30000);
			Assert.assertFalse(receivedEvents.isEmpty());
		}

		for (Event alarm : receivedEvents) {
			Assert.assertEquals(resourceId, (String) alarm.getProperty(CapabilityAlarm.RESOURCE_ID_PROPERTY));
			Event sessionAlarm = (Event) alarm.getProperty(CapabilityAlarm.CAUSE_PROPERTY);
			Assert.assertTrue(sessionAlarm instanceof SessionAlarm);

			Assert.assertEquals(session.getSessionId(), sessionAlarm.getProperty(SessionAlarm.SESSION_ID_PROPERTY));
		}

		unregisterAsListener(registrationNum);

		sessionManager.destroyProtocolSession(session.getSessionId());
	}

	class TestInitInfo {
		public WonesysProtocolSession	session;
		public IProtocolSessionManager	sessionManager;
		public IResource				resource;
		public String					transportId;
		public int						regNum;

	}

	/**
	 * Test that WonesysProtocol receives alarms correctly from transport and notifies listeners upon alarm reception.
	 */
	public void alarmsReceivedTest(TestInitInfo initInfo) throws InterruptedException {

		alarmReceived = false;

		generateAlarm(initInfo.session);

		synchronized (lock) {
			lock.wait(3000);
			// check that the alarm is received & listeners arAlle notified
			Assert.assertTrue(alarmReceived);
		}

		alarmReceived = false;
		alarmCounter = 0;
		receivedAlarms.clear();
	}

	/**
	 * Tests that WonesysProtocolSession distinguishes between alarms and command responses, and only rises and alarm when alarms are received.
	 */
	public void checkAlarmsAndCommandsTest(TestInitInfo initInfo) throws InterruptedException {

		alarmReceived = false;
		alarmCounter = 0;
		receivedAlarms.clear();

		generateAlarm(initInfo.session);

		synchronized (lock) {
			lock.wait(3000);
			// check that the alarm is received & listeners are notified
			Assert.assertTrue(alarmReceived);
		}

		alarmReceived = false;

		// generate command response
		// FIXME XOR is incorrect
		String commandResponse = "59100117FFFF0B02FFFFFFFF0100000100"; // Set channel resp (OK)
		generateRawSocketEvent(initInfo.session.getWonesysTransport().getTransportID(), commandResponse);

		synchronized (lock) {
			lock.wait(3000);
			// check that no alarm has been received
			Assert.assertFalse(alarmReceived);
		}

		Assert.assertTrue(alarmCounter == 1);

		alarmReceived = false;
		alarmCounter = 0;
		receivedAlarms.clear();
	}

	private void generateRawSocketEvent(String transportId, String message) {

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(MESSAGE_PROPERTY_NAME, message);
		properties.put(TRANSPORT_ID_PROPERTY_NAME, transportId);
		long creationTime = new Date().getTime();
		properties.put(ARRIVAL_TIME_PROPERTY_NAME, creationTime);

		Event event = new Event(MSG_RCVD_EVENT_TOPIC, properties);

		eventManager.publishEvent(event);
		log.debug("RawSocketTransport Event generated! " + message + " at " + creationTime);
	}

	/**
	 * Configure the protocol to connect
	 */
	private ProtocolSessionContext newWonesysSessionContextMock() {

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();

		protocolSessionContext.addParameter(
				"protocol.mock", "true");
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"wonesys");
		// ADDED
		return protocolSessionContext;
	}

	private int registerAsResourceAlarmListener(EventHandler handler) {
		EventFilter filter = new EventFilter(ResourceAlarm.TOPIC);
		return eventManager.registerEventHandler(handler, filter);
	}

	private void unregisterAsListener(int registrationNum) {
		eventManager.unregisterHandler(registrationNum);
	}

	private ResourceDescriptor createResourceDescriptorWithConnectionsAndMonitoring() {
		List<String> capabilities = new ArrayList<String>();
		capabilities.add("monitoring");
		capabilities.add("queue");
		return ResourceDescriptorFactory.newResourceDescriptorProteus("TestProteus", "roadm", capabilities);
	}

	/**
	 * Generates a WonesysAlarm
	 * 
	 * @param session
	 */
	private void generateWonesysAlarm(IProtocolSession session) {
		int chassis = 0;
		int slot = 1;

		Map<String, Object> prop = new HashMap<String, Object>();
		prop.put(WonesysAlarm.SESSION_ID_PROPERTY, session.getSessionId());
		prop.put(WonesysAlarm.ALARM_ID_PROPERTY, "80");
		prop.put(WonesysAlarm.ARRIVAL_TIME, new Date().getTime());
		prop.put(WonesysAlarm.CHASSIS_PROPERTY, chassis);
		prop.put(WonesysAlarm.SLOT_PROPERTY, slot);

		WonesysAlarm alarm = new WonesysAlarm(prop);

		log.info("Generating WonesysAlarm in session: " + session.getSessionId());
		eventManager.publishEvent(alarm);
	}

	@Override
	public void handleEvent(Event event) {
		synchronized (lock) {
			alarmReceived = true;
			receivedEvents.add(event);
			lock.notifyAll();
		}
	}

	/**
	 * Generates a WonesysAlarm
	 * 
	 * @param session
	 */
	private void generateAndPublishAlarm(IProtocolSession session) {
		log.debug("Generating SessionAlarm in session: " + session.getSessionId());
		eventManager.publishEvent(generateAlarm(session));
	}

	/**
	 * Generates a WonesysAlarm
	 * 
	 * @param session
	 */
	private Event generateAlarm(IProtocolSession session) {

		Map<String, Object> prop = new HashMap<String, Object>();
		prop.put(SessionAlarm.SESSION_ID_PROPERTY, session.getSessionId());

		SessionAlarm alarm = new SessionAlarm(prop);

		return alarm;
	}

	private int registerAsCapabilityAlarmListener(EventHandler handler) {
		EventFilter filter = new EventFilter(CapabilityAlarm.TOPIC);
		return eventManager.registerEventHandler(handler, filter);
	}

	/**
	 * Generates a ResourceAlarm
	 * 
	 * @param resourceId
	 */
	private ResourceAlarm generateResourceAlarm(String resourceId) {

		Map<String, Object> prop = new HashMap<String, Object>();
		prop.put(ResourceAlarm.ARRIVAL_TIME_PROPERTY, new Date().getTime());
		prop.put(ResourceAlarm.ALARM_CODE_PROPERTY, "0001");
		prop.put(ResourceAlarm.RESOURCE_ID_PROPERTY, resourceId);
		prop.put(ResourceAlarm.DESCRIPTION_PROPERTY, "Testing alarm");

		ResourceAlarm alarm = new ResourceAlarm(prop);

		log.debug("Generating ResourceAlarm for resource " + resourceId);
		eventManager.publishEvent(alarm);
		return alarm;
	}

}
