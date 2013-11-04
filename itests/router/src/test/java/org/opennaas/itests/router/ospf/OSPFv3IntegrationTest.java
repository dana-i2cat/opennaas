package org.opennaas.itests.router.ospf;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.IOException;
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
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capability.ospfv3.IOSPFv3Capability;
import org.opennaas.extensions.router.model.OSPFArea.AreaType;
import org.opennaas.extensions.router.model.OSPFProtocolEndpoint;
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
public class OSPFv3IntegrationTest
{
	protected static final String	RESOURCE_INFO_NAME	= "OSPFv3 Test";

	protected ICapability			iOSPFv3Capability;
	protected IResource				routerResource;

	@Inject
	protected IResourceManager		resourceManager;

	@Inject
	protected IProtocolManager		protocolManager;

	@Inject
	protected BundleContext			bundleContext;

	@SuppressWarnings("unused")
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.protocols.netconf)", timeout = 20000)
	private BlueprintContainer		netconfService;

	@SuppressWarnings("unused")
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.repository)", timeout = 20000)
	private BlueprintContainer		routerRepoService;

	private static final Log		log					= LogFactory
																.getLog(OSPFv3IntegrationTest.class);

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-router", "opennaas-router-driver-junos", "itests-helpers"),
				noConsole(),
				keepRuntimeFolder());
	}

	@Test
	/**
	 * Test to check if capability is available from OSGi.
	 */
	public void isCapabilityAccessibleFromResource()
			throws ResourceException, ProtocolException
	{
		startResource();
		Assert.assertTrue(routerResource.getCapabilities().size() > 0);

		stopResource();
		Assert.assertTrue(resourceManager.listResources().isEmpty());
	}

	/**
	 * Test to check activateOSPF method
	 */
	@Test
	public void activateOSPFTest()
			throws ProtocolException, ResourceException
	{
		startResource();

		IOSPFv3Capability ospfv3Capability = (IOSPFv3Capability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.OSPFv3_CAPABILITY_TYPE));
		ospfv3Capability.activateOSPFv3();

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	/**
	 * Test to check OSPFClear Action
	 */
	@Test
	public void addInterfacesInOSPFAreaTest()
			throws ResourceException, ProtocolException, IOException, Exception
	{
		startResource();

		IOSPFv3Capability ospfv3Capability = (IOSPFv3Capability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.OSPFv3_CAPABILITY_TYPE));
		ospfv3Capability.addInterfacesInOSPFv3Area(ParamCreationHelper.getLogicalPorts(new String[] { "fe-0/0/2.1", "fe-0/0/2.2" }),
				ParamCreationHelper.getOSPFArea("0.0.0.0"));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	/**
	 * Test to check clearOSPFConfiguration method
	 */
	@Test
	public void clearOSPFConfigurationTest()
			throws ProtocolException, ResourceException
	{
		startResource();

		IOSPFv3Capability ospfv3Capability = (IOSPFv3Capability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.OSPFv3_CAPABILITY_TYPE));
		ospfv3Capability.clearOSPFv3configuration(ParamCreationHelper.getOSPFv3Service("12345678"));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	/**
	 * Test to check configureOSPFArea method
	 */
	@Test
	public void configureOSPFAreaTest()
			throws IOException, ProtocolException, ResourceException {
		startResource();

		IOSPFv3Capability ospfv3Capability = (IOSPFv3Capability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.OSPFv3_CAPABILITY_TYPE));
		ospfv3Capability.configureOSPFv3Area(ParamCreationHelper.getOSPFAreaConfiguration("0.0.0.0", AreaType.NSSA));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	/**
	 * Test to check configureOSPF method
	 */
	@Test
	public void configureOSPFTest() throws ResourceException, ProtocolException {
		startResource();

		IOSPFv3Capability ospfv3Capability = (IOSPFv3Capability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.OSPFv3_CAPABILITY_TYPE));
		ospfv3Capability.configureOSPFv3(ParamCreationHelper.getOSPFv3Service("12345678"));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	/**
	 * Test to check deactivateOSPF method
	 */
	@Test
	public void deactivateOSPFTest()
			throws ResourceException, ProtocolException
	{
		startResource();

		IOSPFv3Capability ospfv3Capability = (IOSPFv3Capability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.OSPFv3_CAPABILITY_TYPE));
		ospfv3Capability.deactivateOSPFv3();

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	/**
	 * Test to check enableOSPFInterfaceStatus method
	 * 
	 * @TODO Ignored, since there's no OSPFv3 information in mock resource. We need to include it in netconf4j
	 */
	@Test
	@Ignore
	public void enableOSPFInterfaceStatusTest()
			throws ResourceException, ProtocolException {
		startResource();

		IOSPFv3Capability ospfv3Capability = (IOSPFv3Capability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.OSPFv3_CAPABILITY_TYPE));
		ospfv3Capability.enableOSPFv3Interfaces(getInterfaces(new String[] { "fe-0/0/3.45" }));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	/**
	 * Test to check disableOSPFInterfaceStatus method
	 * 
	 * @TODO Ignored, since there's no OSPFv3 information in mock resource. We need to include it in netconf4j
	 */
	@Test
	@Ignore
	public void disableOSPFInterfaceStatusTest()
			throws ResourceException, ProtocolException {
		startResource();

		IOSPFv3Capability ospfv3Capability = (IOSPFv3Capability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.OSPFv3_CAPABILITY_TYPE));
		ospfv3Capability.disableOSPFv3Interfaces(getInterfaces(new String[] { "fe-0/0/3.45" }));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	/**
	 * Test to check getOSPFConfiguration method
	 */
	@Test
	public void getOSPFConfigurationTest()
			throws ResourceException, ProtocolException {
		startResource();

		IOSPFv3Capability ospfv3Capability = (IOSPFv3Capability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.OSPFv3_CAPABILITY_TYPE));
		ospfv3Capability.getOSPFv3Configuration();

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	/**
	 * Test to check removeInterfacesInOSPFArea method
	 */
	@Test
	public void removeInterfacesInOSPFAreaTest()
			throws ResourceException, ProtocolException, IOException, Exception {
		startResource();

		IOSPFv3Capability ospfv3Capability = (IOSPFv3Capability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.OSPFv3_CAPABILITY_TYPE));
		ospfv3Capability.removeInterfacesInOSPFv3Area(ParamCreationHelper.getLogicalPorts(new String[] { "fe-0/0/2.1", "fe-0/0/2.2" }),
				ParamCreationHelper.getOSPFArea("0.0.0.0"));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	/**
	 * Test to check removeOSPFArea method
	 */
	@Test
	public void removeOSPFAreaTest()
			throws ResourceException, ProtocolException, IOException {
		startResource();

		IOSPFv3Capability ospfv3Capability = (IOSPFv3Capability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(TestsConstants.OSPFv3_CAPABILITY_TYPE));
		ospfv3Capability.removeOSPFv3Area(ParamCreationHelper.getOSPFAreaConfiguration("0.0.0.0", AreaType.NSSA));

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	/**
	 * @return List<OSPFProtocolEndpoint>
	 */
	private List<OSPFProtocolEndpoint> getInterfaces(String[] interfaceNames) {
		List<OSPFProtocolEndpoint> ospfPeps = new ArrayList<OSPFProtocolEndpoint>();
		OSPFProtocolEndpoint pep;

		for (String ifaceName : interfaceNames) {
			pep = new OSPFProtocolEndpoint();
			pep.setName(ifaceName);
			ospfPeps.add(pep);
		}

		return ospfPeps;
	}

	@Before
	public void initBundles() throws ResourceException {
		InitializerTestHelper.removeResources(resourceManager);
		log.info("INFO: Initialized!");
	}

	/**
	 * Start router resource with 2 capabilities -> ospf & queue
	 * 
	 * @throws ResourceException
	 * @throws ProtocolException
	 */
	protected void startResource() throws ResourceException, ProtocolException {
		// Add OSPF Capability Descriptor
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor ospfv3CapabilityDescriptor = ResourceHelper.newCapabilityDescriptor(TestsConstants.ACTION_NAME,
				TestsConstants.CAPABILIY_VERSION,
				TestsConstants.OSPFv3_CAPABILITY_TYPE,
				TestsConstants.CAPABILITY_URI);
		lCapabilityDescriptors.add(ospfv3CapabilityDescriptor);

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

	@After
	public void stopBundle() throws ResourceException {
		InitializerTestHelper.removeResources(resourceManager);
		log.info("INFO: Stopped!");
	}

}
