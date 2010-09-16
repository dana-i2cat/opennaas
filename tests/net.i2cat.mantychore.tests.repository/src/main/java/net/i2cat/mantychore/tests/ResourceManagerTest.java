package net.i2cat.mantychore.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import net.i2cat.mantychore.tests.utils.ConfigurerTestFactory;
import net.i2cat.mantychore.tests.utils.ResourceDescriptorFactory;

import org.apache.felix.karaf.testing.AbstractIntegrationTest;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;

import com.iaasframework.extras.itesthelper.IaaSIntegrationTestsHelper;
import com.iaasframework.resources.core.IResource;
import com.iaasframework.resources.core.IResourceManager;
import com.iaasframework.resources.core.IResourceRepository;
import com.iaasframework.resources.core.ResourceException;
import com.iaasframework.resources.core.ResourceManager;
import com.iaasframework.resources.core.ResourceRepository;
import com.iaasframework.resources.core.capability.CapabilityException;

/**
 * Test the ResourceManager
 * 
 * @author Scott Campbell (CRC)
 * 
 */

@RunWith(JUnit4TestRunner.class)
public class ResourceManagerTest extends AbstractIntegrationTest {

	Logger						logger			= Logger.getLogger(ResourceManagerTest.class);

	@Inject
	private BundleContext		bundleContext	= null;
	/* The class under test */
	private IResourceManager	resourceManager	= null;

	private IResourceRepository	repository		= null;

	private String				typeConfig		= "junos";

	IResource					resource		= null;

	@Configuration
	public static Option[] configuration() {

		return ConfigurerTestFactory.newResourceManagerTest();
	}

	@Before
	public void setup() {
		logger.debug("Setting up for the test");

		// Register the mock ActionSet Service to the OSGI Registry
		// registerResourceRepository(typeConfig);

		// get the module factory from the registry;
		resourceManager = getOsgiService(IResourceManager.class, 20000);

		/* create a necessary repository */
		// repository = getOsgiService(IResourceRepository.class, "type=" +
		// typeConfig, 20000);

	}

	private void registerResourceRepository(String type) {

		ResourceRepository repo = new ResourceRepository(type);
		Properties serviceProperties = new Properties();
		serviceProperties.put("type", type);

		logger.info("Manually publishing Repository " + type);
		bundleContext.registerService(IResourceRepository.class.getName(), repo, serviceProperties);
	}

	@Test
	public void testAll() throws CapabilityException {
		IaaSIntegrationTestsHelper.listBundles(bundleContext);
		bundleContextShouldNotBeNull();
		testServicesRegistered();
		testRepositoriesAdded();
		/* testing operations */
		logger.info("Testing operations...");
		testKeepAliveOperation();
	}

	private void testKeepAliveOperation() {
		// TODO Auto-generated method stub

	}

	private void bundleContextShouldNotBeNull() {
		assertNotNull(bundleContext);
	}

	private void testServicesRegistered() {
		assertNotNull(resourceManager);
		assertNotNull(repository);
	}

	private void testRepositoriesAdded() {
		ResourceManager manager = (ResourceManager) resourceManager;
		assertEquals(manager.getResourceRepositories().size(), 1);

		// System.out.println("Resource Repositories:");
		// for(int i=0; i<manager.getResourceRepositories().size(); i++) {
		// IResourceRepository repo = manager.getResourceRepositories().get(i);
		// System.out.println("Repository " + i + ": " +
		// repo.getResourceType());
		// }

	}

	private void testCreateResource() {
		try {
			resource = resourceManager.createResource((new ResourceDescriptorFactory()).newInstanceDescriptorJunOS());

		} catch (ResourceException e) {
			Assert.fail();
		}
	}
}
