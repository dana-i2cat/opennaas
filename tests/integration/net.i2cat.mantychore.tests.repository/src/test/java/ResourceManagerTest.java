import java.util.Properties;

import net.i2cat.mantychore.actionsets.junos.actions.GetConfigurationAction;

import org.apache.felix.karaf.testing.AbstractIntegrationTest;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;

import utils.ConfigurerTestFactory;
import utils.ResourceDescriptorFactory;

import com.iaasframework.capabilities.actionset.ActionSetCapabilityClient;
import com.iaasframework.resources.core.IResource;
import com.iaasframework.resources.core.IResourceIdentifier;
import com.iaasframework.resources.core.IResourceManager;
import com.iaasframework.resources.core.IResourceRepository;
import com.iaasframework.resources.core.ResourceException;
import com.iaasframework.resources.core.ResourceRepository;

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

	private String				nameType		= "junos";

	IResource					resource		= null;

//	@Configuration
	public static Option[] configuration() {

		return ConfigurerTestFactory.newResourceManagerTest();
	}

//	@Before
	public void setup() {
		logger.debug("Setting up for the test");

		/* get resource manager */

		resourceManager = getOsgiService(IResourceManager.class, 20000);

	}

//	@Test
	public void testFirstOperation() throws ResourceException {
		// registerResourceRepository(nameType);
		/* check resource */
		resource = resourceManager.createResource((new ResourceDescriptorFactory()).newInstanceDescriptorJunOS());

		/* Create a resource */
		IResourceIdentifier resourceIdentifier = resource.getResourceIdentifier();
		ActionSetCapabilityClient actionClient = new ActionSetCapabilityClient(resourceIdentifier.getId());

		actionClient.executeAction(GetConfigurationAction.GETCONFIG, null);

		/* Remove resource */
		resourceManager.removeResource(resourceIdentifier);

	}

	private void registerResourceRepository(String type) {

		ResourceRepository repo = new ResourceRepository(type);
		Properties serviceProperties = new Properties();
		serviceProperties.put("type", type);

		logger.info("Manually publishing Repository " + type);
		bundleContext.registerService(IResourceRepository.class.getName(), repo, serviceProperties);
	}

}
