package org.opennaas.itests.router;

import static org.junit.Assert.assertEquals;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeTestHelper;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
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
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capability.chassis.IChassisCapability;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.System;
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
public class MantychoreRepositoryIntegrationTest
{
	private final static Log	log	= LogFactory.getLog(MantychoreRepositoryIntegrationTest.class);

	@Inject
	private IResourceManager	resourceManager;

	@Inject
	@Filter("(type=router)")
	private IResourceRepository	resourceRepository;

	@Inject
	private BundleContext		bundleContext;

	@Inject
	private IProtocolManager	protocolManager;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.capability.chassis)")
	private BlueprintContainer	chassisService;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-router", "opennaas-junos"),
				includeTestHelper(),
				noConsole(),
				keepRuntimeFolder());
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
		protocolManager.getProtocolSessionManagerWithContext(resourceId, newSessionContextNetconf());

	}

	@After
	public void clearRepo() throws ResourceException {

		log.info("Clearing resource repo");

		IResource[] toRemove = new IResource[resourceManager.listResources().size()];
		toRemove = resourceManager.listResources().toArray(toRemove);

		for (IResource resource : toRemove) {
			if (resource.getState().equals(State.ACTIVE)) {
				resourceManager.stopResource(resource.getResourceIdentifier());
			}
			resourceManager.removeResource(resource.getResourceIdentifier());
		}

		log.info("Resource repo cleared!");
	}

	@Test
	public void createAndRemoveResourceTest() throws Exception {

		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor("router");

		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(ResourceHelper.newChassisCapabilityDescriptor());
		capabilityDescriptors.add(ResourceHelper.newQueueCapabilityDescriptor());

		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		IResource resource = resourceManager.createResource(resourceDescriptor);
		Assert.assertFalse(resourceManager.listResources().isEmpty());

		resourceManager.removeResource(resource.getResourceIdentifier());
		Assert.assertTrue(resourceManager.listResources().isEmpty());
	}

	@Test
	public void StartAndStopResourceTest() throws Exception {

		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor("router");
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(ResourceHelper.newChassisCapabilityDescriptor());
		capabilityDescriptors.add(ResourceHelper.newQueueCapabilityDescriptor());

		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

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
	}

	@Test
	public void startedResourceModelHasNameTest() throws Exception {

		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor("router");
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(ResourceHelper.newChassisCapabilityDescriptor());
		capabilityDescriptors.add(ResourceHelper.newQueueCapabilityDescriptor());

		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

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
	}

	@Test
	public void operationWithResourceTest() throws Exception {

		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor("router");

		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(ResourceHelper.newChassisCapabilityDescriptor());
		capabilityDescriptors.add(ResourceHelper.newQueueCapabilityDescriptor());

		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		IResource resource = resourceManager.createResource(resourceDescriptor);
		resource.setModel(new ComputerSystem());
		createProtocolForResource(resource.getResourceIdentifier().getId());

		resourceManager.startResource(resource.getResourceIdentifier());

		IChassisCapability chassisCapability = (IChassisCapability) getCapability(resource.getCapabilities(), "chassis");
		if (chassisCapability == null)
			Assert.fail("Capability not found");
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) getCapability(resource.getCapabilities(), "queue");
		if (queueCapability == null)
			Assert.fail("Capability not found");

		chassisCapability.createLogicalRouter(getLogicalRouter("cpe1"));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();

		Assert.assertTrue(queueResponse.getResponses().size() == 1);
		Assert.assertTrue(queueResponse.getPrepareResponse().getStatus() == ActionResponse.STATUS.OK);
		Assert.assertTrue(queueResponse.getConfirmResponse().getStatus() == ActionResponse.STATUS.OK);
		Assert.assertTrue(queueResponse.getRestoreResponse().getStatus() == ActionResponse.STATUS.PENDING);

		ActionResponse actionResponse = queueResponse.getResponses().get(0);
		Assert.assertEquals(ActionConstants.CREATELOGICALROUTER, actionResponse.getActionID());
		for (Response response : actionResponse.getResponses()) {
			Assert.assertTrue(response.getStatus() == Response.Status.OK);
		}

		List<IAction> queue = (List<IAction>) queueCapability.getActions();
		Assert.assertTrue(queue.size() == 0);

		resourceManager.stopResource(resource.getResourceIdentifier());
		resourceManager.removeResource(resource.getResourceIdentifier());
	}

	/**
	 * Test to check discovery functionalities. TODO It should be interesting to add some test in order to check if i want to remove resources that in
	 * the updated model doesn't exist
	 */

	@Test
	public void discoveryRouterTest() throws Exception {

		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor("router");

		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(ResourceHelper.newChassisCapabilityDescriptor());
		capabilityDescriptors.add(ResourceHelper.newQueueCapabilityDescriptor());

		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		IResource resource = resourceManager.createResource(resourceDescriptor);
		resource.setModel(new ComputerSystem());

		/* first test, check that the resource has created all the logical resources and they have an initialized model */
		existLogicalResourcesTest(resourceManager, resource, bundleContext);

		// FIXME TO TEST
		if (!isMock())
			checkUpdatedExistLogicalResourcesTest(resourceManager, resource, bundleContext);
	}

	@Test
	public void testPersistedIdentifierIdDoesNotChange() throws ResourceException {

		ResourceDescriptor descriptor = ResourceHelper.newResourceDescriptor("router");
		IResource res1 = resourceRepository.createResource(descriptor);

		if (resourceRepository instanceof ResourceRepository) {
			// reset repository and load persisted resources
			((ResourceRepository) resourceRepository).init();

			IResource res2 = resourceRepository.getResource(res1.getResourceIdentifier().getId());
			assertEquals(res1.getResourceDescriptor().getId(), res2.getResourceDescriptor().getId());
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
		IChassisCapability chassisCapability = (IChassisCapability) getCapability(resource.getCapabilities(), "chassis");
		if (chassisCapability == null)
			Assert.fail("Capability not found");
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) getCapability(resource.getCapabilities(), "queue");
		if (queueCapability == null)
			Assert.fail("Capability not found");
		chassisCapability.createLogicalRouter(getLogicalRouter("routerTestRepository"));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
	}

	private void removeLogicalRouterInRouter(IResource resource)
			throws ProtocolException, ResourceException {
		IChassisCapability chassisCapability = (IChassisCapability) getCapability(resource.getCapabilities(), "chassis");
		if (chassisCapability == null)
			Assert.fail("Capability not found");
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) getCapability(resource.getCapabilities(), "queue");
		if (queueCapability == null)
			Assert.fail("Capability not found");
		chassisCapability.deleteLogicalRouter(getLogicalRouter("routerTestRepository"));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
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

	private ICapability getCapability(List<? extends ICapability> list, String type) {
		for (ICapability capability : list) {
			if (capability.getCapabilityInformation().getType().equals(type)) {
				return capability;
			}
		}
		return null;
	}

	private ComputerSystem getLogicalRouter(String lrName) {
		ComputerSystem lrModel = new ComputerSystem();
		lrModel.setName(lrName);
		lrModel.setElementName(lrName);
		return lrModel;
	}

}
