package luminis;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import net.i2cat.luminis.actionsets.wonesys.ActionConstants;
import net.i2cat.luminis.protocols.wonesys.alarms.WonesysAlarm;
import net.i2cat.nexus.events.EventFilter;
import net.i2cat.nexus.events.IEventManager;
import org.opennaas.core.resources.CorruptStateException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.IncorrectLifecycleStateException;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ResourceIdentifier;
import org.opennaas.core.resources.alarms.ResourceAlarm;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.helpers.MockResource;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.core.resources.queue.QueueConstants;
import org.opennaas.core.resources.queue.QueueResponse;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

@RunWith(JUnit4TestRunner.class)
public class MonitoringCapabilityTest extends AbstractIntegrationTest implements EventHandler {

	static Log			log				= LogFactory.getLog(MonitoringCapabilityTest.class);

	@Inject
	BundleContext		bundleContext	= null;

	IEventManager		eventManager;
	IResourceManager	resourceManager;
	IProtocolManager	protocolManager;

	boolean				alarmReceived;
	final Object		lock			= new Object();

	@Configuration
	public static Option[] configuration() throws Exception {

		Option[] options = combine(
				IntegrationTestsHelper.getLuminisTestOptions(),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.events"),
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

	}

	public IResource initResource() {
		MockResource mockResource = new MockResource();

		List<String> capabilities = new ArrayList<String>();
		capabilities.add("queue");
		capabilities.add("monitoring");
		mockResource.setResourceDescriptor(ResourceDescriptorFactory.newResourceDescriptorProteus("MockProteus", "proteus", capabilities));
		mockResource.setResourceIdentifier(new ResourceIdentifier("proteus", mockResource.getResourceDescriptor().getId()));

		return mockResource;
	}

	// @Test
	public void test() {

		initBundles();

		try {

			IResource mockResource = initResource();

			ICapabilityFactory queueManagerFactory = getOsgiService(ICapabilityFactory.class, "capability=queue", 5000);
			ICapability queueCapability = queueManagerFactory.create(mockResource);

			IProtocolSessionManager sessionManager = protocolManager.getProtocolSessionManagerWithContext(mockResource.getResourceIdentifier()
					.getId(),
					newWonesysSessionContextMock());

			// get Monitoring Capability
			ICapabilityFactory monitoringFactory = getOsgiService(ICapabilityFactory.class, "capability=monitoring", 10000);
			ICapability monitoringCapability = monitoringFactory.create(mockResource);
			monitoringCapability.initialize();

			// register this as a ResourceAlarm listener
			int registrationNum = registerAsResourceAlarmListener(this);

			// launch register action
			alarmReceived = false;
			monitoringCapability.sendMessage(ActionConstants.REGISTER, new Object());

			QueueResponse queueResponse = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);
			Assert.assertTrue(queueResponse.isOk());

			// generate WonesysAlarm
			generateAlarm(sessionManager.obtainSessionByProtocol("wonesys", false));

			// check ResourceAlarm is generated (ProcessAlarmAction generates it)
			synchronized (lock) {
				lock.wait(30000);
				Assert.assertTrue(alarmReceived);
			}

			unregisterAsListener(registrationNum);

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
		} catch (IncorrectLifecycleStateException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		} catch (CorruptStateException e) {
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

	/**
	 * Generates a WonesysAlarm
	 * 
	 * @param session
	 */
	private void generateAlarm(IProtocolSession session) {
		int chassis = 0;
		int slot = 1;

		Properties prop = new Properties();
		prop.put(WonesysAlarm.SESSION_ID_PROPERTY, session.getSessionId());
		prop.put(WonesysAlarm.ALARM_ID_PROPERTY, "80");
		prop.put(WonesysAlarm.ARRIVAL_TIME, new Date().getTime());
		prop.put(WonesysAlarm.CHASSIS_PROPERTY, chassis);
		prop.put(WonesysAlarm.SLOT_PROPERTY, slot);

		WonesysAlarm alarm = new WonesysAlarm(prop);

		log.debug("Generating WonesysAlarm in session: " + session.getSessionId());
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
