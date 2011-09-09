package mantychore;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.osgi.service.command.CommandProcessor;

@RunWith(JUnit4TestRunner.class)
public class RemoveLogicalRouterTest extends AbstractIntegrationTest {
	static Log					log				= LogFactory
															.getLog(RemoveLogicalRouterTest.class);
	IResourceManager			resourceManager;
	String						resourceFriendlyID;
	IResource					resource;
	private CommandProcessor	commandprocessor;
	private boolean				isMock			= false;
	@Inject
	BundleContext				bundleContext	= null;

	@Configuration
	public static Option[] configuration() throws Exception {

		Option[] options = combine(
					IntegrationTestsHelper.getMantychoreTestOptions(),
					mavenBundle().groupId("net.i2cat.nexus").artifactId(
							"net.i2cat.nexus.tests.helper")
									// , vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
									// ////////import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

									);

		return options;
	}

	public void clearRepo() throws ResourceException {
		for (IResource resource : resourceManager.listResources()) {
			resourceManager.removeResource(resource.getResourceIdentifier());
		}
	}

	// public Boolean createProtocolForResource(String resourceId) throws ProtocolException {
	// IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 5000);
	//
	// // ProtocolSessionContext context = ProtocolSessionHelper.newSessionContextNetconf();
	// IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManagerWithContext(resourceId, context);
	//
	// if (context.getSessionParameters().get(context.PROTOCOL_URI).toString().contains("mock")) {
	// return true;
	// }
	//
	// return false;
	// }

	// public void initTest() {
	//
	// List<String> capabilities = new ArrayList<String>();
	//
	// capabilities.add("ipv4");
	// capabilities.add("queue");
	//
	// ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("junosm20", "router", capabilities);
	// resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();
	// try {
	// clearRepo();
	// resource = resourceManager.createResource(resourceDescriptor);
	// isMock = createProtocolForResource(resource.getResourceIdentifier().getId());
	// resourceManager.startResource(resource.getResourceIdentifier());
	//
	// // call the command to initialize the model
	// } catch (ResourceException e) {
	// Assert.fail(e.getMessage());
	// } catch (ProtocolException e) {
	// Assert.fail(e.getMessage());
	// }
	//
	// }

	// @Before
	public void initBundles() {
		log.info("Waiting to load all bundles");
		/* Wait for the activation of all the bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		log.info("Loaded all bundles");

		/* init capability */

		log.info("This is running inside Equinox. With all configuration set up like you specified. ");

		resourceManager = getOsgiService(IResourceManager.class);

		log.info("INFO: Initialized!");
		commandprocessor = getOsgiService(CommandProcessor.class);
		// initTest();

	}

	public void RemoveLogicalRouterAdminPhysical() {
		// chassis:deleteLogicalRouter
		// check chassis:listLogicalRouters from R1 does not includes L1

	}

	/**
	 * This test is deprecated net/i2cat/nexus/resources/tests/ResourceManagerTest.java/testRemoveResource. This test is responsible for testing this
	 * story
	 * 
	 * **/
	@Deprecated
	public void RemoveLogicalRouterLogicalProvider() {
		// resource:remove L1
		// check that resource is not in the repo
		// check chassis:listLogicalRouters from R1 does includes L1
	}

	public void failRemoveCreateLogicalRouterTest() {
		// chassis:createLogicalRouter R1 L1 fe-0/0/1.1 fe-0/0/1.3 fe-0/0/1.2
		// check logical router creation
		// resource:start L1
		// chassis:removeInterface R1 L1 fe-0/0/1.1
		// test fail, cannot add new interfaces when the L1 resource is started
		// restore configuration

	}

}