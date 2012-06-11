package org.opennaas.itests.roadm.alarms;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import javax.inject.Inject;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.opennaas.extensions.roadm.wonesys.protocols.WonesysProtocolSession;
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

@SuppressWarnings("unused")
@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class CompleteAlarmsWorkflowTest
{
	private static final Log	log				= LogFactory.getLog(CompleteAlarmsWorkflowTest.class);

	@Inject
	private BundleContext		bundleContext;

	private List<Event>			receivedEvents	= new ArrayList<Event>();
	private final Object		lock			= new Object();

	@Inject
	private IEventManager		eventManager;

	@Inject
	private IResourceManager	resourceManager;

	@Inject
	private IProtocolManager	protocolManager;

	@Inject
	private IAlarmsRepository	alarmsRepo;

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
				noConsole(),
				keepRuntimeFolder());
	}

	private TestInitInfo setUp() throws ResourceException, ProtocolException {

		// clear resource repo
		List<IResource> resources = resourceManager.listResources();
		for (IResource resource : resources) {
			if (resource.getState().equals(ILifecycle.State.ACTIVE))
				resourceManager.stopResource(resource.getResourceIdentifier());
			resourceManager.removeResource(resource.getResourceIdentifier());
		}

		IResource resource = resourceManager.createResource(createResourceDescriptorWithMonitoring());

		// add SessionContext
		IProtocolSessionManager sessionManager = protocolManager.getProtocolSessionManagerWithContext(resource.getResourceIdentifier().getId(),
				createWonesysSessionContextMock());

		// start resource
		resourceManager.startResource(resource.getResourceIdentifier());

		// create session
		IProtocolSession session = sessionManager.obtainSessionByProtocol("wonesys", false);

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
		alarmsRepo.clear();
	}

	@Test
	public void completeAlarmsWorkflowTest() throws Exception {

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
