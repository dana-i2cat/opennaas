package mantychore;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.protocol.IProtocolManager;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;
import net.i2cat.nexus.tests.InitializerTestHelper;
import net.i2cat.nexus.tests.IntegrationTestsHelper;
import net.i2cat.nexus.tests.KarafCommandHelper;
import net.i2cat.nexus.tests.ProtocolSessionHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Assert;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.osgi.framework.BundleContext;
import org.osgi.service.command.CommandProcessor;

public class ConfigureLRTest extends AbstractIntegrationTest {
	static Log		log				= LogFactory
														.getLog(ConfigureLRTest.class);

	@Inject
	BundleContext	bundleContext	= null;

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

	public void ConfigureInterfaceInterfaceTest() throws Exception {
		IResourceManager resourceManager = null;
		CommandProcessor commandprocessor = null;
		IProtocolManager protocolManager = null;

		String name = "junosm20";
		String type = "router";
		String resourceFriendlyID = name + ":" + type;

		ArrayList<String> capabilitiesId = new ArrayList<String>();
		capabilitiesId.add("chassis");
		capabilitiesId.add("ipv4");
		capabilitiesId.add("queue");

		ProtocolSessionContext protocolSessionContext = ProtocolSessionHelper.newSessionContextNetconf();
		String nameURI = ((String) protocolSessionContext.getSessionParameters().get(ProtocolSessionContext.PROTOCOL_URI));
		boolean isMock = nameURI.startsWith("mock");

		/* initialize bundles */

		log.info("Waiting to load all bundles");
		/* Wait for the activation of all the bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		log.info("Loaded all bundles");

		/* init capability */

		log.info("This is running inside Equinox. With all configuration set up like you specified. ");

		resourceManager = getOsgiService(IResourceManager.class);
		commandprocessor = getOsgiService(CommandProcessor.class);
		protocolManager = getOsgiService(IProtocolManager.class, 5000);

		log.info("INFO: Initialized!");

		/* initialize resource to test */
		IResource resource = InitializerTestHelper.initResource(name, type, capabilitiesId, resourceManager, protocolManager, protocolSessionContext);

		// chassis:createlogicalrouter
		String logicalRouterName = "Logical1";
		String interfId2 = "fe-0/1/3.1";
		String interfId1 = "lt-0/1/2.12";
		String interfId3 = "lo0.1";

		String interfId4 = "fe-0/1/3.4";

		// When you create a logical router, do you wanto to have created a resource which represents this resource!!! I think this idea is not
		// correct
		// You should have a different command o extra flag to create this resource (in the resource:create??). Also, you have to specify its
		// capabilities
		List<String> response = KarafCommandHelper.executeCommand("chassis:createLogicalRouter " + resourceFriendlyID + " "
				+ logicalRouterName + " " + interfId1 + " " + interfId2 + " " + interfId3,
				commandprocessor);
		log.info(response.get(0));

		// check logical router creation
		//FIXME listLogicalRouter or listLogicalRouters!!!
		List<String> response2 = KarafCommandHelper.executeCommand("chassis:listLogicalRouters" + resourceFriendlyID, commandprocessor);
		log.info(response2.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response2.get(1).isEmpty());

		if (!isMock) {
			ComputerSystem physicalRouter = (ComputerSystem) resource.getModel();
			boolean exist = CheckHelper.checkExistLogicalRouter(physicalRouter, logicalRouterName);
			Assert.assertTrue(exist);

		}

		// start resource
		resourceManager.startResource(resource.getResourceIdentifier());

		// HOW GET WE A VIRTUAL RESOURCE, WE DON'T HAVE ANY METHOD TO SEARCH????
		IResourceIdentifier resourceIdentifier = resourceManager.getIdentifierFromResourceName("router", logicalRouterName);
		IResource logicalResource = resourceManager.getResource(resourceIdentifier);
		/* test for ethernet interfaces */
		testLogicalRouterConfigureCheckInterface(commandprocessor, "fe-0/1/3", "1", "192.168.13.2", "255.255.255.0", logicalRouterName,
				(ComputerSystem) logicalResource.getModel(), isMock);

		/* test for ethernet interfaces */
		testLogicalRouterConfigureCheckInterface(commandprocessor, "lt-0/1/2", "12", "192.168.12.2", "255.255.255.0", logicalRouterName,
				(ComputerSystem) logicalResource.getModel(), isMock);

		/* test for ethernet interfaces */
		testLogicalRouterConfigureCheckInterface(commandprocessor, "lo0.1", "1", "192.168.1.3", "255.255.255.0", logicalRouterName,
				(ComputerSystem) logicalResource.getModel(), isMock);

		/* test to check that add interface don't work if the logical resource is started */
		// chassis:addInterface R1 L1 fe-0/0/1.1
		// check interface is included in the L1
		List<String> response3 = KarafCommandHelper.executeCommand(
				"chassis:addInterface " + resourceFriendlyID + " " + logicalRouterName + " " + interfId4,
				commandprocessor);
		log.info(response3.get(0));

		testLogicalRouterConfigureCheckInterface(commandprocessor, "fe-0/1/3", "4", null, null, logicalRouterName, (ComputerSystem) logicalResource
				.getModel(), isMock);
		// assert command output contains ERROR tag
		Assert.assertFalse(!response3.get(1).isEmpty() && response3.get(1).contains("ERROR"));

		/* test to check add and remove interface */
		resourceManager.stopResource(logicalResource.getResourceIdentifier());

		// chassis:addInterface R1 L1 fe-0/0/1.1
		// check interface is included in the L1
		List<String> response4 = KarafCommandHelper.executeCommand(
				"chassis:addInterface " + resourceFriendlyID + " " + logicalRouterName + " " + interfId4,
				commandprocessor);
		log.info(response4.get(0));

		testLogicalRouterConfigureCheckInterface(commandprocessor, "fe-0/1/3", "4", null, null, logicalRouterName, (ComputerSystem) logicalResource
				.getModel(), isMock);
		// assert command output contains ERROR tag
		Assert.assertFalse(response4.get(1).isEmpty());

		// chassis:removeInterface R1 L1 fe-0/0/1.1
		// check interface is not included in the L1
		List<String> response5 = KarafCommandHelper.executeCommand(
				"chassis:removeInterface " + resourceFriendlyID + " " + logicalRouterName + " " + interfId4,
				commandprocessor);
		log.info(response5.get(0));

		testLogicalRouterConfigureCheckInterface(commandprocessor, "fe-0/1/3", "4", null, null, logicalRouterName, (ComputerSystem) logicalResource
				.getModel(), isMock);
		// assert command output contains ERROR tag
		Assert.assertFalse(response5.get(1).isEmpty());

		List<String> response6 = KarafCommandHelper.executeCommand("chassis:deleteLogicalRouter " + resourceFriendlyID + " " + logicalRouterName,
				commandprocessor);
		log.info(response6.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response6.get(1).isEmpty());

		try {
			InitializerTestHelper.removeResources(resourceManager);
		} catch (ResourceException e) {
			Assert.fail(e.getMessage());
		}

	}

	public void testLogicalRouterConfigureCheckInterface(CommandProcessor commandprocessor, String inter, String port, String Ip, String mask,
			String resourceFriendlyID, ComputerSystem model, boolean isMock) throws Exception {
		// ipv4:setIP fe-0/0/1.1 192.168.1.2 255.255.255.0
		List<String> response = KarafCommandHelper.executeCommand(
				"ipv4:setIP " + resourceFriendlyID + " " + inter + "." + port + " " + Ip + " " + mask,
				commandprocessor);
		//
		// assert command output contains "[ERROR] Configuration for Loopback interface not allowed"
		// Assert.assertTrue(response.contains("[ERROR] Configuration for Loopback interface not allowed"));
		Assert.assertTrue(response.get(1).contains("[ERROR] Configuration for Loopback interface not allowed"));

		// check that command fails if interface doesn't exist
		// check updated interface if exists
		if (!isMock)
			Assert.assertTrue(CheckHelper.checkInterface(inter, port, Ip, mask, model));
		// restore configuration
	}

}
