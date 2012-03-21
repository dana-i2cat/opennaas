package mantychore;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.System;
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
public class RemoveLogicalRouterTest {
	static Log					log				= LogFactory.getLog(RemoveLogicalRouterTest.class);
	private boolean				isMock			= false;
	String						resourceFriendlyID;
	String						LRFriendlyID	= "pepito";
	IResource					resource;

	@Inject
	private CommandProcessor commandprocessor;

	@Inject
	private IResourceManager resourceManager;

	@Inject
	private IProtocolManager protocolManager;

	@Inject
	private BundleContext bundleContext;

    @Inject
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.repository)")
    private BlueprintContainer routerRepositoryService;

    @Inject
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.capability.chassis)")
    private BlueprintContainer chasisService;

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

	@Before
	public void initResource() throws ResourceException, ProtocolException {
		List<String> capabilities = new ArrayList<String>();

		capabilities.add("chassis");
		capabilities.add("queue");

		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("junosm20", "router", capabilities);
		resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		resource = resourceManager.createResource(resourceDescriptor);
		isMock = createProtocolForResource(resource.getResourceIdentifier().getId());
		resourceManager.startResource(resource.getResourceIdentifier());
	}

	public Boolean createProtocolForResource(String resourceId) throws ProtocolException {
		ProtocolSessionContext context = ResourceHelper.newSessionContextNetconf();

		IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManagerWithContext(resourceId, context);

		return context.getSessionParameters().get(context.PROTOCOL_URI).toString().contains("mock");
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
	public void RemoveLogicalRouterfromPhysicalRouter() throws Exception {
		String logicalRouterName;

		if (isMock) {
			logicalRouterName = "cpe1";
		} else {
			logicalRouterName = "pepito";
		}

		List<String> response2 = KarafCommandHelper.executeCommand("chassis:listLogicalRouters " + resourceFriendlyID,
					commandprocessor);
		Assert.assertTrue(response2.get(0).contains(logicalRouterName));

		// chassis:deleteLogicalRoute

		List<String> response = KarafCommandHelper.executeCommand("chassis:deleteLogicalRouter " + resourceFriendlyID + " " + logicalRouterName,
					commandprocessor);
		// assert command output no contains ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());

		List<String> response1 = KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID, commandprocessor);
		// assert command output no contains ERROR tagInitializerTestHelper
		Assert.assertTrue(response1.get(1).isEmpty());

		response2 = KarafCommandHelper.executeCommand("chassis:listLogicalRouters " + resourceFriendlyID,
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
}