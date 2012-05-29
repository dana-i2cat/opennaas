package org.opennaas.itests.bod;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeTestHelper;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

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
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.IResourceRepository;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;

@RunWith(JUnit4TestRunner.class)
public class BODRepositoryIntegrationTest
{
	private static final Log	log	= LogFactory.getLog(BODRepositoryIntegrationTest.class);

	@Inject
	private BundleContext		bundleContext;

	@Inject
	@Filter("(type=bod)")
	private IResourceRepository	repository;

	@Inject
	private IResourceManager	resourceManager;

	// @Inject
	// @Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.bod.repository)")
	// private BlueprintContainer bodService;

	// @Inject
	// @Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.bod.capability.l2bod)")
	// private BlueprintContainer l2bodService;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-bod"),
				includeTestHelper(),
				noConsole(),
				keepRuntimeFolder());
	}

	/**
	 * Test to create and remove the resource.
	 */
	@Test
	public void createAndRemoveResourceTest() throws Exception {

		// BoD Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor("bod");

		// Create resource
		IResource resource = repository.createResource(resourceDescriptor);
		Assert.assertFalse(repository.listResources().isEmpty());

		// Remove resource
		repository.removeResource(resource.getResourceIdentifier().getId());
		Assert.assertTrue(repository.listResources().isEmpty());
	}

	/**
	 * Test to start and stop the resource.
	 */
	@Test
	public void StartAndStopResourceTest() throws Exception {

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

	/**
	 * Test to check if repostitory is accessible from Resource Manager
	 */
	@Test
	public void isResourceAccessibleFromRM() throws Exception {

		/* init services */
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor("bod");

		IResource resource = resourceManager.createResource(resourceDescriptor);

		Assert.assertTrue(repository.listResources().contains(resource));
	}

	/**
	 * At the end of the tests, we empty the repository
	 */
	@After
	public void clearRepository() throws ResourceException {

		log.info("Clearing resource repo");

		List<IResource> toRemove = repository.listResources();

		for (IResource resource : toRemove) {
			if (resource.getState().equals(State.ACTIVE)) {
				repository.stopResource(resource.getResourceIdentifier().getId());
			}
			repository.removeResource(resource.getResourceIdentifier().getId());
		}

		log.info("Resource repo cleared!");
	}
}
