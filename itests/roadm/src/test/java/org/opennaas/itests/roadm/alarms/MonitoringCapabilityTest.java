package org.opennaas.itests.roadm.alarms;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
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
public class MonitoringCapabilityTest implements EventHandler
{
	private final static Log		log		= LogFactory.getLog(MonitoringCapabilityTest.class);

	@Inject
	private BundleContext			bundleContext;

	@Inject
	private IEventManager			eventManager;

	@Inject
	private IResourceManager		resourceManager;

	@Inject
	private IProtocolManager		protocolManager;

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

	private boolean					alarmReceived;
	private final Object			lock	= new Object();

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-luminis", "opennaas-roadm-proteus"),
				noConsole(),
				keepRuntimeFolder());
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
	public void test() throws Exception {

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
			lock.notifyAll();
		}
	}

}
