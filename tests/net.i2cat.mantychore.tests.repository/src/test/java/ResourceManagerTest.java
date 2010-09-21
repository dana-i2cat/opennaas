
import static org.junit.Assert.fail;
import net.i2cat.mantychore.constants.ActionJunosConstants;
import net.i2cat.mantychore.tests.utils.ConfigurerTestFactory;
import net.i2cat.mantychore.tests.utils.ResourceDescriptorFactory;

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

import com.iaasframework.capabilities.actionset.ActionSetCapabilityClient;
import com.iaasframework.resources.core.IResource;
import com.iaasframework.resources.core.IResourceIdentifier;
import com.iaasframework.resources.core.IResourceManager;
import com.iaasframework.resources.core.IResourceRepository;
import com.iaasframework.resources.core.ResourceException;
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

		/* get resource manager */
		resourceManager = getOsgiService(IResourceManager.class, 20000);

	}

	@Test
	public void testFirstOperation() throws CapabilityException {
		try {
			/* check resource */
			resource = resourceManager.createResource((new ResourceDescriptorFactory()).newInstanceDescriptorJunOS());

			/* Create a resource */
			IResourceIdentifier resourceIdentifier = resource.getResourceIdentifier();
			ActionSetCapabilityClient actionClient = new ActionSetCapabilityClient(resourceIdentifier.getId());

			actionClient.executeAction(ActionJunosConstants.GETCONFIG, null);

			/* Remove resource */
			resourceManager.removeResource(resourceIdentifier);

		} catch (ResourceException e) {
			e.printStackTrace();
			fail();
		}
	}

}
