package luminis;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import net.i2cat.luminis.protocols.wonesys.alarms.WonesysAlarm;
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.IResourceRepository;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ResourceIdentifier;
import org.opennaas.core.resources.ResourceNotFoundException;
import org.opennaas.core.resources.alarms.IAlarmsRepository;
import org.opennaas.core.resources.alarms.ResourceAlarm;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;

@RunWith(JUnit4TestRunner.class)
public class AlarmsRepoTest extends AbstractIntegrationTest {
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

	static Log			log				= LogFactory.getLog(AlarmsRepoTest.class);

	@Inject
	BundleContext		bundleContext	= null;

	IEventManager		eventManager;
	IResourceManager	resourceManager;
	IProtocolManager	protocolManager;
	IAlarmsRepository	alarmRepo;

	boolean				alarmReceived;
	final Object		lock			= new Object();

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
		alarmRepo = getOsgiService(IAlarmsRepository.class, 5000);
		IResourceRepository roadmRepo = getOsgiService(IResourceRepository.class, "type=roadm", 5000);
		Assert.assertNotNull(roadmRepo);
	}

	public IResource initResource() throws ResourceException {
		List<String> capabilities = new ArrayList<String>();
		capabilities.add("queue");
		capabilities.add("monitoring");
		ResourceDescriptor desc = ResourceDescriptorFactory.newResourceDescriptorProteus("MockProteus", "roadm", capabilities);

		IResource resource = resourceManager.createResource(desc);

		return resource;
	}

	@Test
	public void alarmsRepositoryTest() {

		initBundles();

		String resourceId = new ResourceIdentifier("proteus").getId();

		ResourceAlarm alarm = generateResourceAlarm(resourceId);
		
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			Assert.fail("Interrupted!");
		}
		
		List<ResourceAlarm> alarms;
		try {
			alarms = alarmRepo.getResourceAlarms(resourceId);
			
			Assert.assertTrue(alarms.contains(alarm));

//			boolean isFound = false;
//			for (ResourceAlarm alarmToCompare : alarms) {
//				if (alarmToCompare.getProperty(ResourceAlarm.RESOURCE_ID_PROPERTY).equals(resourceId)) {
//					isFound = true;
//					break;
//				}
//			}
//			Assert.assertTrue(isFound);

		} catch (ResourceNotFoundException e1) {
			Assert.fail(e1.getMessage());
		}

		alarms = alarmRepo.getAlarms();

		Assert.assertTrue(alarms.contains(alarm));
		
//		boolean isFound = false;
//		for (ResourceAlarm alarmToCompare : alarms) {
//			if (alarmToCompare.getProperty(ResourceAlarm.RESOURCE_ID_PROPERTY).equals(resourceId)) {
//				isFound = true;
//				break;
//			}
//		}

		alarmRepo.clear();

		try {
			Assert.assertTrue(alarmRepo.getResourceAlarms(resourceId).isEmpty());
		} catch (ResourceNotFoundException e) {
			Assert.fail(e.getMessage());
		}
		Assert.assertTrue(alarmRepo.getAlarms().isEmpty());
	}

	@Test
	public void repoReceivesAlarmsCausedByAlarmsInSession() {

		try {
			initBundles();

			IResource resource = initResource();

			IProtocolSessionManager sessionManager = protocolManager.getProtocolSessionManagerWithContext(resource.getResourceIdentifier()
						.getId(),
						newWonesysSessionContextMock());

			resourceManager.startResource(resource.getResourceIdentifier());

			alarmRepo.clear();

			// generate WonesysAlarm
			generateWonesysAlarm(sessionManager.obtainSessionByProtocol("wonesys", false));

			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				Assert.fail("Interrupted!");
			}

			// Check alarmRepo has an alarm (only one)
			List<ResourceAlarm> alarms = alarmRepo.getResourceAlarms(resource.getResourceIdentifier().getId());
			Assert.assertFalse(alarms.isEmpty());
			Assert.assertTrue(alarms.size() == 1);
			
			resourceManager.stopResource(resource.getResourceIdentifier());
			resourceManager.removeResource(resource.getResourceIdentifier());

		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		} catch (ResourceException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		} catch (ProtocolException e) {
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

		log.debug("Generating WonesysAlarm in session: " + session.getSessionId());
		eventManager.publishEvent(alarm);
	}

	/**
	 * Generates a ResourceAlarm
	 * 
	 * @param resourceId
	 */
	private ResourceAlarm generateResourceAlarm(String resourceId) {

		Properties prop = new Properties();
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
