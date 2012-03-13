package org.opennaas.bod.tests.repository;

import java.io.File;
import java.util.List;
import javax.inject.Inject;

import net.i2cat.nexus.tests.ResourceHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.IResourceRepository;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;

import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;

import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

import static net.i2cat.nexus.tests.OpennaasExamOptions.*;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.*;
import static org.ops4j.pax.exam.CoreOptions.*;

@RunWith(JUnit4TestRunner.class)
public class BODRepositoryIntegrationTest
{
	private static final Log	log		= LogFactory.getLog(BODRepositoryIntegrationTest.class);

	@Inject
	private BundleContext		bundleContext;

	@Inject
	@Filter("(type=bod)")
	private IResourceRepository	repository;

	@Inject
	private IResourceManager	resourceManager;

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
