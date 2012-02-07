package org.opennaas.bod.tests.capability.l2bod;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
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
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;

@RunWith(JUnit4TestRunner.class)
public class L2BoDCapabilityIntegrationTest extends AbstractIntegrationTest {

	private static final String	ACTION_NAME			= "dummy";

	private static final String	VERSION				= "1.0";

	private static final String	CAPABILIY_TYPE		= "l2bod";

	private static final String	CAPABILITY_URI		= "mock://user:pass@host.net:2212/mocksubsystem";

	private static final String	RESOURCE_TYPE		= "bod";

	private static final String	RESOURCE_URI		= "user:pass@host.net:2212";

	private static final String	RESOURCE_INFO_NAME	= "L2BoD Test";

	@Inject
	private BundleContext		bundleContext;

	private IResourceManager	resourceManager;

	private ICapability			l2bodCapability;

	private static Log			log					= LogFactory
															.getLog(L2BoDCapabilityIntegrationTest.class);

	// private IResourceRepository resourceRepository = null;

	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

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
	public void initBundles() throws ResourceException {

		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);

		// Get Resource Manager
		resourceManager = getOsgiService(IResourceManager.class, 50000);

		clearRepository();

		log.info("INFO: Initialized!");
	}

	@Test
	/**
	 * Test to check if repostitory is accessible from OSGi.
	 */
	public void isCapabilityAccessibleFromResource() {

		try {

			// L2BoD Capability Descriptor
			List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();
			CapabilityDescriptor capabilityDescriptor = ResourceHelper.newCapabilityDescriptor(ACTION_NAME, VERSION, CAPABILIY_TYPE,
					CAPABILITY_URI);
			lCapabilityDescriptors.add(capabilityDescriptor);

			// BoD Resource Descriptor
			ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, RESOURCE_TYPE, RESOURCE_URI,
					RESOURCE_INFO_NAME);

			// Create resource
			IResource resource = resourceManager.createResource(resourceDescriptor);

			// Start resource
			resourceManager.startResource(resource.getResourceIdentifier());
			Assert.assertTrue(resource.getCapabilities().size() > 0);

			// Stop resource
			resourceManager.stopResource(resource.getResourceIdentifier());

			// Remove resource
			resourceManager.removeResource(resource.getResourceIdentifier());
			Assert.assertTrue(resourceManager.listResources().isEmpty());

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

	/**
	 * At the end of the tests, we empty the repository
	 */
	private void clearRepository() {

		log.info("Clearing resource repo");

		List<IResource> toRemove = resourceManager.listResources();

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

}
