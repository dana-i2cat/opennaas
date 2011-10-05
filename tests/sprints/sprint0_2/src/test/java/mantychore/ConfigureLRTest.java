package mantychore;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.model.ComputerSystem;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import net.i2cat.nexus.tests.IntegrationTestsHelper;
import net.i2cat.nexus.tests.KarafCommandHelper;
import net.i2cat.nexus.tests.ProtocolSessionHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.apache.felix.service.command.CommandProcessor;

@RunWith(JUnit4TestRunner.class)
public class ConfigureLRTest extends AbstractIntegrationTest {
	static Log					log				= LogFactory
															.getLog(ConfigureLRTest.class);

	@Inject
	BundleContext				bundleContext	= null;

	String						resourceFriendlyID;
	String						logicalRouterName;
	IResource					resource;
	private CommandProcessor	commandprocessor;
	private IResourceManager	resourceManager;
	private boolean				isMock			= true;

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

	@Before
	public void initBundles() {
		log.info("Waiting to load all bundles");
		/* Wait for the activation of all tLogicalROuterhe bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		log.info("Loaded all bundles");
		resourceManager = getOsgiService(IResourceManager.class, 5000);
		commandprocessor = getOsgiService(CommandProcessor.class);
		clearRepo();
	}

	public Boolean createProtocolForResource(String resourceId) throws ProtocolException {
		IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 5000);

		ProtocolSessionContext context = ProtocolSessionHelper.newSessionContextNetconf();

		IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManagerWithContext(resourceId, context);

		if (context.getSessionParameters().get(context.PROTOCOL_URI).toString().contains("mock")) {
			return true;
		}

		return false;
	}

	public void initResource() {

		clearRepo();
		List<String> capabilities = new ArrayList<String>();

		capabilities.add("chassis");
		capabilities.add("ipv4");
		capabilities.add("queue");

		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("junosm20", "router", capabilities);
		resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();
		try {

			resource = resourceManager.createResource(resourceDescriptor);
			isMock = createProtocolForResource(resource.getResourceIdentifier().getId());
			resourceManager.startResource(resource.getResourceIdentifier());

			// call the command to initialize the model
		} catch (ResourceException e) {
			Assert.fail(e.getMessage());
		} catch (ProtocolException e) {
			Assert.fail(e.getMessage());
		}

	}

	public void clearRepo() {

		log.info("Clearing resource repo");

		IResource[] toRemove = new IResource[resourceManager.listResources().size()];
		toRemove = resourceManager.listResources().toArray(toRemove);

		for (IResource resource : toRemove) {
			if (resource.getState().equals(State.ACTIVE)) {
				try {
					resourceManager.stopResource(resource.getResourceIdentifier());
				} catch (ResourceException e) {
					log.error("Failed to remove resource " + resource.getResourceIdentifier().getId() + " from repository.");
				}
			}
			try {
				resourceManager.removeResource(resource.getResourceIdentifier());
			} catch (ResourceException e) {
				log.error("Failed to remove resource " + resource.getResourceIdentifier().getId() + " from repository.");
			}

		}

		log.info("Resource repo cleared!");
	}

	@Test
	public void ConfigureInterfaceInterfaceTest() throws Exception {
		initBundles();
		initResource();

		List<String> response;
		List<String> response1;

		if (isMock) {
			logicalRouterName = "routerV2";
		} else {
			logicalRouterName = "pepito";
		}

		// chassis:createlogicalrouter

		String interfId2 = "fe-0/1/3.1";
		String interfId1 = "lt-0/1/2.12";
		String interfId3 = "lo0.1";

		String interfId4 = "fe-0/1/3.4";

		// When you create a logical router, do you want to have created a resource which represents this resource!!! I think this idea is not
		// correct
		// You should have a different command o extra flag to create this resource (in the resource:create??). Also, you have to specify its
		// capabilities
		response = KarafCommandHelper.executeCommand("chassis:createLogicalRouter " + resourceFriendlyID + " "
				+ logicalRouterName + " " + interfId1 + " " + interfId2 + " " + interfId3,
				commandprocessor);

		// check logical router creation
		List<String> response2 = KarafCommandHelper.executeCommand("chassis:listLogicalRouter " + resourceFriendlyID, commandprocessor);
		log.info(response2.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response2.get(1).isEmpty());

		if (!isMock) {
			ComputerSystem physicalRouter = (ComputerSystem) resource.getModel();
			boolean exist = CheckHelper.checkExistLogicalRouter(physicalRouter, logicalRouterName);
			Assert.assertTrue(exist);

		}

		// check logical router creation
		List<String> response7 = KarafCommandHelper.executeCommand("resource:refresh " + resourceFriendlyID, commandprocessor);

		// assert command output no contains ERROR tag
		Assert.assertTrue(response7.get(1).isEmpty());
		Assert.assertFalse(resource.getModel().getChildren().isEmpty());

		// HOW GET WE A VIRTUAL RESOURCE, WE DON'T HAVE ANY METHOD TO SEARCH????
		IResourceIdentifier resourceIdentifier = resourceManager.getIdentifierFromResourceName("router", logicalRouterName);

		createProtocolForResource(resourceIdentifier.getId());
		IResource logicalResource = resourceManager.getResource(resourceIdentifier);

		// check logical router creation
		List<String> response8 = KarafCommandHelper.executeCommand("resource:start router:" + logicalRouterName, commandprocessor);
		log.info(response8.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response8.get(1).isEmpty());

		/* test for ethernet interfaces */
		// FIXME THESE INTERFACES HAVE TO EXIST
		boolean result = testLogicalRouterConfigureCheckInterface(commandprocessor, "fe-0/1/3", "1", "192.168.13.2", "255.255.255.0",
				"router:" + logicalRouterName);
		Assert.assertTrue(result);

		/* test for ethernet interfaces */
		result = testLogicalRouterConfigureCheckInterface(commandprocessor, "lt-0/1/2", "12", "192.168.12.2", "255.255.255.0",
				"router:" + logicalRouterName);
		Assert.assertTrue(result);

		/* test for ethernet interfaces */
		result = testLogicalRouterConfigureCheckInterface(commandprocessor, "lo0", "1", "192.168.1.3", "255.255.255.0", "router:" + logicalRouterName);
		Assert.assertFalse(result);
		/* test to check that add interface don't work if the logical resource is started */
		// chassis:addInterface R1 L1 fe-0/0/1.1
		// check interface is included in the L1
		List<String> response3 = KarafCommandHelper.executeCommand(
				"chassis:addInterface " + resourceFriendlyID + " " + "router:" + logicalRouterName + " " + interfId4,
				commandprocessor);
		log.info(response3.get(0));
		Assert.assertTrue(!response3.get(1).isEmpty() && response3.get(1).contains("ERROR"));

		boolean isSent = true;
		try {
			testLogicalRouterConfigureCheckInterface(commandprocessor, "fe-0/1/3", "4", null, null, "router:" + logicalRouterName);

		} catch (Exception e) {
			isSent = false;
		}

		// assert command output contains ERROR tag
		Assert.assertFalse(isSent);

		/* test to check add and remove interface */
		resourceManager.stopResource(logicalResource.getResourceIdentifier());

		// chassis:addInterface R1 L1 fe-0/0/1.1
		// check interface is included in the L1
		List<String> response4 = KarafCommandHelper.executeCommand(
				"chassis:addInterface " + resourceFriendlyID + " " + "router:" + logicalRouterName + " " + interfId4,
				commandprocessor);
		log.info(response4.get(0));

		// chassis:removeInterface R1 L1 fe-0/0/1.1
		// check interface is not included in the L1
		List<String> response5 = KarafCommandHelper.executeCommand(
				"chassis:removeInterface " + resourceFriendlyID + " " + "router:" + logicalRouterName + " " + interfId4,
				commandprocessor);
		log.info(response5.get(0));

		resourceManager.startResource(logicalResource.getResourceIdentifier());
		isSent = true;
		try {
			result = testLogicalRouterConfigureCheckInterface(commandprocessor, "fe-0/1/3", "4", null, null, "router:" + logicalRouterName);

		} catch (Exception e) {
			isSent = false;
		}

		List<String> response6 = KarafCommandHelper.executeCommand("chassis:deleteLogicalRouter " + resourceFriendlyID + " " + logicalRouterName,
				commandprocessor);
		log.info(response6.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response6.get(1).isEmpty());

		// try {
		// InitializerTestHelper.stopResources(resourceManager);
		// InitializerTestHelper.removeResources(resourceManager);
		// } catch (ResourceException e) {
		// Assert.fail(e.getMessage());
		// }

	}

	public boolean testLogicalRouterConfigureCheckInterface(CommandProcessor commandprocessor, String inter, String port, String Ip, String mask,
			String resourceFriendlyID) throws Exception {
		// ipv4:setIP fe-0/0/1.1 192.168.1.2 255.255.255.0
		List<String> response = KarafCommandHelper.executeCommand(
				"ipv4:setIP " + resourceFriendlyID + " " + inter + "." + port + " " + Ip + " " + mask,
				commandprocessor);

		if (inter.startsWith("lo"))
			Assert.assertTrue(response.get(1).contains("[ERROR] Configuration for Loopback interface not allowed"));

		return response.get(1).isEmpty() || !response.get(1).contains("ERROR");

		// check that command fails if interface doesn't exist
		// check updated interface if exists
		// if (!isMock)
		// return CheckHelper.checkInterface(inter, port, Ip, mask, model);
		// else
		// return true;
		// restore configuration
	}

}
