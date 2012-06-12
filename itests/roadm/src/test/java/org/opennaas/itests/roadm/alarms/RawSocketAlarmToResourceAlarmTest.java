package org.opennaas.itests.roadm.alarms;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeTestHelper;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.events.EventFilter;
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.alarms.ResourceAlarm;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.itests.helpers.InitializerTestHelper;
import org.opennaas.extensions.roadm.wonesys.protocols.alarms.WonesysAlarm;
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
public class RawSocketAlarmToResourceAlarmTest implements EventHandler
{
	private final static Log	log				= LogFactory.getLog(RawSocketAlarmToResourceAlarmTest.class);

	@Inject
	private BundleContext		bundleContext;

	private List<Event>			receivedEvents	= new ArrayList<Event>();
	private Object				lock			= new Object();

	@Inject
	private IEventManager		eventManager;

	@Inject
	private IResourceManager	resourceManager;

	@Inject
	private IProtocolManager	protocolManager;

	@Inject
	@Filter("(capability=monitoring)")
	private ICapabilityFactory	monitoringFactory;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.roadm.repository)")
	private BlueprintContainer	roadmRepositoryService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.roadm.protocols.wonesys)")
	private BlueprintContainer	wonesysProtocolService;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-luminis", "opennaas-roadm-proteus"),
				includeTestHelper(),
				noConsole(),
				keepRuntimeFolder());
	}

	/**
	 * Check a TransportAlarm is transformed into a ResourceAlarm
	 */
	@Test
	public void checkTransportAlarmTriggersResourceAlarmTest() throws Exception {

		lock = new Object();

		InitializerTestHelper.removeResources(resourceManager);

		// register this as ResourceAlarm listener
		int registrationNum = registerAsResourceAlarmListener(this);

		// create resource with monitoring capability and connections capab
		IResource resource = resourceManager.createResource(createResourceDescriptorWithConnectionsAndMonitoring());

		// create session
		IProtocolSessionManager sessionManager =
				protocolManager.getProtocolSessionManagerWithContext(resource.getResourceIdentifier().getId(),
						newWonesysSessionContextMock());

		// start resource
		resourceManager.startResource(resource.getResourceIdentifier());

		receivedEvents.clear();

		// generate Alarm
		generateAlarm(sessionManager.obtainSessionByProtocol("wonesys", false));

		// check ResourceAlarm has arrived in less than 30s
		synchronized (lock) {
			lock.wait(60000);
			Assert.assertFalse(receivedEvents.isEmpty());
		}

		for (Event alarm : receivedEvents) {
			String resourceId = (String) alarm.getProperty(ResourceAlarm.RESOURCE_ID_PROPERTY);
			String alarmCode = (String) alarm.getProperty(ResourceAlarm.ALARM_CODE_PROPERTY);
			String alarmDesc = (String) alarm.getProperty(ResourceAlarm.DESCRIPTION_PROPERTY);
			log.info("Alarm " + alarmCode + " received in resource " + resourceId + " : " + alarmDesc);
			Assert.assertTrue(resourceId.equals(resource.getResourceIdentifier().getId()));
		}

		unregisterAsListener(registrationNum);

		resourceManager.stopResource(resource.getResourceIdentifier());
		resourceManager.removeResource(resource.getResourceIdentifier());
	}

	@Override
	public void handleEvent(Event event) {
		synchronized (lock) {
			receivedEvents.add(event);
			lock.notify();
		}
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
		capabilities.add("connections");
		capabilities.add("monitoring");
		capabilities.add("queue");
		return ResourceDescriptorFactory.newResourceDescriptorProteus("TestProteus", "roadm", capabilities);
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

	/**
	 * Generates a WonesysAlarm
	 * 
	 * @param session
	 */
	private void generateAlarm(IProtocolSession session) {
		int chassis = 0;
		int slot = 1;

		Map<String, Object> prop = new HashMap<String, Object>();
		prop.put(WonesysAlarm.SESSION_ID_PROPERTY, session.getSessionId());
		prop.put(WonesysAlarm.ALARM_ID_PROPERTY, "80");
		prop.put(WonesysAlarm.ARRIVAL_TIME, new Date().getTime());
		prop.put(WonesysAlarm.CHASSIS_PROPERTY, chassis);
		prop.put(WonesysAlarm.SLOT_PROPERTY, slot);

		WonesysAlarm alarm = new WonesysAlarm(prop);

		log.debug("Generating WonesysAlarm in session: " + session.getSessionId());
		eventManager.publishEvent(alarm);
	}

}
