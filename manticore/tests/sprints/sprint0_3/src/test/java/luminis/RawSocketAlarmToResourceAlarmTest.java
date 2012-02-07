package luminis;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import junit.framework.Assert;
import net.i2cat.luminis.protocols.wonesys.alarms.WonesysAlarm;
import net.i2cat.nexus.tests.InitializerTestHelper;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.events.EventFilter;
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.alarms.ResourceAlarm;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

@RunWith(JUnit4TestRunner.class)
public class RawSocketAlarmToResourceAlarmTest extends AbstractIntegrationTest implements EventHandler {

	static Log			log				= LogFactory.getLog(RawSocketAlarmToResourceAlarmTest.class);

	@Inject
	BundleContext		bundleContext	= null;

	List<Event>			receivedEvents	= new ArrayList<Event>();
	final Object		lock			= new Object();

	IEventManager		eventManager;
	IResourceManager	resourceManager;
	IProtocolManager	protocolManager;

	@Configuration
	public static Option[] configuration() throws Exception {

		Option[] options = combine(
				IntegrationTestsHelper.getLuminisTestOptions(),
				mavenBundle().groupId("org.opennaas").artifactId(
						"opennaas-core-events"),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.tests.helper")
				// , vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
				);

		return options;
	}

	private void initBundles() {
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);

		eventManager = getOsgiService(IEventManager.class, 5000);
		resourceManager = getOsgiService(IResourceManager.class, 5000);
		protocolManager = getOsgiService(IProtocolManager.class, 5000);

		ICapabilityFactory monitoringFactory = getOsgiService(ICapabilityFactory.class, "capability=monitoring", 5000);
		Assert.assertNotNull(monitoringFactory);
	}

	/**
	 * Check a TransportAlarm is transformed into a ResourceAlarm
	 */
	@Test
	public void checkTransportAlarmTriggersResourceAlarmTest() {

		initBundles();

		try {

			InitializerTestHelper.removeResources(resourceManager);

			// register this as ResourceAlarm listener
			int registrationNum = registerAsResourceAlarmListener(this);

			// create resource with monitoring capability and connections capab
			IResource resource = resourceManager.createResource(createResourceDescriptorWithConnectionsAndMonitoring());

			// create session
			IProtocolSessionManager sessionManager = protocolManager.getProtocolSessionManagerWithContext(resource.getResourceIdentifier().getId(),
					newWonesysSessionContextMock());

			// start resource
			resourceManager.startResource(resource.getResourceIdentifier());

			receivedEvents.clear();

			// generate Alarm
			generateAlarm(sessionManager.obtainSessionByProtocol("wonesys", false));

			// check ResourceAlarm has arrived in less than 30s
			synchronized (lock) {
				try {
					lock.wait(60000);
					Assert.assertFalse(receivedEvents.isEmpty());
				} catch (InterruptedException e) {
					Assert.fail("Interrupted while waiting for events");
				}
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

		} catch (ResourceException e1) {
			e1.printStackTrace();
			Assert.fail(e1.getLocalizedMessage());
		} catch (ProtocolException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}
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
