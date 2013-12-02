package org.opennaas.itests.router.vrrp;

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
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capability.vrrp.IVRRPCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.VRRPGroup;
import org.opennaas.extensions.router.model.VRRPProtocolEndpoint;
import org.opennaas.itests.helpers.InitializerTestHelper;
import org.opennaas.itests.router.TestsConstants;
import org.opennaas.itests.router.helpers.ParamCreationHelper;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;

/**
 * @author Julio Carlos Barrera
 * @author Adrian Rosello
 */
@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class VRRPIntegrationTest {
	private final static Log	log					= LogFactory.getLog(VRRPIntegrationTest.class);

	private final static String	RESOURCE_INFO_NAME	= "VRRP test";

	protected ICapability		iVRRPCapability;

	protected IResource			routerResource;

	@Inject
	private BundleContext		bundleContext;

	@Inject
	protected IResourceManager	resourceManager;

	@Inject
	private IProtocolManager	protocolManager;

	@SuppressWarnings("unused")
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.protocols.netconf)", timeout = 20000)
	private BlueprintContainer	netconfService;

	@SuppressWarnings("unused")
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.repository)", timeout = 20000)
	private BlueprintContainer	routerRepoService;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-router", "opennaas-junos", "itests-helpers"),
				noConsole(),
				keepRuntimeFolder());
	}

	@Before
	public void initBundles() throws ResourceException, ProtocolException {
		InitializerTestHelper.removeResources(resourceManager);
		log.info("INFO: Initialized!");
		startResource();
	}

	@After
	public void stopBundle() throws Exception {
		InitializerTestHelper.removeResources(resourceManager);
		log.info("INFO: Stopped!");
	}

	/**
	 * Start router resource with 2 capabilities -> vrrp & queue
	 * 
	 * @throws ResourceException
	 * @throws ProtocolException
	 */
	protected void startResource() throws ResourceException, ProtocolException {
		// Add VRRP Capability Descriptor
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor vrrpCapabilityDescriptor = ResourceHelper.newCapabilityDescriptor(TestsConstants.ACTION_NAME,
				TestsConstants.CAPABILIY_VERSION,
				TestsConstants.VRRP_CAPABILITY_TYPE,
				TestsConstants.CAPABILITY_URI);
		lCapabilityDescriptors.add(vrrpCapabilityDescriptor);

		// Add Queue Capability Descriptor
		CapabilityDescriptor queueCapabilityDescriptor = ResourceHelper.newQueueCapabilityDescriptor();
		lCapabilityDescriptors.add(queueCapabilityDescriptor);

		// Router Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, TestsConstants.RESOURCE_TYPE,
				TestsConstants.RESOURCE_URI,
				RESOURCE_INFO_NAME);

		// Create resource
		routerResource = resourceManager.createResource(resourceDescriptor);

		// If not exists the protocol session manager, it's created and add the session context
		InitializerTestHelper.addSessionContext(protocolManager, routerResource.getResourceIdentifier().getId(), TestsConstants.RESOURCE_URI);

		// Start resource
		resourceManager.startResource(routerResource.getResourceIdentifier());
	}

	/**
	 * Stop and remove the router resource
	 * 
	 * @throws ResourceException
	 * @throws ProtocolException
	 */
	protected void stopResource() throws ResourceException, ProtocolException {
		// Stop resource
		resourceManager.stopResource(routerResource.getResourceIdentifier());

		// Remove resource
		resourceManager.removeResource(routerResource.getResourceIdentifier());
	}

	@Test
	public void testConfigureVRRP() throws ProtocolException, ResourceException {
		IVRRPCapability vrrpCapability = (IVRRPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.VRRP_CAPABILITY_TYPE));
		vrrpCapability.configureVRRP((VRRPProtocolEndpoint) ParamCreationHelper
				.newParamsVRRPGroupWithOneEndpoint("192.168.1.1", "fe-0/3/2", "192.168.1.100", "255.255.255.0").getProtocolEndpoint().get(0));
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	@Test
	public void testUnconfigureVRRP() throws ProtocolException, ResourceException {
		IVRRPCapability vrrpCapability = (IVRRPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.VRRP_CAPABILITY_TYPE));
		vrrpCapability.unconfigureVRRP((VRRPProtocolEndpoint) ParamCreationHelper
				.newParamsVRRPGroupWithOneEndpoint("192.168.1.1", "fe-0/3/2", "192.168.1.100", "255.255.255.0").getProtocolEndpoint().get(0));
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	@Test
	public void testUpdateVRRPVirtualIPAddress() throws ProtocolException, ResourceException {

		prepareResourceModelforIPv4();

		IVRRPCapability vrrpCapability = (IVRRPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.VRRP_CAPABILITY_TYPE));
		vrrpCapability.updateVRRPVirtualIPAddress((VRRPProtocolEndpoint) ParamCreationHelper
				.newParamsVRRPGroupWithOneEndpoint("192.168.1.1", "fe-0/3/2", "192.168.1.100", "255.255.255.0").getProtocolEndpoint().get(0));
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	@Test
	public void testUpdateVRRPPriority() throws ProtocolException, ResourceException {

		prepareResourceModelforIPv4();

		IVRRPCapability vrrpCapability = (IVRRPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.VRRP_CAPABILITY_TYPE));
		vrrpCapability.updateVRRPPriority((VRRPProtocolEndpoint) ParamCreationHelper
				.newParamsVRRPGroupWithOneEndpoint("192.168.1.1", "fe-0/3/2", "192.168.1.100", "255.255.255.0").getProtocolEndpoint().get(0));
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	@Test(expected = CapabilityException.class)
	public void testConfigureVRRPIPv6WrongParams() throws ProtocolException, ResourceException {
		IVRRPCapability vrrpCapability = (IVRRPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.VRRP_CAPABILITY_TYPE));
		vrrpCapability.configureVRRP((VRRPProtocolEndpoint) ParamCreationHelper
				.newParamsVRRPGroupWithOneEndpointIPv6("fecd:123:a1::4", "f8:34::12", "fe-1/0/1", "fecd:123:a1::5/64").getProtocolEndpoint().get(0));
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	@Test
	public void testConfigureVRRPIPv6() throws ProtocolException, ResourceException {
		IVRRPCapability vrrpCapability = (IVRRPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.VRRP_CAPABILITY_TYPE));
		vrrpCapability.configureVRRP((VRRPProtocolEndpoint) ParamCreationHelper
				.newParamsVRRPGroupWithThreeEndpointIPv6("fecd:123:a1::4", "f8:34::12", "fe-1/0/1", "fecd:123:a1::5/64", "f8:34::13/64", "fecd::/64")
				.getProtocolEndpoint().get(0));
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	@Test
	public void testUnconfigureVRRPIPv6() throws ProtocolException, ResourceException {
		IVRRPCapability vrrpCapability = (IVRRPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.VRRP_CAPABILITY_TYPE));
		vrrpCapability.unconfigureVRRP((VRRPProtocolEndpoint) ParamCreationHelper
				.newParamsVRRPGroupWithOneEndpointIPv6("fecd:123:a1::4", "f8:34::12", "fe-1/0/1", "fecd:123:a1::5/64").getProtocolEndpoint().get(0));
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	@Test
	public void testUpdatePriorityVRRPIPv6() throws ProtocolException, ResourceException {

		prepareResourceModelforIPv6();

		IVRRPCapability vrrpCapability = (IVRRPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.VRRP_CAPABILITY_TYPE));
		vrrpCapability.updateVRRPPriority((VRRPProtocolEndpoint) ParamCreationHelper
				.newParamsVRRPGroupWithOneEndpointIPv6("fecd:123:a1::4", "f8:34::12", "fe-1/0/1", "fecd:123:a1::5/64").getProtocolEndpoint().get(0));
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	@Test
	public void testUpdateIPAddressVRRPIPv6() throws ProtocolException, ResourceException {

		prepareResourceModelforIPv6();

		IVRRPCapability vrrpCapability = (IVRRPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.VRRP_CAPABILITY_TYPE));
		vrrpCapability.updateVRRPVirtualIPAddress((VRRPProtocolEndpoint) ParamCreationHelper
				.newParamsVRRPGroupWithOneEndpointIPv6("fecd:123:a1::4", "f8:34::12", "fe-1/0/1", "fecd:123:a1::5/64").getProtocolEndpoint().get(0));
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	@Test
	public void testUpdateVirtualLinkAddress() throws ProtocolException, ResourceException {

		prepareResourceModelforIPv6();

		IVRRPCapability vrrpCapability = (IVRRPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.VRRP_CAPABILITY_TYPE));
		vrrpCapability.updateVRRPVirtualLinkAddress(ParamCreationHelper
				.newParamsVRRPGroupWithOneEndpointIPv6("fecd:123:a1::4", "f8:34::12", "fe-1/0/1", "fecd:123:a1::5/64"));
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	@Test(expected = CapabilityException.class)
	public void testUpdateVirualLinkAddressWithWrongIP() throws ProtocolException, ResourceException {

		prepareResourceModelforIPv6();

		IVRRPCapability vrrpCapability = (IVRRPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.VRRP_CAPABILITY_TYPE));
		vrrpCapability.updateVRRPVirtualLinkAddress(ParamCreationHelper
				.newParamsVRRPGroupWithOneEndpointIPv6("fecd:123:a1::4", "192.168.1.1", "fe-1/0/1", "fecd:123:a1::5/64"));
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	private void prepareResourceModelforIPv6() {

		ComputerSystem model = new ComputerSystem();
		VRRPGroup group = ParamCreationHelper.newParamsVRRPGroupWithOneEndpointIPv6("fecd:123:a1::4", "f8:34::13", "fe-1/0/1", "fecd:123:a1::5/64");

		model.addHostedService(group);
		routerResource.setModel(model);
	}

	private void prepareResourceModelforIPv4() {

		ComputerSystem model = new ComputerSystem();
		VRRPGroup group = ParamCreationHelper.newParamsVRRPGroupWithOneEndpoint("192.168.1.1", "fe-0/3/2", "192.168.1.100", "255.255.255.0");

		model.addHostedService(group);
		routerResource.setModel(model);
	}
}