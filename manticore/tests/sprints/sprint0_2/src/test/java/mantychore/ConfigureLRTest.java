package mantychore;

import javax.inject.Inject;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.nexus.tests.KarafCommandHelper;
import net.i2cat.nexus.tests.ResourceHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.felix.service.command.CommandProcessor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.ops4j.pax.exam.Customizer;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.junit.ProbeBuilder;
import org.ops4j.pax.exam.util.Filter;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.blueprint.container.BlueprintContainer;

import static net.i2cat.nexus.tests.OpennaasExamOptions.*;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.*;
import static org.ops4j.pax.exam.CoreOptions.*;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class ConfigureLRTest
{
	static Log log = LogFactory.getLog(ConfigureLRTest.class);

	String						resourceFriendlyID;
	String						logicalRouterName;
	IResource					resource;
	private boolean				isMock			= true;

	@Inject
	private BundleContext bundleContext;

	@Inject
	private CommandProcessor commandprocessor;

	@Inject
	private IResourceManager resourceManager;

	@Inject
	private IProtocolManager protocolManager;

    @Inject
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.repository)")
    private BlueprintContainer routerRepositoryService;

    @Inject
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.capability.chassis)")
    private BlueprintContainer chasisService;

    @Inject
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.capability.ip)")
    private BlueprintContainer ipService;

    @Inject
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.queuemanager)")
    private BlueprintContainer queueService;

    @ProbeBuilder
    public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
        probe.setHeader(Constants.DYNAMICIMPORT_PACKAGE, "*,org.apache.felix.service.*;status=provisional");
        return probe;
    }

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
					   includeFeatures("opennaas-router"),
					   includeTestHelper(),
					   noConsole(),
					   keepRuntimeFolder());
	}

	public Boolean createProtocolForResource(String resourceId) throws ProtocolException {

		ProtocolSessionContext context = ResourceHelper.newSessionContextNetconf();

		IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManagerWithContext(resourceId, context);

		if (context.getSessionParameters().get(context.PROTOCOL_URI).toString().contains("mock")) {
			return true;
		}

		return false;
	}

	@Before
	public void initResource() {
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

	@After
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

		List<String> response;
		List<String> response1;

		if (isMock) {
			logicalRouterName = "cpe1";
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

		response = KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID, commandprocessor);

		// check logical router creation
		List<String> response2 = KarafCommandHelper.executeCommand("chassis:listLogicalRouters " + resourceFriendlyID, commandprocessor);
		log.info(response2.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response2.get(1).isEmpty());

		if (!isMock) {
			Assert.assertFalse(resource.getModel().getChildren().isEmpty());

			ComputerSystem physicalRouter = (ComputerSystem) resource.getModel();
			boolean exist = ExistanceHelper.checkExistLogicalRouter(physicalRouter, logicalRouterName);
			Assert.assertTrue(exist);
		}

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
