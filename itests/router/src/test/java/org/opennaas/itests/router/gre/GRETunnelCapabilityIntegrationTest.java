package org.opennaas.itests.router.gre;

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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capability.gretunnel.IGRETunnelCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.GRETunnelConfiguration;
import org.opennaas.extensions.router.model.GRETunnelEndpoint;
import org.opennaas.extensions.router.model.GRETunnelService;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;
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

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class GRETunnelCapabilityIntegrationTest {

	/**
	 *
	 */
	private static Log					log					= LogFactory
																	.getLog(GRETunnelCapabilityIntegrationTest.class);

	protected static final String		TUNNEL_NAME			= "gr-1/0/3.1";
	protected static final String		IPv4_ADDRESS		= "192.168.32.1/24";
	protected static final String		IP_SOURCE			= "147.56.89.62";
	protected static final String		IP_DESTINY			= "193.45.23.1";
	private static final String			IPv6_ADDRESS		= "2001:43:F3::12/64";

	protected IQueueManagerCapability	queueCapability;
	protected IGRETunnelCapability		greTunnelCapability;
	private static final String			RESOURCE_INFO_NAME	= "GRE Integration Test";

	private IResource					routerResource;

	@Inject
	private BundleContext				bundleContext;

	@Inject
	protected IResourceManager			resourceManager;

	@Inject
	private IProtocolManager			protocolManager;

	@Inject
	@Filter("(capability=gretunnel)")
	private ICapabilityFactory			gretunnelFactory;

	@SuppressWarnings("unused")
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.repository)", timeout = 20000)
	private BlueprintContainer			routerRepositoryService;

	@SuppressWarnings("unused")
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.queuemanager)", timeout = 20000)
	private BlueprintContainer			queueService;

	@SuppressWarnings("unused")
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.capability.gretunnel)", timeout = 20000)
	private BlueprintContainer			gretunnelService;

	@SuppressWarnings("unused")
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.protocols.netconf)", timeout = 20000)
	private BlueprintContainer			netconfService;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-router", "opennaas-router-driver-junos", "itests-helpers"),
				noConsole(),
				keepRuntimeFolder());
	}

	@Test
	public void createGRETunnelTest() throws CapabilityException, ProtocolException
	{
		log.info("Test createGRETunnel method");
		IGRETunnelCapability greCapability = (IGRETunnelCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.GRE_CAPABILITY_TYPE));

		greCapability
				.createGRETunnel(ParamCreationHelper.getGRETunnelService(TUNNEL_NAME, IPv4_ADDRESS, IP_SOURCE, IP_DESTINY));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));

		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());
	}

	@Test
	public void deleteGRETunnelTest() throws CapabilityException, ProtocolException {
		log.info("Test createGRETunnel method");

		IGRETunnelCapability greCapability = (IGRETunnelCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.GRE_CAPABILITY_TYPE));

		greCapability.deleteGRETunnel(ParamCreationHelper.getGRETunnelService(TUNNEL_NAME, null, null, null, null));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));

		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();

		Assert.assertTrue(queueResponse.isOk());
	}

	@Test
	public void showGRETunnelConfigurationTest() throws CapabilityException, ProtocolException {
		log.info("Test showGRETunnelConfiguration method");

		IGRETunnelCapability greCapability = (IGRETunnelCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.GRE_CAPABILITY_TYPE));

		ComputerSystem routerModel = (ComputerSystem) routerResource.getModel();
		routerModel.addHostedService(
				ParamCreationHelper.getGRETunnelService(TUNNEL_NAME, IPv4_ADDRESS, IP_SOURCE,
						IP_DESTINY));
		routerModel.addHostedService(
				ParamCreationHelper.getGRETunnelService(TUNNEL_NAME, IPv6_ADDRESS, IP_SOURCE,
						IP_DESTINY));

		List<GRETunnelService> greServices = greCapability.showGRETunnelConfiguration();
		Assert.assertEquals(greServices.size(), 2);

		GRETunnelService greService = greServices.get(0);
		Assert.assertEquals(TUNNEL_NAME, greService.getName());

		Assert.assertNotNull(greService.getGRETunnelConfiguration());
		GRETunnelConfiguration greConfig = greService.getGRETunnelConfiguration();
		Assert.assertEquals(IP_SOURCE, greConfig.getSourceAddress());
		Assert.assertEquals(IP_DESTINY, greConfig.getDestinationAddress());

		Assert.assertEquals(greService.getProtocolEndpoint().size(), 1);
		ProtocolEndpoint pE = greService.getProtocolEndpoint().get(0);
		Assert.assertTrue(pE instanceof GRETunnelEndpoint);
		GRETunnelEndpoint gE = (GRETunnelEndpoint) pE;
		Assert.assertEquals(IPUtilsHelper.getAddressFromIP(IPv4_ADDRESS), gE.getIPv4Address());
		Assert.assertEquals(IPUtilsHelper.getPrefixFromIp(IPv4_ADDRESS), IPUtilsHelper.parseLongToShortIpv4NetMask(gE.getSubnetMask()));
		Assert.assertEquals(ProtocolIFType.IPV4, gE.getProtocolIFType());

		greService = greServices.get(1);
		Assert.assertEquals(TUNNEL_NAME, greService.getName());

		Assert.assertNotNull(greService.getGRETunnelConfiguration());
		greConfig = greService.getGRETunnelConfiguration();
		Assert.assertEquals(IP_SOURCE, greConfig.getSourceAddress());
		Assert.assertEquals(IP_DESTINY, greConfig.getDestinationAddress());

		Assert.assertEquals(greService.getProtocolEndpoint().size(), 1);
		pE = greService.getProtocolEndpoint().get(0);
		Assert.assertTrue(pE instanceof GRETunnelEndpoint);
		gE = (GRETunnelEndpoint) pE;
		Assert.assertEquals(IPUtilsHelper.getAddressFromIP(IPv6_ADDRESS), gE.getIPv6Address());
		Assert.assertEquals(IPUtilsHelper.getPrefixFromIp(IPv6_ADDRESS), String.valueOf(gE.getPrefixLength()));
		Assert.assertEquals(ProtocolIFType.IPV6, gE.getProtocolIFType());
	}

	@Test
	public void createGRETunnelv6Test() throws CapabilityException, ProtocolException
	{
		log.info("Test createGRETunnel method");
		IGRETunnelCapability greCapability = (IGRETunnelCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.GRE_CAPABILITY_TYPE));

		greCapability
				.createGRETunnel(ParamCreationHelper.getGRETunnelService(TUNNEL_NAME, IPv6_ADDRESS, IP_SOURCE, IP_DESTINY));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));

		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());
	}

	@Test
	public void testGRECreateTunnelAction() throws CapabilityException, ProtocolException {
		log.info("TEST GRE TUNNEL ACTION");
		IGRETunnelCapability greCapability = (IGRETunnelCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.GRE_CAPABILITY_TYPE));

		greCapability.createGRETunnel(ParamCreationHelper.getGRETunnelService(TUNNEL_NAME, IPv4_ADDRESS, IP_SOURCE, IP_DESTINY));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));

		List<IAction> queue = (List<IAction>) queueCapability.getActions();
		Assert.assertEquals(queue.size(), 1);

		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertEquals(queueResponse.getResponses().size(), 1);

		Assert.assertEquals(queueResponse.getPrepareResponse().getStatus(), ActionResponse.STATUS.OK);
		Assert.assertEquals(queueResponse.getConfirmResponse().getStatus(), ActionResponse.STATUS.OK);
		Assert.assertEquals(queueResponse.getRefreshResponse().getStatus(), ActionResponse.STATUS.OK);
		Assert.assertEquals(queueResponse.getRestoreResponse().getStatus(), ActionResponse.STATUS.PENDING);

		Assert.assertTrue(queueResponse.isOk());

		queue = (List<IAction>) queueCapability.getActions();
		Assert.assertEquals(queue.size(), 0);
	}

	@Test
	public void testGREDeleteTunnelAction() throws CapabilityException, ProtocolException {
		log.info("TEST GRE TUNNEL ACTION");
		IGRETunnelCapability greCapability = (IGRETunnelCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.GRE_CAPABILITY_TYPE));

		// add gre tunnel service to the router model.
		GRETunnelService greTunnelService = new GRETunnelService();
		greTunnelService.setName(TUNNEL_NAME);

		ComputerSystem model = (ComputerSystem) routerResource.getModel();
		model.addHostedService(greTunnelService);
		routerResource.setModel(model);

		greCapability.deleteGRETunnel(ParamCreationHelper.getGRETunnelService(TUNNEL_NAME, null, null, null, null));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));

		List<IAction> queue = (List<IAction>) queueCapability.getActions();
		Assert.assertEquals(queue.size(), 1);

		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertEquals(queueResponse.getResponses().size(), 1);

		Assert.assertEquals(queueResponse.getPrepareResponse().getStatus(), ActionResponse.STATUS.OK);
		Assert.assertEquals(queueResponse.getConfirmResponse().getStatus(), ActionResponse.STATUS.OK);
		Assert.assertEquals(queueResponse.getRefreshResponse().getStatus(), ActionResponse.STATUS.OK);
		Assert.assertEquals(queueResponse.getRestoreResponse().getStatus(), ActionResponse.STATUS.PENDING);

		Assert.assertTrue(queueResponse.isOk());

		queue = (List<IAction>) queueCapability.getActions();
		Assert.assertEquals(queue.size(), 0);
	}

	/**
	 * This test require the dirty model, since the checkParams method would check there's a GreTunnelService in the model.
	 * 
	 * @throws CapabilityException
	 * @throws ProtocolException
	 */
	@Test
	@Ignore
	public void testDeleteUnexistingTunnelAction() throws CapabilityException, ProtocolException {
		log.info("TEST GRE TUNNEL ACTION");
		IGRETunnelCapability greCapability = (IGRETunnelCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.GRE_CAPABILITY_TYPE));

		greCapability.deleteGRETunnel(ParamCreationHelper.getGRETunnelService(TUNNEL_NAME, null, null, null, null));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));

		List<IAction> queue = (List<IAction>) queueCapability.getActions();
		Assert.assertEquals(queue.size(), 1);

		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertEquals(queueResponse.getResponses().size(), 1);

		Assert.assertFalse(queueResponse.isOk());

		queue = (List<IAction>) queueCapability.getActions();
		Assert.assertEquals(queue.size(), 0);
	}

	@Before
	public void initBundle() throws ResourceException, ProtocolException {

		InitializerTestHelper.removeResources(resourceManager);
		log.info("INFO: Initialized!");
		startResource();
	}

	@After
	public void stopBundle() throws ResourceException {
		InitializerTestHelper.removeResources(resourceManager);
		log.info("INFO: Stopped!");
	}

	public void startResource() throws ResourceException, ProtocolException {
		/* initialize model */
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor greCapabilityDescriptor = ResourceHelper.newCapabilityDescriptor(TestsConstants.ACTION_NAME,
				TestsConstants.CAPABILIY_VERSION,
				TestsConstants.GRE_CAPABILITY_TYPE,
				TestsConstants.CAPABILITY_URI);
		lCapabilityDescriptors.add(greCapabilityDescriptor);

		// Add Queue Capability Descriptor
		CapabilityDescriptor queueCapabilityDescriptor = ResourceHelper.newQueueCapabilityDescriptor();
		lCapabilityDescriptors.add(queueCapabilityDescriptor);

		// Router Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, TestsConstants.RESOURCE_TYPE,
				TestsConstants.RESOURCE_URI,
				RESOURCE_INFO_NAME);

		routerResource = resourceManager.createResource(resourceDescriptor);

		// If not exists the protocol session manager, it's created and add the session context
		InitializerTestHelper.addSessionContext(protocolManager, routerResource.getResourceIdentifier().getId(), TestsConstants.RESOURCE_URI);

		// Start resource
		resourceManager.startResource(routerResource.getResourceIdentifier());
	}

}