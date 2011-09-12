package mantychore;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;


import java.util.List;

import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.tests.IntegrationTestsHelper;
import net.i2cat.nexus.tests.KarafCommandHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Assert;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.osgi.framework.BundleContext;
import org.osgi.service.command.CommandProcessor;

public class CreateLogicalRouterTest extends AbstractIntegrationTest {
	static Log					log				= LogFactory
														.getLog(CreateLogicalRouterTest.class);

	String						resourceFriendlyID;
	String						LRFriendlyID;
	IResource					resource;
	private CommandProcessor	commandprocessor;
	private IResourceManager	resourceManager;

	private Boolean				isMock;

	/*
	 * all types interfaces
	 */
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

	public void initBundles() {
		log.info("Waiting to load all bundles");
		/* Wait for the activation of all the bundles */
		// IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		log.info("Loaded all bundles");
		resourceManager = getOsgiService(IResourceManager.class, 5000);
		commandprocessor = getOsgiService(CommandProcessor.class);
		// initTest();

	}

	public void createLogicalRouterTest() {
		// chassis:createLogicalRouter R1 L1
		List<String> response;
		try {
			response = KarafCommandHelper.executeCommand("chassis:createLR " + resourceFriendlyID + " " + LRFriendlyID,
					commandprocessor);
			// check logical router creation
			// assert command output no contains ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());

			// check new LR is created on resources pool ( ResourceManager and ResourceRepo)
			response = KarafCommandHelper.executeCommand("resource:list",
					commandprocessor);
			Assert.assertTrue(response.get(0).contains(LRFriendlyID));

			response = KarafCommandHelper.executeCommand("chassis:deleteLR " + resourceFriendlyID + " " + LRFriendlyID,
					commandprocessor);
			// check logical router is deleted
			// assert command output no contains ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());

			// chassis:addInterface R1 L1 fe-0/0/1.1
			// check that the interface is included in the L1
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void listLogicalRoutersTest() {
		// chassis:listLogicalRouters
		// check if the command works
		List<String> response;
		try {
			response = KarafCommandHelper.executeCommand("chassis:listLR " + resourceFriendlyID,
					commandprocessor);
			// assert command output no contains ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());

			Assert.assertTrue(response.get(0).contains(LRFriendlyID));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void discoveryLogicalRoutersTest() {

		try {
			// INIT TEST

			String LRname = "";
			// resource:create
			// the start method has to create the LR router
			// resource:start

			// resource:list
			List<String> response = KarafCommandHelper.executeCommand("resource:list ",
					commandprocessor);
			// assert command output no contains ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());

			// check logical routers are in the list
			Assert.assertTrue(response.get(0).contains(LRname));

			response = KarafCommandHelper.executeCommand("resource:info " + "router:" + LRname,
					commandprocessor);
			// assert command output no contains ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());

			// check resource initialized
			// check descriptors include IP capability
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// public void testingMethod(String inter, String subport, int VLANid) throws Exception {
	//
	// // Obtain the previous IP/MASK make the rollback of the test
	// int OldVLAN = getOldInterface(resource, inter, subport);
	//
	// // SET NEW VLAN
	// responseError = KarafCommandHelper.executeCommand("chassis:setVLAN " + resourceFriendlyID + " " + inter + "." + subport + " " + VLANid
	// , commandprocessor);
	// // assert command output no contains ERROR tag
	// Assert.assertTrue(responseError.get(1).isEmpty());
	// responseError = KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID, commandprocessor);
	// // assert command output no contains ERROR tag
	// Assert.assertTrue(responseError.get(1).isEmpty());
	//
	// // Check that the resource have the old VLAN in the model despite of have send the command
	// checkModel(inter, subport, OldVLAN, resource);
	//
	// // REFRESH to fill up the model
	// responseError = KarafCommandHelper.executeCommand("chassis:showInterfaces -r " + resourceFriendlyID, commandprocessor);
	// // assert command output no contains ERROR tag
	// Assert.assertTrue(responseError.get(1).isEmpty());
	//
	// // CHECK CHANGES IN THE INTERFACE with new VLAN
	// checkModel(inter, subport, VLANid, resource);
	//
	// // ROLLBACK OF THE INTERFACE
	// responseError = KarafCommandHelper.executeCommand("chassis:setVLAN " + resourceFriendlyID + " " + inter + "." + subport + " " + OldVLAN
	// , commandprocessor);
	// Assert.assertTrue(responseError.get(1).isEmpty());
	// responseError = KarafCommandHelper.executeCommand("queue:execute  " + resourceFriendlyID, commandprocessor);
	// Assert.assertTrue(responseError.get(1).isEmpty());
	//
	// // REFRESH to fill up the model
	// responseError = KarafCommandHelper.executeCommand("chassis:showInterfaces -r " + resourceFriendlyID, commandprocessor);
	// // assert command output no contains ERROR tag
	// Assert.assertTrue(responseError.get(1).isEmpty());
	//
	// // CHECK THe ROLLBACK IS DONE
	// checkModel(inter, subport, OldVLAN, resource);
	//
	// }

}