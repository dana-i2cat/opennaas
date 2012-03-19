package luminis;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.inject.Inject;

import net.i2cat.luminis.protocols.wonesys.alarms.WonesysAlarm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.blueprint.container.BlueprintContainer;

import static net.i2cat.nexus.tests.OpennaasExamOptions.*;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.*;
import static org.ops4j.pax.exam.CoreOptions.*;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class AlarmsRepoTest
{
	private final static Log	log				= LogFactory.getLog(AlarmsRepoTest.class);

	@Inject
	private BundleContext		bundleContext;

	@Inject
	private IEventManager		eventManager;

	@Inject
	private IResourceManager	resourceManager;

	@Inject
	private IProtocolManager	protocolManager;

	@Inject
	private IAlarmsRepository	alarmRepo;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=net.i2cat.luminis.ROADM.repository)")
	private BlueprintContainer		roadmRepositoryService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.queuemanager)")
	private BlueprintContainer		queueService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=net.i2cat.luminis.capability.monitoring)")
	private BlueprintContainer 		monitoringService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=net.i2cat.luminis.protocols.wonesys)")
	private BlueprintContainer      wonesysProtocolService;

	private boolean				alarmReceived;
	private final Object		lock			= new Object();

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
					   includeFeatures("opennaas-alarms", "opennaas-cim", "opennaas-luminis"),
					   noConsole(),
					   keepRuntimeFolder());
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

		IResource resource = initResource();

		IProtocolSessionManager sessionManager =
			protocolManager.getProtocolSessionManagerWithContext(resource.getResourceIdentifier().getId(),
																 newWonesysSessionContextMock());

		resourceManager.startResource(resource.getResourceIdentifier());

		alarmRepo.clear();

		// generate WonesysAlarm
		generateWonesysAlarm(sessionManager.obtainSessionByProtocol("wonesys", false));

		Thread.sleep(10000);

		// Check alarmRepo has an alarm (only one)
		List<ResourceAlarm> alarms = alarmRepo.getResourceAlarms(resource.getResourceIdentifier().getId());
		Assert.assertFalse(alarms.isEmpty());
		Assert.assertTrue(alarms.size() == 1);

		resourceManager.stopResource(resource.getResourceIdentifier());
		resourceManager.removeResource(resource.getResourceIdentifier());
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
