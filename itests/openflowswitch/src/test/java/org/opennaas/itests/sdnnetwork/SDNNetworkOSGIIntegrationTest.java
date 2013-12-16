package org.opennaas.itests.sdnnetwork;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.IResourceRepository;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.OpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.FloodlightProtocolSession;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.IFloodlightStaticFlowPusherClient;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.mockup.FloodlightMockClientFactory;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;
import org.opennaas.extensions.sdnnetwork.capability.ofprovision.IOFProvisioningNetworkCapability;
import org.opennaas.extensions.sdnnetwork.capability.ofprovision.OFProvisioningNetworkCapability;
import org.opennaas.extensions.sdnnetwork.driver.internal.actionsets.SDNNetworkInternalActionsetImplementation;
import org.opennaas.extensions.sdnnetwork.model.NetworkConnection;
import org.opennaas.extensions.sdnnetwork.model.Port;
import org.opennaas.extensions.sdnnetwork.model.Route;
import org.opennaas.extensions.sdnnetwork.model.SDNNetworkOFFlow;
import org.opennaas.extensions.sdnnetwork.model.helper.SDNNetworkModelHelper;
import org.opennaas.itests.helpers.InitializerTestHelper;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.service.blueprint.container.BlueprintContainer;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class SDNNetworkOSGIIntegrationTest {

	private final static Log	log						= LogFactory.getLog(SDNNetworkOSGIIntegrationTest.class);

	private static final String	NET_RESOURCE_TYPE		= "sdnnetwork";
	private static final String	NET_RESOURCE_NAME		= "net1";

	private static final String	SWITCH_RESOURCE_TYPE	= "openflowswitch";
	private static final String	SWITCH_RESOURCE_NAME	= "s1";

	private static final String	WS_URI					= "http://localhost:8888/opennaas/" + NET_RESOURCE_TYPE + "/" + NET_RESOURCE_NAME + "/" + OFProvisioningNetworkCapability.CAPABILITY_TYPE;
	private static final String	WS_USERNAME				= "admin";
	private static final String	WS_PASSWORD				= "123456";

	/**
	 * Make sure blueprint for org.opennaas.extensions.sdnnetwork bundle has finished its initialization
	 */
	@SuppressWarnings("unused")
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.sdnnetwork)", timeout = 20000)
	private BlueprintContainer	sdnNetworkBlueprintContainer;

	@Inject
	private IProtocolManager	protocolManager;

	@Inject
	private IResourceManager	resourceManager;

	@Inject
	@Filter("(type=sdnnetwork)")
	private IResourceRepository	sdnNetworkRepository;

	@Inject
	@Filter("(capability=ofprovisionnet)")
	private ICapabilityFactory	capabilityFactory;

	@Inject
	@Filter("(&(actionset.name=internal)(actionset.capability=ofprovisionnet))")
	private IActionSet			actionset;

	private ResourceDescriptor	sdnResourceDescriptor;
	private ResourceDescriptor	ofswitchResourceDescriptor;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-sdnnetwork", "itests-helpers", "opennaas-openflowswitch-driver-floodlight"),
				noConsole(),
				keepRuntimeFolder());
	}

	@Before
	public void initializeSDNDescriptor() {
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(null, NET_RESOURCE_TYPE, null, NET_RESOURCE_NAME);
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(ResourceHelper.newCapabilityDescriptor(SDNNetworkInternalActionsetImplementation.ACTIONSET_ID, "1.0.0",
				OFProvisioningNetworkCapability.CAPABILITY_TYPE, null));
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);
		this.sdnResourceDescriptor = resourceDescriptor;
	}

	@Before
	public void initializeSwitchDescriptor() {
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(null, SWITCH_RESOURCE_TYPE, null, SWITCH_RESOURCE_NAME);
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(ResourceHelper.newCapabilityDescriptor("floodlight", "0.90",
				OpenflowForwardingCapability.CAPABILITY_TYPE, null));
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);
		this.ofswitchResourceDescriptor = resourceDescriptor;

	}

	@Before
	@After
	public void clearRM() throws ResourceException {
		resourceManager.destroyAllResources();

	}

	@Test
	public void resourceWorkflowTest() throws Exception {

		IResource resource = resourceManager.createResource(sdnResourceDescriptor);
		Assert.assertEquals(State.INITIALIZED, resource.getState());
		Assert.assertFalse(resourceManager.listResources().isEmpty());

		resourceManager.startResource(resource.getResourceIdentifier());
		Assert.assertEquals(State.ACTIVE, resource.getState());

		// retrieve capability, will throw exception if unable
		resource.getCapabilityByInterface(IOFProvisioningNetworkCapability.class);

		resourceManager.stopResource(resource.getResourceIdentifier());
		Assert.assertEquals(State.INITIALIZED, resource.getState());

		resourceManager.removeResource(resource.getResourceIdentifier());
		Assert.assertTrue(resourceManager.listResources().isEmpty());
	}

	@Test
	public void ofProvisioningNetworkCapabilityTest() throws Exception {

		IResource sdnResource = resourceManager.createResource(sdnResourceDescriptor);
		resourceManager.startResource(sdnResource.getResourceIdentifier());

		IResource switchResource = resourceManager.createResource(ofswitchResourceDescriptor);
		Map<String, Object> sessionParameters = new HashMap<String, Object>();
		sessionParameters.put(FloodlightProtocolSession.SWITCHID_CONTEXT_PARAM_NAME, "00:00:00:00:00:00:00:01");

		// If not exists the protocol session manager, it's created and add the session context
		InitializerTestHelper.addSessionContextWithSessionParams(protocolManager, switchResource.getResourceIdentifier().getId(),
				"mock://user:pass@host.net:2212/mocksubsystem", "floodlight", sessionParameters);
		resourceManager.startResource(switchResource.getResourceIdentifier());
		prepareClient(switchResource);

		IOFProvisioningNetworkCapability capab = (IOFProvisioningNetworkCapability) sdnResource
				.getCapabilityByInterface(IOFProvisioningNetworkCapability.class);

		ofProvisioningNetworkCapabilityCheck(capab);
	}

	@Test
	public void ofProvisioningNetworkCapabilityWSTest() throws Exception {

		IResource resource = resourceManager.createResource(sdnResourceDescriptor);
		resourceManager.startResource(resource.getResourceIdentifier());

		IResource switchResource = resourceManager.createResource(ofswitchResourceDescriptor);
		Map<String, Object> sessionParameters = new HashMap<String, Object>();
		sessionParameters.put(FloodlightProtocolSession.SWITCHID_CONTEXT_PARAM_NAME, "00:00:00:00:00:00:00:01");

		// If not exists the protocol session manager, it's created and add the session context
		InitializerTestHelper.addSessionContextWithSessionParams(protocolManager, switchResource.getResourceIdentifier().getId(),
				"mock://user:pass@host.net:2212/mocksubsystem", "floodlight", sessionParameters);
		resourceManager.startResource(switchResource.getResourceIdentifier());
		prepareClient(switchResource);

		IOFProvisioningNetworkCapability capabClient = InitializerTestHelper.createRestClient(WS_URI, IOFProvisioningNetworkCapability.class, null,
				WS_USERNAME, WS_PASSWORD);

		ofProvisioningNetworkCapabilityCheck(capabClient);
	}

	public void ofProvisioningNetworkCapabilityCheck(IOFProvisioningNetworkCapability capab) throws Exception {

		SDNNetworkOFFlow flow1 = generateSampleSDNNetworkOFFlow("flow1", "1", "2");
		SDNNetworkOFFlow flow2 = generateSampleSDNNetworkOFFlow("flow2", "2", "1");

		Assert.assertEquals("SDN network shouldn't contain any flow.", 0, capab.getAllocatedFlows().size());
		String flow1Id = capab.allocateOFFlow(flow1);
		Assert.assertEquals("SDN network should contain one flow.", 1, capab.getAllocatedFlows().size());
		String flow2Id = capab.allocateOFFlow(flow2);
		Assert.assertEquals("SDN network should contain two flows.", 2, capab.getAllocatedFlows().size());

		Iterator<SDNNetworkOFFlow> iterator = capab.getAllocatedFlows().iterator();
		while (iterator.hasNext()) {
			SDNNetworkOFFlow flow = iterator.next();
			if (flow.getName().equals(flow1Id))
				Assert.assertTrue("recently alocated flow1 is returned by getAllocatedFlows",
						SDNNetworkModelHelper.compareFlowsWithoutIds(flow1, flow));
			else
				Assert.assertTrue("recently alocated flow2 is returned by getAllocatedFlows",
						SDNNetworkModelHelper.compareFlowsWithoutIds(flow2, flow));
		}

		capab.deallocateOFFlow(flow1Id);
		Assert.assertEquals("SDN network should contain one flow.", 1, capab.getAllocatedFlows().size());
		SDNNetworkOFFlow flow = capab.getAllocatedFlows().iterator().next();
		Assert.assertFalse("recently dealocated flow1 is NOT returned by getAllocatedFlows",
				SDNNetworkModelHelper.compareFlowsWithoutIds(flow1, flow));
		Assert.assertTrue("alocated flow2 is returned by getAllocatedFlows",
				SDNNetworkModelHelper.compareFlowsWithoutIds(flow2, flow));

		capab.deallocateOFFlow(flow2Id);
		Assert.assertEquals("SDN network shouldn't contain any flow.", 0, capab.getAllocatedFlows().size());

	}

	private SDNNetworkOFFlow generateSampleSDNNetworkOFFlow(String name, String inputPort, String outputPort) {

		SDNNetworkOFFlow flow = new SDNNetworkOFFlow();
		flow.setName(name);
		flow.setPriority("1");

		FloodlightOFMatch match = new FloodlightOFMatch();
		match.setIngressPort(inputPort);
		flow.setMatch(match);

		FloodlightOFAction floodlightAction = new FloodlightOFAction();
		floodlightAction.setType("output");
		floodlightAction.setValue(outputPort);

		List<FloodlightOFAction> actions = new ArrayList<FloodlightOFAction>();
		actions.add(floodlightAction);

		flow.setActions(actions);

		Route route = generateSampleRoute();
		flow.setRoute(route);

		return flow;
	}

	private Route generateSampleRoute() {

		List<NetworkConnection> routeConnections = new ArrayList<NetworkConnection>();

		Port firstPort = new Port();
		firstPort.setDeviceId("s1");
		firstPort.setPortNumber("0");

		Port secondPort = new Port();
		secondPort.setDeviceId("s1");
		secondPort.setPortNumber("1");

		NetworkConnection connection01 = new NetworkConnection();
		connection01.setId("0:1");
		connection01.setName("connetion01");
		connection01.setSource(firstPort);
		connection01.setDestination(secondPort);

		routeConnections.add(connection01);

		Route route = new Route();
		route.setId("0");
		route.setNetworkConnections(routeConnections);

		return route;
	}

	private void prepareClient(IResource switchResource) throws ProtocolException {

		IProtocolSessionManager sessionManager = protocolManager.getProtocolSessionManager(switchResource.getResourceIdentifier().getId());

		FloodlightProtocolSession session = (FloodlightProtocolSession) sessionManager.obtainSessionByProtocol(
				FloodlightProtocolSession.FLOODLIGHT_PROTOCOL_TYPE, false);

		session.setClientFactory(new FloodlightMockClientFactory());
		IFloodlightStaticFlowPusherClient client = session.getClientFactory().createClient(session.getSessionContext());
		session.setFloodlightClient(client);

	}

}
