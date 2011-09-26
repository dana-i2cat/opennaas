package mantychore;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.nexus.resources.ILifecycle.State;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;
import net.i2cat.nexus.resources.helpers.ResourceDescriptorFactory;
import net.i2cat.nexus.resources.protocol.IProtocolManager;
import net.i2cat.nexus.resources.protocol.IProtocolSessionManager;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;
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
import org.osgi.service.command.CommandProcessor;

@RunWith(JUnit4TestRunner.class)
public class CreateLogicalRouterTest extends AbstractIntegrationTest {
	static Log					log				= LogFactory
														.getLog(CreateLogicalRouterTest.class);

	String						resourceFriendlyID;
	String						LRFriendlyID	= "pepito";
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

	@Before
	public void initBundles() {
		log.info("Waiting to load all bundles");
		/* Wait for the activation of all tLogicalROuterhe bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		log.info("Loaded all bundles");
		resourceManager = getOsgiService(IResourceManager.class, 5000);
		commandprocessor = getOsgiService(CommandProcessor.class);

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
	public void createLogicalRouterTest() {

		initBundles();
		initResource();
		// chassis:createLogicalRouter R1 L1
		List<String> response;
		try {
			LRFriendlyID = "cpe1";
			response = KarafCommandHelper.executeCommand("chassis:createLogicalRouter " + resourceFriendlyID + " " + LRFriendlyID,
					commandprocessor);
			// assert command output no contains ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());
			response = KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID,
					commandprocessor);
			Assert.assertTrue(response.get(1).isEmpty());
			// check logical router creation
			response = KarafCommandHelper.executeCommand("resource:refresh " + resourceFriendlyID,
					commandprocessor);
			Assert.assertTrue(response.get(1).isEmpty());

			if (!isMock)
				Assert.assertTrue(CheckHelper.checkExistLogicalRouter((ComputerSystem) resource.getModel(), LRFriendlyID));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void deleteLogicalRouterTest() {
		List<String> response;
		try {
			LRFriendlyID = "cpe1";
			// delete LR
			response = KarafCommandHelper.executeCommand("chassis:deleteLogicalRouter " + resourceFriendlyID + " " + LRFriendlyID,
					commandprocessor);
			// assert command output no contains ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());

			response = KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID,
					commandprocessor);
			Assert.assertTrue(response.get(1).isEmpty());

			// check logical router is deleted
			response = KarafCommandHelper.executeCommand("resource:refresh " + resourceFriendlyID,
					commandprocessor);
			// assert command output no contains ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());

			if (!isMock)
				Assert.assertFalse(CheckHelper.checkExistLogicalRouter((ComputerSystem) resource.getModel(), LRFriendlyID));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void listLogicalRoutersTest() {
		initBundles();
		initResource();
		List<String> response;
		try {
			LRFriendlyID = "cpe1";
			// chassis:listLogicalRouters
			response = KarafCommandHelper.executeCommand("chassis:listLogicalRouters " + resourceFriendlyID,
					commandprocessor);
			// assert command output no contains ERROR tag

			Assert.assertTrue(response.get(1).isEmpty());
			if (!isMock) {
				Assert.assertTrue(CheckHelper.checkExistLogicalRouter((ComputerSystem) resource.getModel(), LRFriendlyID));
				Assert.assertTrue(response.get(0).contains(LRFriendlyID));

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void discoveryLogicalRoutersTest() {
		initBundles();
		initResource();
		try {
			String LRname = "cpe1";

			// resource:list
			List<String> response = KarafCommandHelper.executeCommand("resource:list ",
					commandprocessor);
			// assert command output no contains ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());

			// check that the logical router is on the list
			if (!isMock)
				Assert.assertTrue(response.get(0).contains(LRname));

			response = KarafCommandHelper.executeCommand("resource:info " + "router:" + LRname,
					commandprocessor);
			// assert command output no contains ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());

			// check resource initialized
			if (!isMock)
				Assert.assertTrue(response.get(0).contains("INITIALIZED"));
			// check descriptors include IP capability

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}