package org.opennaas.itests.sdnnetwork;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.List;

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
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;
import org.opennaas.extensions.sdnnetwork.capability.ofprovision.IOFProvisioningNetworkCapability;
import org.opennaas.extensions.sdnnetwork.capability.ofprovision.OFProvisioningNetworkCapability;
import org.opennaas.extensions.sdnnetwork.driver.internal.actionsets.SDNNetworkInternalActionsetImplementation;
import org.opennaas.extensions.sdnnetwork.model.SDNNetworkOFFlow;
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

	private final static Log	log					= LogFactory.getLog(SDNNetworkOSGIIntegrationTest.class);

	private static final String	NET_RESOURCE_TYPE	= "sdnnetwork";
	private static final String	NET_RESOURCE_NAME	= "net1";

	private static final String	WS_URI				= "http://localhost:8888/opennaas/" + NET_RESOURCE_TYPE + "/" + NET_RESOURCE_NAME + "/" + OFProvisioningNetworkCapability.CAPABILITY_TYPE;
	private static final String	WS_USERNAME			= "admin";
	private static final String	WS_PASSWORD			= "123456";

	/**
	 * Make sure blueprint for org.opennaas.extensions.sdnnetwork bundle has finished its initialization
	 */
	@SuppressWarnings("unused")
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.sdnnetwork)", timeout = 20000)
	private BlueprintContainer	sdnNetworkBlueprintContainer;

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

	private ResourceDescriptor	resourceDescriptor;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-sdn-network", "itests-helpers"),
				noConsole(),
				keepRuntimeFolder());
	}

	@Before
	public void initializeDescriptor() {
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(null, NET_RESOURCE_TYPE, NET_RESOURCE_NAME, null);
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(ResourceHelper.newCapabilityDescriptor(SDNNetworkInternalActionsetImplementation.ACTIONSET_ID, "1.0.0",
				OFProvisioningNetworkCapability.CAPABILITY_TYPE, null));
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);
		this.resourceDescriptor = resourceDescriptor;
	}

	// TODO create switch resource before.

	@Before
	@After
	public void clearRM() throws ResourceException {
		resourceManager.destroyAllResources();
	}

	@Test
	public void resourceWorkflowTest() throws Exception {

		IResource resource = resourceManager.createResource(resourceDescriptor);
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

		IResource resource = resourceManager.createResource(resourceDescriptor);
		resourceManager.startResource(resource.getResourceIdentifier());

		IOFProvisioningNetworkCapability capab = (IOFProvisioningNetworkCapability) resource
				.getCapabilityByInterface(IOFProvisioningNetworkCapability.class);

		ofProvisioningNetworkCapabilityCheck(capab);
	}

	@Test
	public void ofProvisioningNetworkCapabilityWSTest() throws Exception {

		IResource resource = resourceManager.createResource(resourceDescriptor);
		resourceManager.startResource(resource.getResourceIdentifier());

		IOFProvisioningNetworkCapability capabClient = InitializerTestHelper.createRestClient(WS_URI, IOFProvisioningNetworkCapability.class, null,
				WS_USERNAME, WS_PASSWORD);

		ofProvisioningNetworkCapabilityCheck(capabClient);
	}

	public void ofProvisioningNetworkCapabilityCheck(IOFProvisioningNetworkCapability capab) throws Exception {

		SDNNetworkOFFlow flow1 = generateSampleSDNNetworkOFFlow("flow1", "1", "2");
		SDNNetworkOFFlow flow2 = generateSampleSDNNetworkOFFlow("flow2", "2", "1");

		String flow1Id = capab.allocateOFFlow(flow1);
		Assert.assertTrue("recently alocated flow1 is returned by getAllocatedFlows", capab.getAllocatedFlows().contains(flow1));

		String flow2Id = capab.allocateOFFlow(flow2);
		Assert.assertTrue("recently alocated flow2 is returned by getAllocatedFlows", capab.getAllocatedFlows().contains(flow2));

		capab.deallocateOFFlow(flow1Id);
		Assert.assertFalse("recently dealocated flow1 is NOT returned by getAllocatedFlows", capab.getAllocatedFlows().contains(flow1));
		Assert.assertTrue("alocated flow2 is returned by getAllocatedFlows", capab.getAllocatedFlows().contains(flow2));

		capab.deallocateOFFlow(flow2Id);
		Assert.assertFalse("dealocated flow1 is NOT returned by getAllocatedFlows", capab.getAllocatedFlows().contains(flow1));
		Assert.assertFalse("recently dealocated flow2 is NOT returned by getAllocatedFlows", capab.getAllocatedFlows().contains(flow2));
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

		// TODO create route

		return flow;
	}

}
