package luminis;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import junit.framework.Assert;
import net.i2cat.luminis.protocols.wonesys.WonesysProtocolSession;
import net.i2cat.luminis.transports.wonesys.rawsocket.RawSocketTransport;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.ILifecycle;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.alarms.IAlarmsRepository;
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

@RunWith(JUnit4TestRunner.class)
public class CompleteAlarmsWorkflowTest extends AbstractIntegrationTest {
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

	static Log			log				= LogFactory.getLog(CompleteAlarmsWorkflowTest.class);

	@Inject
	BundleContext		bundleContext	= null;

	List<Event>			receivedEvents	= new ArrayList<Event>();
	final Object		lock			= new Object();

	IEventManager		eventManager;
	IResourceManager	resourceManager;
	IProtocolManager	protocolManager;
	IAlarmsRepository	alarmsRepo;

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
		alarmsRepo = getOsgiService(IAlarmsRepository.class, 5000);

		ICapabilityFactory monitoringFactory = getOsgiService(ICapabilityFactory.class, "capability=monitoring", 5000);
		Assert.assertNotNull(monitoringFactory);
	}

	private TestInitInfo setUp() throws ResourceException, ProtocolException {

		initBundles();

		// clear resource repo
		List<IResource> resources = resourceManager.listResources();
		for (IResource resource : resources) {
			if (resource.getState().equals(ILifecycle.State.ACTIVE))
				resourceManager.stopResource(resource.getResourceIdentifier());
			resourceManager.removeResource(resource.getResourceIdentifier());
		}

		IResource resource = resourceManager.createResource(createResourceDescriptorWithMonitoring());

		// create session
		IProtocolSessionManager sessionManager = protocolManager.getProtocolSessionManagerWithContext(resource.getResourceIdentifier().getId(),
				createWonesysSessionContextMock());

		IProtocolSession session = sessionManager.obtainSessionByProtocol("wonesys", false);

		// start resource
		resourceManager.startResource(resource.getResourceIdentifier());

		String transportId = ((WonesysProtocolSession) session).getWonesysTransport().getTransportID();

		alarmsRepo.clear();

		TestInitInfo info = new TestInitInfo();
		info.resource = resource;
		info.sessionManager = sessionManager;
		info.session = (WonesysProtocolSession) session;
		info.transportId = transportId;

		return info;
	}

	private void tearDown(TestInitInfo initInfo) throws ResourceException, ProtocolException {
		resourceManager.stopResource(initInfo.resource.getResourceIdentifier());
		resourceManager.removeResource(initInfo.resource.getResourceIdentifier());
		initInfo.sessionManager.destroyProtocolSession(initInfo.session.getSessionId());
		alarmsRepo.clear();
	}

	@Test
	public void completeAlarmsWorkflowTest() throws ResourceException, ProtocolException {

		TestInitInfo initInfo = setUp();

		// ChannelPlan Changed message
		String chassis = "00";
		String slot = "01";
		String alarmMessage = "FFFF0000" + chassis + slot + "01FF80";

		generateRawSocketEvent(initInfo.transportId, alarmMessage);

		try {

			Thread.sleep(20000);

			List<ResourceAlarm> alarms = alarmsRepo.getResourceAlarms(initInfo.resource.getResourceIdentifier().getId());

			Assert.assertFalse(alarms.isEmpty());
			Assert.assertTrue(alarms.size() == 1);

			// Check alarm is identified as Channel plan changed alarm
			Assert.assertTrue(alarms.get(0).getProperty(ResourceAlarm.ALARM_CODE_PROPERTY).equals("CPLANCHANGED"));

		} catch (Exception e) {
			Assert.fail(e.getLocalizedMessage());
		} finally {
			tearDown(initInfo);
		}
	}

	private ResourceDescriptor createResourceDescriptorWithMonitoring() {
		List<String> capabilities = new ArrayList<String>();
		capabilities.add("monitoring");
		capabilities.add("queue");
		return ResourceDescriptorFactory.newResourceDescriptorProteus("TestProteus", "roadm", capabilities);
	}

	private ProtocolSessionContext createWonesysSessionContextMock() {
		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter("protocol.mock", "true");
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL, "wonesys");
		return protocolSessionContext;
	}

	private void generateRawSocketEvent(String transportId, String message) {

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(RawSocketTransport.MESSAGE_PROPERTY_NAME, message);
		properties.put(RawSocketTransport.TRANSPORT_ID_PROPERTY_NAME, transportId);
		long creationTime = new Date().getTime();
		properties.put(RawSocketTransport.ARRIVAL_TIME_PROPERTY_NAME, creationTime);

		Event event = new Event(RawSocketTransport.MSG_RCVD_EVENT_TOPIC, properties);

		eventManager.publishEvent(event);
		log.debug("RawSocketTransport Event generated! " + message + " at " + creationTime);
	}

	class TestInitInfo {
		public WonesysProtocolSession	session;
		public IProtocolSessionManager	sessionManager;
		public IResource				resource;
		public String					transportId;
	}

}
