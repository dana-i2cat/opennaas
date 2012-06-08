package org.opennaas.itests.router.staticroute;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeTestHelper;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
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
import org.opennaas.extensions.itests.helpers.InitializerTestHelper;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capability.staticroute.StaticRouteCapability;
import org.opennaas.itests.router.TestsConstants;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;

/**
 * 
 * @author Jordi Puig
 * @author Adrian Rosello
 * 
 */
@RunWith(JUnit4TestRunner.class)
public class StaticRouteIntegrationTest {

	protected static final String	RESOURCE_INFO_NAME	= "Static Route Test";

	protected ICapability			iStaticRouteCapability;
	protected IResource				routerResource;

	@Inject
	protected IResourceManager		resourceManager;

	@Inject
	protected IProtocolManager		protocolManager;

	@Inject
	protected BundleContext			bundleContext;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.protocols.netconf)")
	private BlueprintContainer		netconfService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.repository)")
	private BlueprintContainer		routerRepoService;

	private static final Log		log					= LogFactory
																.getLog(StaticRouteIntegrationTest.class);

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-router", "opennaas-junos"),
				includeTestHelper(),
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
		Assert.assertFalse(routerResource.getCapabilities().isEmpty());
		Assert.assertNotNull(routerResource.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE)));
		Assert.assertNotNull(routerResource.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.STATIC_ROUTE_CAPABILITY_TYPE)));
		stopResource();
		Assert.assertTrue(resourceManager.listResources().isEmpty());
	}

	/**
	 * Test to check create static route method
	 */
	@Test
	public void createStaticRouteTest()
			throws ProtocolException, ResourceException {
		startResource();

		StaticRouteCapability staticRouteCapability = (StaticRouteCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.STATIC_ROUTE_CAPABILITY_TYPE));
		staticRouteCapability.createStaticRoute("0.0.0.0", "0.0.0.0", "192.168.1.1");

		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(TestsConstants.QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());

		stopResource();
	}

	@Before
	public void initBundles() throws ResourceException {
		InitializerTestHelper.removeResources(resourceManager);
		log.info("INFO: Initialized!");
	}

	/**
	 * Start router resource with 2 capabilities -> staticroute & queue
	 * 
	 * @throws ResourceException
	 * @throws ProtocolException
	 */
	protected void startResource() throws ResourceException, ProtocolException {
		// Add Static Route Capability Descriptor
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor staticrouteCapabilityDescriptor = ResourceHelper.newCapabilityDescriptor(
				TestsConstants.ACTION_NAME,
				TestsConstants.CAPABILIY_VERSION,
				TestsConstants.STATIC_ROUTE_CAPABILITY_TYPE,
				TestsConstants.CAPABILITY_URI);
		lCapabilityDescriptors.add(staticrouteCapabilityDescriptor);

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
