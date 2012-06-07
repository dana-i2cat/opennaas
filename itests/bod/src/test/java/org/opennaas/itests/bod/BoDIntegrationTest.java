package org.opennaas.itests.bod;

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
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.IResourceRepository;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.extensions.bod.capability.l2bod.L2BoDCapability;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;

@RunWith(JUnit4TestRunner.class)
public class BoDIntegrationTest
{
	private final static Log		log					= LogFactory.getLog(BoDIntegrationTest.class);

	private ProtocolSessionManager	protocolSessionManager;

	@Inject
	private BundleContext			bundleContext;

	@Inject
	private IResourceManager		resourceManager;

	@Inject
	@Filter("(type=bod)")
	private IResourceRepository		repository;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.bod.repository)")
	private BlueprintContainer		repositoryService;

	private static final String		ACTION_NAME			= "dummy";
	private static final String		VERSION				= "1.0";
	private static final String		CAPABILITY_TYPE		= "l2bod";
	private static final String		CAPABILITY_URI		= "mock://user:pass@host.net:2212/mocksubsystem";

	private static final String		RESOURCE_TYPE		= "bod";
	private static final String		RESOURCE_URI		= "user:pass@host.net:2212";
	private static final String		RESOURCE_INFO_NAME	= "BoD Resource";

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-bod"),
				includeTestHelper(),
				noConsole(),
				keepRuntimeFolder());
	}

	/**
	 * Test to check if repostitory is accessible from Resource Manager
	 */
	@Test
	public void isResourceAccessibleFromRM() throws Exception {

		/* init services */
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor("bod");

		IResource resource = resourceManager.createResource(resourceDescriptor);

		Assert.assertTrue(repository.listResources().contains(resource));

		resourceManager.removeResource(resource.getResourceIdentifier());

		Assert.assertFalse(repository.listResources().contains(resource));
	}

	/**
	 * Test to create start, stop and remove the resource.
	 */
	@Test
	public void BoDResourceLifeCycleTest() throws Exception {

		// BoD Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor("bod");

		// Create resource
		IResource resource = repository.createResource(resourceDescriptor);
		Assert.assertFalse(repository.listResources().isEmpty());

		// Start resource
		resource.start();
		Assert.assertTrue(resource.getState().equals(State.ACTIVE));

		// Stop resource
		resource.stop();
		Assert.assertTrue(resource.getState().equals(State.INITIALIZED));

		// Remove resource
		repository.removeResource(resource.getResourceIdentifier().getId());
		Assert.assertTrue(repository.listResources().isEmpty());
	}

	@Test
	public void BoDResourceTest() throws Exception {

		// L2BoD Capability Descriptor

		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		CapabilityDescriptor capabilityDescriptor =
				ResourceHelper.newCapabilityDescriptor(ACTION_NAME, VERSION, CAPABILITY_TYPE, CAPABILITY_URI);
		lCapabilityDescriptors.add(capabilityDescriptor);

		// BoD Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, RESOURCE_TYPE, RESOURCE_URI,
				RESOURCE_INFO_NAME);

		// Create resource
		IResource resource = resourceManager.createResource(resourceDescriptor);

		// Start Resource
		resourceManager.startResource(resource.getResourceIdentifier());
		Assert.assertTrue(resource.getState().equals(State.ACTIVE));

		// Get Capabilities
		List<? extends ICapability> capabilityList = resource.getCapabilities();
		Assert.assertTrue(capabilityList.size() > 0);

		// Get and Execute Actions
		for (ICapability capability : capabilityList) {
			Assert.assertTrue(capability.getCapabilityInformation().getType().equals(CAPABILITY_TYPE));

			IActionSet actionSet = ((L2BoDCapability) capability).getActionSet();
			List<String> actionList = actionSet.getActionNames();
			Assert.assertTrue(actionList.size() > 0);

			for (String actionName : actionList) {
				IAction action = actionSet.obtainAction(actionName);
				ActionResponse actionResponse = action.execute(protocolSessionManager);
				Assert.assertTrue(actionResponse.getStatus().equals(STATUS.OK));

			}
		}
		// Stop Resource
		resourceManager.stopResource(resource.getResourceIdentifier());

		// Remove Resource from ResourceManager
		resourceManager.removeResource(resource.getResourceIdentifier());
		Assert.assertTrue(resourceManager.listResources().isEmpty());
	}

	@After
	public void clearRepository() throws ResourceException {

		log.info("Clearing resource repository");

		List<IResource> toRemove = resourceManager.listResources();

		for (IResource resource : toRemove) {
			if (resource.getState().equals(State.ACTIVE)) {
				resourceManager.stopResource(resource.getResourceIdentifier());
			}
		}
	}
}
