package net.i2cat.mantychore.repository.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.System;
import net.i2cat.nexus.tests.IntegrationTestsHelper;
import net.i2cat.nexus.tests.ResourceHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.IResourceRepository;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ResourceNotFoundException;
import org.opennaas.core.resources.ResourceRepository;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.core.resources.queue.QueueConstants;
import org.opennaas.core.resources.queue.QueueResponse;
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
	IResourceRepository resourceRepository;

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
		if (uri == null || uri.equals("${protocol.uri}") || uri.isEmpty()) {
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

		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);

		/* init services */
		resourceManager = getOsgiService(IResourceManager.class, 50000);
		resourceRepository = getOsgiService(IResourceRepository.class, "type=router", 50000);

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

		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor("router");

		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(ResourceHelper.newChassisCapabilityDescriptor());
		capabilityDescriptors.add(ResourceHelper.newQueueCapabilityDescriptor());

		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		try {
			IResource resource = resourceManager.createResource(resourceDescriptor);
			Assert.assertFalse(resourceManager.listResources().isEmpty());

			// createProtocolForResource(resource.getResourceIdentifier().getId());

			resourceManager.removeResource(resource.getResourceIdentifier());
			Assert.assertTrue(resourceManager.listResources().isEmpty());

		} catch (Exception e) {
			log.error("Exception!! ", e);
			Assert.fail(e.getMessage());
		} finally {
			clearRepo();
		}

	}

	@Test
	public void StartAndStopResourceTest() {

		clearRepo();

		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor("router");
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(ResourceHelper.newChassisCapabilityDescriptor());
		capabilityDescriptors.add(ResourceHelper.newQueueCapabilityDescriptor());

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
			log.error("Exception!! ", e);
			Assert.fail(e.getMessage());
		} finally {
			clearRepo();
		}

	}

	@Test
	public void startedResourceModelHasNameTest() {

		clearRepo();

		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor("router");
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(ResourceHelper.newChassisCapabilityDescriptor());
		capabilityDescriptors.add(ResourceHelper.newQueueCapabilityDescriptor());

		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		try {

			/* create resource */
			IResource resource = resourceManager.createResource(resourceDescriptor);
			Assert.assertFalse(resourceManager.listResources().isEmpty());

			createProtocolForResource(resource.getResourceIdentifier().getId());

			/* start resource */
			resourceManager.startResource(resource.getResourceIdentifier());
			Assert.assertFalse(resource.getCapabilities().isEmpty());
			Assert.assertNotNull(resource.getModel()); // this proves bootstrapper has been executed

			Assert.assertTrue(resource.getModel() instanceof ComputerSystem);
			Assert.assertNotNull(((ComputerSystem) resource.getModel()).getName());

		} catch (Exception e) {
			log.error("Exception!! ", e);
			Assert.fail(e.getMessage());
		} finally {
			clearRepo();
		}

	}

	@Test
	public void operationWithResourceTest() {

		clearRepo();

		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor("router");

		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(ResourceHelper.newChassisCapabilityDescriptor());
		capabilityDescriptors.add(ResourceHelper.newQueueCapabilityDescriptor());

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
			log.error("Exception!!", e);
			Assert.fail(e.getMessage());
		} finally {
			clearRepo();
		}

	}

	/**
	 * Test to check discovery functionalities. TODO It should be interesting to add some test in order to check if i want to remove resources that in
	 * the updated model doesn't exist
	 */

	@Test
	public void discoveryRouterTest() {

		clearRepo();

		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor("router");

		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(ResourceHelper.newChassisCapabilityDescriptor());
		capabilityDescriptors.add(ResourceHelper.newQueueCapabilityDescriptor());

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
			log.error("Exception!!", e);
			Assert.fail(e.getMessage());
		} finally {
			clearRepo();
		}

	}
	
	@Test
	public void testPersistedIdentifierIdDoesNotChange() {
		try {

			ResourceDescriptor descriptor = ResourceHelper.newResourceDescriptor("router");
			IResource res1 = resourceRepository.createResource(descriptor);
			
			if (resourceRepository instanceof ResourceRepository){
				//reset repository and load persisted resources
				((ResourceRepository) resourceRepository).init();
				
				try {
					IResource res2 = resourceRepository.getResource(res1.getResourceIdentifier().getId());
					assertEquals(res1.getResourceDescriptor().getId(), res2.getResourceDescriptor().getId());
				} catch (ResourceException e){
					fail("Resource with given ID is not present");
				}
				//already checked that id is the same
			}
			
		} catch (ResourceException e) {
			fail(e.getLocalizedMessage());
		}
	}

	private static boolean isMock() {
		String uri = java.lang.System.getProperty("protocol.uri");
		return (uri == null || uri.equals("${protocol.uri}") || uri.isEmpty());
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
