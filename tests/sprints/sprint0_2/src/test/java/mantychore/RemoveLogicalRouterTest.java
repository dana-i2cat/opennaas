package mantychore;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.System;
import org.opennaas.core.resources.IResource;
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.apache.felix.service.command.CommandProcessor;

@RunWith(JUnit4TestRunner.class)
public class RemoveLogicalRouterTest extends AbstractIntegrationTest {
	static Log					log				= LogFactory.getLog(RemoveLogicalRouterTest.class);
	private boolean				isMock			= false;
	String						resourceFriendlyID;
	String						LRFriendlyID	= "pepito";
	IResource					resource;
	private CommandProcessor	commandprocessor;
	private IResourceManager	resourceManager;
	private IProtocolManager	protocolManager	= null;
	@Inject
	BundleContext				bundleContext	= null;

	@Configuration
	public static Option[] configuration() throws Exception {

		Option[] options = combine(
				IntegrationTestsHelper.getMantychoreTestOptions(),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.tests.helper")
				// ,
				// vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
				// ////////import static
				// org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

				);

		return options;
	}

	public void initBundles() {
		log.info("Waiting to load all bundles");
		/* Wait for the activation of all tLogicalROuterhe bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		log.info("Loaded all bundles");
		resourceManager = getOsgiService(IResourceManager.class, 5000);
		commandprocessor = getOsgiService(CommandProcessor.class);
		protocolManager = getOsgiService(IProtocolManager.class, 5000);

	}

	public void initResource() {

		clearRepo();
		List<String> capabilities = new ArrayList<String>();

		capabilities.add("chassis");
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

	public Boolean createProtocolForResource(String resourceId) throws ProtocolException {
		IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 5000);

		ProtocolSessionContext context = ProtocolSessionHelper.newSessionContextNetconf();

		IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManagerWithContext(resourceId, context);

		if (context.getSessionParameters().get(context.PROTOCOL_URI).toString().contains("mock")) {
			return true;
		}

		return false;
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
	public void RemoveLogicalRouterfromPhysicalRouter() throws Exception {
		initBundles();
		initResource();
		/* init capability */

		String logicalRouterName;

		if (isMock) {
			logicalRouterName = "routerV2";
		} else {
			logicalRouterName = "pepito";
		}

		List<String> response2 = KarafCommandHelper.executeCommand("chassis:listLogicalRouter " + resourceFriendlyID,
					commandprocessor);
		Assert
				.assertTrue(response2.get(0).contains(logicalRouterName));

		// chassis:deleteLogicalRoute

		List<String> response = KarafCommandHelper.executeCommand("chassis:deleteLogicalRouter " + resourceFriendlyID + " " + logicalRouterName,
					commandprocessor);
		// assert command output no contains ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());

		List<String> response1 = KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID, commandprocessor);
		// assert command output no contains ERROR tagInitializerTestHelper
		Assert.assertTrue(response1.get(1).isEmpty());

		response1 = KarafCommandHelper.executeCommand("resource:refresh " + resourceFriendlyID, commandprocessor);
		Assert.assertTrue(response1.get(1).isEmpty());

		response2 = KarafCommandHelper.executeCommand("chassis:listLogicalRouter " + resourceFriendlyID,
					commandprocessor);
		// assert command output no contains ERROR tag
		Assert.assertTrue(response2.get(1).isEmpty());

		// check chassis:listLogicalRouters from R1 does not includes L1
		if (!isMock) {
			ComputerSystem physicalRouter = (ComputerSystem) resource.getModel();
			boolean exist = checkExistLogicalRouter(physicalRouter, logicalRouterName);
			Assert.assertFalse(exist);
			Assert.assertFalse(response2.get(0).contains(logicalRouterName));

		}
		clearRepo();

	}

	// public void failRemovingLogicalRouterTest() throws Exception {
	// initBundles();
	// initResource();
	// // chassis:createlogicalrouter
	// String logicalRouterName = "Logical1";
	//
	// List<String> response = KarafCommandHelper.executeCommand("chassis:createLogicalRouter " + resourceFriendlyID + " "
	// + logicalRouterName, commandprocessor);
	//
	// // check logical router creation
	// List<String> response2 = KarafCommandHelper.executeCommand("chassis:listLogicalRouters" + resourceFriendlyID, commandprocessor);
	//
	// // assert command output no contains ERROR tag
	// Assert.assertTrue(response2.get(1).isEmpty());
	//
	// if (!isMock) {
	// ComputerSystem physicalRouter = (ComputerSystem) resource.getModel();
	// boolean exist = checkExistLogicalRouter(physicalRouter, logicalRouterName);
	// Assert.assertTrue(exist);
	//
	// }
	//
	// // start resource
	// resourceManager.startResource(resource.getResourceIdentifier());
	//
	// // chassis:removeInterface R1 L1 fe-0/0/1.1 //TODO IMPLEMENT REMOVE INTERFACE??
	// List<String> response3 = KarafCommandHelper.executeCommand(
	// "chassis:removeInterface " + resourceFriendlyID + " " + logicalRouterName,
	// commandprocessor);
	// log.info(response3.get(0));
	//
	// // assert command output contains ERROR tag
	// Assert.assertFalse(!response3.get(1).isEmpty() && response3.get(1).contains("ERROR"));
	//
	// // test fail, cannot add new interfaces when the L1 resource is started
	// // restore configuration
	//
	// resourceManager.stopResource(resource.getResourceIdentifier());
	//
	// List<String> response4 = KarafCommandHelper.executeCommand("chassis:deleteLogicalRouter " + resourceFriendlyID + " " + logicalRouterName,
	// commandprocessor);
	// log.info(response4.get(0));
	//
	// // assert command output no contains ERROR tag
	// Assert.assertTrue(response4.get(1).isEmpty());
	//
	// try {
	// InitializerTestHelper.removeResources(resourceManager);
	// } catch (ResourceException e) {
	// Assert.fail(e.getMessage());
	// }
	// }

	public static boolean checkExistLogicalRouter(ComputerSystem physicalRouter, String logicalRouterName) {
		List<System> logicalRouters = physicalRouter.getSystems();
		for (System logicalRouter : logicalRouters) {
			if (logicalRouter.getName().equals(logicalRouterName))
				return true;
		}
		return false;
	}

	/**
	 * This test is deprecated org.opennaas.core.resources/tests/ResourceManagerTest .java/testRemoveResource. This test is responsible for testing this
	 * story
	 * 
	 * **/
	@Deprecated
	public void RemoveLogicalRouterLogicalProvider() {
		// resource:remove L1
		// check that resource is not in the repo
		// check chassis:listLogicalRouters from R1 does includes L1
	}

}