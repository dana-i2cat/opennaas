package net.i2cat.mantychore.repository.tests;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.System;
import net.i2cat.nexus.resources.ILifecycle.State;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.ResourceNotFoundException;
import net.i2cat.nexus.resources.action.ActionResponse;
import net.i2cat.nexus.resources.action.IAction;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.command.Response;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;
import net.i2cat.nexus.resources.protocol.IProtocolManager;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;
import net.i2cat.nexus.resources.queue.QueueConstants;
import net.i2cat.nexus.resources.queue.QueueResponse;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;

@RunWith(JUnit4TestRunner.class)
public class MantychoreRepositoryIntegrationTest extends AbstractIntegrationTest {
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

	static Log			log				= LogFactory
													.getLog(MantychoreRepositoryIntegrationTest.class);

	IResourceManager	resourceManager;

	@Inject
	BundleContext		bundleContext	= null;

	@Configuration
	public static Option[] configure() {

		Option[] options = combine(
				IntegrationTestsHelper.getMantychoreTestOptions(),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.tests.helper")
				// , vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
				);
		return options;
	}

	/**
	 * Configure the protocol to connect
	 */
	private ProtocolSessionContext newSessionContextNetconf() {
		String uri = java.lang.System.getProperty("protocol.uri");
		if (uri == null || uri.equals("${protocol.uri}")) {
			uri = "mock://user:pass@host.net:2212/mocksubsystem";
		}

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();

		protocolSessionContext.addParameter(
				ProtocolSessionContext.PROTOCOL_URI, uri);
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"netconf");
		// ADDED
		return protocolSessionContext;
	}

	public void createProtocolForResource(String resourceId) throws ProtocolException {
		IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 5000);
		protocolManager.getProtocolSessionManagerWithContext(resourceId, newSessionContextNetconf());

	}

	@Before
	public void initBundles() throws ResourceException {
		log.info("Waiting to load all bundles");
		/* Wait for the activation of all the bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		log.info("Loaded all bundles");

		/* init capability */

		log.info("This is running inside Equinox. With all configuration set up like you specified. ");

		resourceManager = getOsgiService(IResourceManager.class, 50000);

		clearRepo();

		log.info("INFO: Initialized!");

	}

	@After
	public void clearRepo() {

		log.info("Clearing resource repo");

		IResource[] toRemove = new IResource[resourceManager.listResources().size()];
		toRemove = resourceManager.listResources().toArray(toRemove);

		for (IResource resource : toRemove) {
			try {
				if (resource.getState().equals(State.ACTIVE)) {
					resourceManager.stopResource(resource.getResourceIdentifier());
				}
				resourceManager.removeResource(resource.getResourceIdentifier());
			} catch (ResourceException e) {
				log.error("Failed to remove resource " + resource.getResourceIdentifier().getId() + " from repository.");
				Assert.fail(e.getLocalizedMessage());
			}
		}

		log.info("Resource repo cleared!");
	}

	@Test
	public void createAndRemoveResourceTest() {

		clearRepo();

		ResourceDescriptor resourceDescriptor = RepositoryHelper.newResourceDescriptor("router");

		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(RepositoryHelper.newChassisCapabilityDescriptor());
		capabilityDescriptors.add(RepositoryHelper.newQueueCapabilityDescriptor());

		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		try {
			IResource resource = resourceManager.createResource(resourceDescriptor);
			Assert.assertFalse(resourceManager.listResources().isEmpty());

			// createProtocolForResource(resource.getResourceIdentifier().getId());

			resourceManager.removeResource(resource.getResourceIdentifier());
			Assert.assertTrue(resourceManager.listResources().isEmpty());

		} catch (Exception e) {
			clearRepo();
			log.error("Exception!! ", e);
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void StartAndStopResourceTest() {

		clearRepo();

		ResourceDescriptor resourceDescriptor = RepositoryHelper.newResourceDescriptor("router");
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(RepositoryHelper.newChassisCapabilityDescriptor());
		capabilityDescriptors.add(RepositoryHelper.newQueueCapabilityDescriptor());

		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		try {

			/* create resource */
			IResource resource = resourceManager.createResource(resourceDescriptor);

			Assert.assertNotNull(resource.getResourceIdentifier());
			Assert.assertNotNull(resource.getResourceDescriptor());
			Assert.assertTrue(resource.getCapabilities().isEmpty());
			Assert.assertNull(resource.getModel());
			Assert.assertNull(resource.getProfile());

			Assert.assertFalse(resourceManager.listResources().isEmpty());

			createProtocolForResource(resource.getResourceIdentifier().getId());

			/* start resource */
			resourceManager.startResource(resource.getResourceIdentifier());
			Assert.assertFalse(resource.getCapabilities().isEmpty());
			Assert.assertNotNull(resource.getModel()); // this proves bootstrapper has been executed
			// Assert.assertNotNull(resource.getProfile());

			/* stop resource */
			resourceManager.stopResource(resource.getResourceIdentifier());

			Assert.assertNotNull(resource.getResourceIdentifier());
			Assert.assertNotNull(resource.getResourceDescriptor());
			Assert.assertTrue(resource.getCapabilities().isEmpty());
			Assert.assertNull(resource.getModel());
			// Assert.assertNull(resource.getProfile());

			// Assert.assertFalse(resourceManager.listResources().isEmpty());

			/* remove resource */
			IResourceIdentifier resourceIdentifier = resource.getResourceIdentifier();
			resourceManager.removeResource(resource.getResourceIdentifier());

			Assert.assertTrue(resource.getCapabilities().isEmpty());
			Assert.assertNull(resource.getModel());
			Assert.assertNull(resource.getProfile());
			boolean exist = true;
			try {
				resourceManager.getResource(resourceIdentifier);
			} catch (ResourceException e) {
				exist = false;
			}
			Assert.assertFalse(exist);

		} catch (Exception e) {
			clearRepo();
			log.error("Exception!! ", e);
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void operationWithResourceTest() {

		clearRepo();

		ResourceDescriptor resourceDescriptor = RepositoryHelper.newResourceDescriptor("router");

		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(RepositoryHelper.newChassisCapabilityDescriptor());
		capabilityDescriptors.add(RepositoryHelper.newQueueCapabilityDescriptor());

		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		try {
			IResource resource = resourceManager.createResource(resourceDescriptor);
			resource.setModel(new ComputerSystem());
			createProtocolForResource(resource.getResourceIdentifier().getId());

			resourceManager.startResource(resource.getResourceIdentifier());

			ICapability chassisCapability = getCapability(resource.getCapabilities(), "chassis");
			if (chassisCapability == null)
				Assert.fail("Capability not found");
			ICapability queueCapability = getCapability(resource.getCapabilities(), "queue");
			if (queueCapability == null)
				Assert.fail("Capability not found");

			Response resp = (Response) chassisCapability.sendMessage(ActionConstants.GETCONFIG, null);
			QueueResponse queueResponse = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);

			Assert.assertTrue(queueResponse.getResponses().size() == 1);
			Assert.assertTrue(queueResponse.getPrepareResponse().getStatus() == ActionResponse.STATUS.OK);
			Assert.assertTrue(queueResponse.getConfirmResponse().getStatus() == ActionResponse.STATUS.OK);
			Assert.assertTrue(queueResponse.getRestoreResponse().getStatus() == ActionResponse.STATUS.PENDING);

			ActionResponse actionResponse = queueResponse.getResponses().get(0);
			Assert.assertEquals(ActionConstants.GETCONFIG, actionResponse.getActionID());
			for (Response response : actionResponse.getResponses()) {
				Assert.assertTrue(response.getStatus() == Response.Status.OK);
			}

			List<IAction> queue = (List<IAction>) queueCapability.sendMessage(QueueConstants.GETQUEUE, null);
			Assert.assertTrue(queue.size() == 0);

			resourceManager.stopResource(resource.getResourceIdentifier());
			resourceManager.removeResource(resource.getResourceIdentifier());

		} catch (Exception e) {
			clearRepo();
			log.error("Exception!!", e);
			Assert.fail(e.getMessage());
		}

	}

	/**
	 * Test to check discovery functionalities. TODO It should be interesting to add some test in order to check if i want to remove resources that in
	 * the updated model doesn't exist
	 */

	@Test
	public void discoveryRouterTest() {

		clearRepo();

		ResourceDescriptor resourceDescriptor = RepositoryHelper.newResourceDescriptor("router");

		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(RepositoryHelper.newChassisCapabilityDescriptor());
		capabilityDescriptors.add(RepositoryHelper.newQueueCapabilityDescriptor());

		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		try {
			IResource resource = resourceManager.createResource(resourceDescriptor);
			resource.setModel(new ComputerSystem());

			/* first test, check that the resource has created all the logical resources and they have an initialized model */
			existLogicalResourcesTest(resourceManager, resource, bundleContext);

			// FIXME TO TEST
			if (!isMock())
				checkUpdatedExistLogicalResourcesTest(resourceManager, resource, bundleContext);

		} catch (Exception e) {
			clearRepo();
			log.error("Exception!!", e);
			Assert.fail(e.getMessage());
		}

	}

	private static boolean isMock() {
		String uri = java.lang.System.getProperty("protocol.uri");
		return (uri == null || uri.equals("${protocol.uri}"));
	}

	private void existLogicalResourcesTest(IResourceManager resourceManager, IResource resource, BundleContext context)
			throws ProtocolException, ResourceException {
		createProtocolForResource(resource.getResourceIdentifier().getId());
		resourceManager.startResource(resource.getResourceIdentifier());

		/* check all resources loaded */
		List<String> nameLogicalRouters = getLogicalRoutersFromModel(resource);

		/* all the resources were created correctly */
		for (String nameRouter : nameLogicalRouters) {
			IResourceIdentifier resourceIdentifier = null;
			try {
				resourceIdentifier = resourceManager.getIdentifierFromResourceName("router", nameRouter);
			} catch (ResourceNotFoundException exception) {
				Assert.fail("Resource " + nameRouter + " was not found");
			}
			// Assert.assertNotNull(resourceManager.getResource(resourceIdentifier).getModel());
		}
		/* Restore configuration */
		resourceManager.stopResource(resource.getResourceIdentifier());
		resourceManager.removeResource(resource.getResourceIdentifier());

	}

	private void checkUpdatedExistLogicalResourcesTest(IResourceManager resourceManager, IResource resource, BundleContext context)
			throws ProtocolException, ResourceException {

		addNewLogicalRouterInRouter(resource);

		resourceManager.startResource(resource.getResourceIdentifier());

		/* check all resources loaded */
		List<String> nameLogicalRouters = getLogicalRoutersFromModel(resource);

		/* all the resources were created correctly */
		for (String nameRouter : nameLogicalRouters) {
			IResourceIdentifier resourceIdentifier = null;
			try {
				resourceIdentifier = resourceManager.getIdentifierFromResourceName("router", nameRouter);
			} catch (ResourceNotFoundException exception) {
				Assert.fail("Resource " + nameRouter + " was not found");
			}
			Assert.assertNotNull(resourceManager.getResource(resourceIdentifier).getModel());
		}

		/* Restore configuration */
		removeLogicalRouterInRouter(resource);
		resourceManager.stopResource(resource.getResourceIdentifier());
		resourceManager.removeResource(resource.getResourceIdentifier());

	}

	private void addNewLogicalRouterInRouter(IResource resource)
			throws ProtocolException, ResourceException {
		ICapability chassisCapability = getCapability(resource.getCapabilities(), "chassis");
		if (chassisCapability == null)
			Assert.fail("Capability not found");
		ICapability queueCapability = getCapability(resource.getCapabilities(), "queue");
		if (queueCapability == null)
			Assert.fail("Capability not found");
		Response resp = (Response) chassisCapability.sendMessage(ActionConstants.CREATELOGICALROUTER, "routerTestRepository");
		QueueResponse queueResponse = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);

	}

	private void removeLogicalRouterInRouter(IResource resource)
			throws ProtocolException, ResourceException {
		ICapability chassisCapability = getCapability(resource.getCapabilities(), "chassis");
		if (chassisCapability == null)
			Assert.fail("Capability not found");
		ICapability queueCapability = getCapability(resource.getCapabilities(), "queue");
		if (queueCapability == null)
			Assert.fail("Capability not found");
		Response resp = (Response) chassisCapability.sendMessage(ActionConstants.DELETELOGICALROUTER, "routerTestRepository");
		QueueResponse queueResponse = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);

	}

	private List<String> getLogicalRoutersFromModel(IResource resource) {
		ComputerSystem router = (ComputerSystem) resource.getModel();

		ArrayList<String> nameRouters = new ArrayList<String>();
		List<System> logicalRouters = router.getSystems();
		for (System logicalRouter : logicalRouters) {
			nameRouters.add(logicalRouter.getName());
		}
		return nameRouters;
	}

	public ICapability getCapability(List<ICapability> capabilities, String type) {
		for (ICapability capability : capabilities) {
			if (capability.getCapabilityInformation().getType().equals(type)) {
				return capability;
			}
		}
		return null;
	}

}
