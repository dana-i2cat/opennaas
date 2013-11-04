package org.opennaas.itests.router.ip;

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
import org.opennaas.extensions.router.capability.ip.IIPCapability;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
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
public class IPCapabilityIntegrationTest
{
	private final static Log	log					= LogFactory.getLog(IPCapabilityIntegrationTest.class);

	private final static String	RESOURCE_INFO_NAME	= "IP test";

	protected ICapability		iIPCapability;

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
				includeFeatures("opennaas-router", "opennaas-router-driver-junos", "itests-helpers"),
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

	@Test
	public void testSetIPv4() throws ProtocolException, ResourceException {

		IIPCapability ipCapability = (IIPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.IP_CAPABILITY_TYPE));
		ipCapability.setIPv4(ParamCreationHelper.getLogicalPort(), ParamCreationHelper.getIPProtocolEndPoint());
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());
	}

	@Test
	public void testAddIPv4() throws ProtocolException, ResourceException {
		IIPCapability ipCapability = (IIPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.IP_CAPABILITY_TYPE));
		ipCapability.addIPv4(ParamCreationHelper.getLogicalPort(), ParamCreationHelper.getIPProtocolEndPoint());
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());
	}

	@Test
	public void testAddIPv6() throws ProtocolException, ResourceException {
		IIPCapability ipCapability = (IIPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.IP_CAPABILITY_TYPE));
		ipCapability.addIPv6(ParamCreationHelper.getLogicalPort(), ParamCreationHelper.getIPProtocolEndPointIPv6());
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());
	}

	@Test
	public void removeIPv4() throws ProtocolException, ResourceException {
		IIPCapability ipCapability = (IIPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.IP_CAPABILITY_TYPE));
		ipCapability.removeIPv4(ParamCreationHelper.getLogicalPort(), ParamCreationHelper.getIPProtocolEndPoint());
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());
	}

	@Test
	public void testSetIP() throws ProtocolException, ResourceException {

		// Set IP with IPv4 ProtocolEndpoint

		IIPCapability ipCapability = (IIPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.IP_CAPABILITY_TYPE));
		ipCapability.setIP(ParamCreationHelper.getLogicalPort(), ParamCreationHelper.getIPProtocolEndPoint());
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		// Set IP with IPv6 ProtocolEndpoint

		ipCapability.setIP(ParamCreationHelper.getLogicalPort(), ParamCreationHelper.getIPProtocolEndPointIPv6());

		queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		// Set IP with string ipv4
		ipCapability.setIP(ParamCreationHelper.getLogicalPort(), "192.168.1.12/24");

		queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		// Set IP with string ipv6
		ipCapability.setIP(ParamCreationHelper.getLogicalPort(), "fedc:54:123:ffa1::8/64");

		queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

	}

	@Test(expected = CapabilityException.class)
	public void testSetIPWrongParameters() throws CapabilityException {

		// test IP with empty ProtocolEndpoint - should fail

		IIPCapability ipCapability = (IIPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.IP_CAPABILITY_TYPE));
		ipCapability.setIP(ParamCreationHelper.getLogicalPort(), new IPProtocolEndpoint());

	}

	@Test(expected = CapabilityException.class)
	public void setIPWithUnvalidIP() throws CapabilityException {
		// test IP with invalid ip String - should fail

		IIPCapability ipCapability = (IIPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.IP_CAPABILITY_TYPE));
		ipCapability.setIP(ParamCreationHelper.getLogicalPort(), "invalidIP");
	}

	@Test(expected = CapabilityException.class)
	public void setIPWithEmptyIP() throws CapabilityException {
		// test IP with invalid ip String - should fail

		IIPCapability ipCapability = (IIPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.IP_CAPABILITY_TYPE));
		ipCapability.setIP(ParamCreationHelper.getLogicalPort(), new String());
	}

	@Test
	public void testAddIP() throws ProtocolException, ResourceException {

		// Add IP with IPv4 ProtocolEndpoint

		IIPCapability ipCapability = (IIPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.IP_CAPABILITY_TYPE));
		ipCapability.addIP(ParamCreationHelper.getLogicalPort(), ParamCreationHelper.getIPProtocolEndPoint());
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		// Add IP with IPv6 ProtocolEndpoint

		ipCapability.addIP(ParamCreationHelper.getLogicalPort(), ParamCreationHelper.getIPProtocolEndPointIPv6());

		queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		// Add IP with string ipv4
		ipCapability.addIP(ParamCreationHelper.getLogicalPort(), "192.168.1.12/24");

		queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		// Add IP with string ipv6
		ipCapability.addIP(ParamCreationHelper.getLogicalPort(), "fedc:54:123:ffa1::8/64");

		queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

	}

	@Test(expected = CapabilityException.class)
	public void testAddIPWrongParameters() throws ProtocolException, ResourceException {

		// Add IP with empty ProtocolEndpoint - should fail

		IIPCapability ipCapability = (IIPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.IP_CAPABILITY_TYPE));
		ipCapability.removeIP(ParamCreationHelper.getLogicalPort(), new IPProtocolEndpoint());

	}

	@Test(expected = CapabilityException.class)
	public void addIPWithUnvalidIP() throws CapabilityException {
		// test IP with invalid ip String - should fail

		IIPCapability ipCapability = (IIPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.IP_CAPABILITY_TYPE));
		ipCapability.addIP(ParamCreationHelper.getLogicalPort(), "invalidIP");
	}

	@Test(expected = CapabilityException.class)
	public void addIPWithEmptyIP() throws CapabilityException {
		// test IP with invalid ip String - should fail

		IIPCapability ipCapability = (IIPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.IP_CAPABILITY_TYPE));
		ipCapability.addIP(ParamCreationHelper.getLogicalPort(), new String());
	}

	@Test
	public void testRemoveIP() throws ProtocolException, ResourceException {

		// Remove IP with IPv4 ProtocolEndpoint

		IIPCapability ipCapability = (IIPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.IP_CAPABILITY_TYPE));
		ipCapability.removeIP(ParamCreationHelper.getLogicalPort(), ParamCreationHelper.getIPProtocolEndPoint());
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		// Remove IP with IPv6 ProtocolEndpoint

		ipCapability.removeIP(ParamCreationHelper.getLogicalPort(), ParamCreationHelper.getIPProtocolEndPointIPv6());

		queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		// Remove IP with string ipv4
		ipCapability.removeIP(ParamCreationHelper.getLogicalPort(), "192.168.1.12/24");

		queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		// Remove IP with string ipv6
		ipCapability.removeIP(ParamCreationHelper.getLogicalPort(), "fedc:54:123:ffa1::8/64");

		queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

	}

	@Test(expected = CapabilityException.class)
	public void testRemoveIPWrongParameters() throws ProtocolException, ResourceException {

		// Remove IP with empty ProtocolEndpoint - should fail

		IIPCapability ipCapability = (IIPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.IP_CAPABILITY_TYPE));
		ipCapability.removeIP(ParamCreationHelper.getLogicalPort(), new IPProtocolEndpoint());

	}

	@Test(expected = CapabilityException.class)
	public void removeIPWithUnvalidIP() throws CapabilityException {
		// test IP with invalid ip String - should fail

		IIPCapability ipCapability = (IIPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.IP_CAPABILITY_TYPE));
		ipCapability.removeIP(ParamCreationHelper.getLogicalPort(), "invalidIP");
	}

	@Test(expected = CapabilityException.class)
	public void removeIPWithEmptyIP() throws CapabilityException {
		// test IP with invalid ip String - should fail

		IIPCapability ipCapability = (IIPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.IP_CAPABILITY_TYPE));
		ipCapability.removeIP(ParamCreationHelper.getLogicalPort(), new String());
	}

	@Test
	public void removeIPv6() throws ProtocolException, ResourceException {
		IIPCapability ipCapability = (IIPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.IP_CAPABILITY_TYPE));
		ipCapability.removeIPv6(ParamCreationHelper.getLogicalPort(), ParamCreationHelper.getIPProtocolEndPointIPv6());
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());
	}

	@Test
	public void testSetInterfaceDescription() throws ProtocolException, ResourceException {
		IIPCapability ipCapability = (IIPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.IP_CAPABILITY_TYPE));
		ipCapability.setInterfaceDescription(ParamCreationHelper.getLogicalPort());
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

	}

	public void startResource() throws ResourceException, ProtocolException {
		/* initialize model */
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor ipCapabilityDescriptor = ResourceHelper.newCapabilityDescriptor(TestsConstants.ACTION_NAME,
				TestsConstants.CAPABILIY_VERSION,
				TestsConstants.IP_CAPABILITY_TYPE,
				TestsConstants.CAPABILITY_URI);
		lCapabilityDescriptors.add(ipCapabilityDescriptor);

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
