package luminis;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import net.i2cat.luminis.protocols.wonesys.alarms.WonesysAlarm;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.events.EventFilter;
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.ILifecycle;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.alarms.ResourceAlarm;
import org.opennaas.core.resources.capability.CapabilityException;
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
public class MonitoringCapabilityTest extends AbstractIntegrationTest implements EventHandler {

	static Log				log				= LogFactory.getLog(MonitoringCapabilityTest.class);

	@Inject
	BundleContext			bundleContext	= null;

	IEventManager			eventManager;
	IResourceManager		resourceManager;
	IProtocolManager		protocolManager;
	IProtocolSessionManager	sessionManager;

	boolean					alarmReceived;
	final Object			lock			= new Object();

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

		ICapabilityFactory monitoringFactory = getOsgiService(ICapabilityFactory.class, "capability=monitoring", 10000);
		Assert.assertNotNull(monitoringFactory);

		String filter = "(&(actionset.name=proteus)(actionset.capability=monitoring)(actionset.version=1.0))";
		IActionSet actionSet = getOsgiService(IActionSet.class, filter, 5000);
		Assert.assertNotNull(actionSet);
	}

	private IResource initResource() throws ResourceException, ProtocolException {

		removeResourcesFromRepo();

		IResource resource = resourceManager.createResource(createResourceDescriptorWithConnectionsAndMonitoring());

		try {
			sessionManager = protocolManager.getProtocolSessionManagerWithContext(resource.getResourceIdentifier().getId(),
					newWonesysSessionContextMock());

			resourceManager.startResource(resource.getResourceIdentifier());

		} catch (ProtocolException e) {
			resourceManager.removeResource(resource.getResourceIdentifier());
			throw e;
		}

		return resource;
	}

	private void removeResourcesFromRepo() throws ResourceException {

		List<IResource> resources = resourceManager.listResourcesByType("roadm");
		for (int i = resources.size() - 1; i >= 0; i--) {
			IResource resource = resources.get(i);
			if (resource.getState().equals(ILifecycle.State.ACTIVE))
				resourceManager.stopResource(resource.getResourceIdentifier());
			resourceManager.removeResource(resource.getResourceIdentifier());
		}
	}

	@Test
	public void test() {

		initBundles();

		try {

			IResource mockResource = initResource();

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

			resourceManager.stopResource(mockResource.getResourceIdentifier());
			resourceManager.removeResource(mockResource.getResourceIdentifier());

		} catch (InterruptedException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		} catch (ProtocolException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		} catch (CapabilityException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		} catch (ResourceException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}

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

		Properties prop = new Properties();
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
			lock.notifyAll();
		}
	}

}
