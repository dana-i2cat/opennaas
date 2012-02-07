package org.opennaas.bod.tests.repository;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.List;

import net.i2cat.nexus.tests.IntegrationTestsHelper;
import net.i2cat.nexus.tests.ResourceHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
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
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;

@RunWith(JUnit4TestRunner.class)
public class BODRepositoryIntegrationTest extends AbstractIntegrationTest {

	@Inject
	BundleContext		bundleContext	= null;

	IResourceRepository	repository		= null;

	private static Log	log				= LogFactory
												.getLog(BODRepositoryIntegrationTest.class);

	/* import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption; */

	/**
	 * Initialize the configuration
	 * 
	 * @return
	 */
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

	@Before
	/**
	 * Test to check if repository is accessible from OSGi.
	 */
	public void isRepositoryAccessibleFromContainer() {

		try {

			IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);

			repository =
					getOsgiService(IResourceRepository.class, "type=bod", 50000);

			Assert.assertNotNull(repository);

		} catch (IllegalArgumentException e) {
			log.error("Exception!! ", e.getCause());
			Assert.fail(e.getMessage());
		} catch (RuntimeException e) {
			log.error("Exception!! ", e.getCause());
			Assert.fail(e.getMessage());
		} catch (Exception e) {
			log.error("Exception!! ", e.getCause());
			Assert.fail(e.getMessage());
		}
	}

	@Test
	/**
	 * Test to create and remove the resource.
	 */
	public void createAndRemoveResourceTest() {

		try {

			// BoD Resource Descriptor
			ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor("bod");

			// Create resource
			IResource resource = repository.createResource(resourceDescriptor);
			Assert.assertFalse(repository.listResources().isEmpty());

			// Remove resource
			repository.removeResource(resource.getResourceIdentifier().getId());
			Assert.assertTrue(repository.listResources().isEmpty());

		} catch (ResourceException e) {
			log.error("Exception!! ", e.getCause());
			Assert.fail(e.getMessage());
		} catch (Exception e) {
			log.error("Exception!! ", e.getCause());
			Assert.fail(e.getMessage());
		} finally {
			clearRepository();
		}
	}

	/**
	 * Test to start and stop the resource.
	 */
	@Test
	public void StartAndStopResourceTest() {

		try {

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

		} catch (ResourceException e) {
			log.error("Exception!! ", e.getCause());
			Assert.fail(e.getMessage());
		} catch (Exception e) {
			log.error("Exception!! ", e.getCause());
			Assert.fail(e.getMessage());
		} finally {
			clearRepository();
		}

	}

	@Test
	/**
	 * Test to check if repostitory is accessible from Resource Manager
	 */
	public void isResourceAccessibleFromRM() {

		try {

			/* init services */
			IResourceManager resourceManager = getOsgiService(IResourceManager.class, 50000);

			ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor("bod");

			IResource resource = resourceManager.createResource(resourceDescriptor);

			Assert.assertTrue(repository.listResources().contains(resource));

		} catch (ResourceException e) {
			log.error("Exception!! ", e.getCause());
			Assert.fail(e.getLocalizedMessage());
		} catch (Exception e) {
			log.error("Exception!! ", e.getCause());
			Assert.fail(e.getLocalizedMessage());
		} finally {
			clearRepository();
		}
	}

	/**
	 * At the end of the tests, we empty the repository
	 */
	private void clearRepository() {

		log.info("Clearing resource repo");

		List<IResource> toRemove = repository.listResources();

		for (IResource resource : toRemove) {
			try {
				if (resource.getState().equals(State.ACTIVE)) {
					repository.stopResource(resource.getResourceIdentifier().getId());
				}
				repository.removeResource(resource.getResourceIdentifier().getId());
			} catch (ResourceException e) {
				log.error("Failed to remove resource " + resource.getResourceIdentifier().getId() + " from repository.");
				Assert.fail(e.getLocalizedMessage());
			}
		}

		log.info("Resource repo cleared!");
	}

}
