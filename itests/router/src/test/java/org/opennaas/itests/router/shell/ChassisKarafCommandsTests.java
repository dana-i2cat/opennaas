package org.opennaas.itests.router.shell;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeTestHelper;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.felix.service.command.CommandProcessor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.itests.helpers.KarafCommandHelper;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.LogicalTunnelPort;
import org.opennaas.extensions.router.model.ManagedSystemElement.OperationalStatus;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.System;
import org.opennaas.extensions.router.model.VLANEndpoint;
import org.opennaas.itests.router.helpers.ExistanceHelper;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.junit.ProbeBuilder;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.blueprint.container.BlueprintContainer;

/**
 * Tests new chassis operations in interface. In this feature it is necessary to create two operations to configure the status interface. The
 * objective it is to configure the interface status (up, down status administrative)
 * 
 * jira ticket: http://jira.i2cat.net:8080/browse/MANTYCHORE-161
 * 
 * @author Carlos Báez Ruiz
 * 
 */
// @SuppressWarnings("unused")
@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class ChassisKarafCommandsTests
{
	private final static Log	log	= LogFactory.getLog(ChassisKarafCommandsTests.class);

	private String				logicalRouterName;
	private String				resourceFriendlyID;
	private IResource			resource;
	private Boolean				isMock;

	@Inject
	private IProtocolManager	protocolManager;

	@Inject
	private CommandProcessor	commandprocessor;

	@Inject
	private IResourceManager	resourceManager;

	@Inject
	private BundleContext		bundleContext;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.repository)")
	private BlueprintContainer	routerService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.queuemanager)")
	private BlueprintContainer	queueService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.capability.chassis)")
	private BlueprintContainer	chassisService;

	@ProbeBuilder
	public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
		probe.setHeader(Constants.DYNAMICIMPORT_PACKAGE, "*,org.apache.felix.service.*;status=provisional");
		return probe;
	}

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-router", "opennaas-junos"),
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
	public void initTest() throws ResourceException, ProtocolException {
		List<String> capabilities = new ArrayList<String>();
		capabilities.add("chassis");
		capabilities.add("queue");
		capabilities.add("ipv4");

		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("junosm20", "router", capabilities);

		resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		resource = resourceManager.createResource(resourceDescriptor);

		isMock = createProtocolForResource(resource.getResourceIdentifier().getId());
		resourceManager.startResource(resource.getResourceIdentifier());
	}

	@After
	public void resetRepository() throws InterruptedException, ResourceException {
		resourceManager.stopResource(resource.getResourceIdentifier());
		resourceManager.removeResource(resource.getResourceIdentifier());
		for (IResource resource : resourceManager.listResources()) {
			resourceManager.removeResource(resource.getResourceIdentifier());
		}
	}

	/**
	 * This test change the interface status to up. It try to enable the administrative mode, and it will be able to be configured. Estimation: 15
	 * 
	 * tasks:
	 * 
	 * -> Create unitary test, and integration test to new feature
	 * 
	 * -> Implement operation
	 * 
	 * -> create template
	 * 
	 * -> add modifications in the parser
	 * 
	 * -> create karaf command -
	 * 
	 * -> test to a real router
	 * 
	 * 
	 */
	@Test
	public void DownInterfaceETHTest() throws Exception {
		String interfaceToConfigure = "fe-0/3/0";
		// chassis:setVLAN interface VLANid
		List<String> response = KarafCommandHelper.executeCommand("chassis:down " + resourceFriendlyID + " " + interfaceToConfigure,
				commandprocessor);
		log.info(response.get(0));

		// assert command output contains no ERROR
		Assert.assertTrue(response.get(1).isEmpty());
		List<String> response1 = KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID, commandprocessor);
		log.info(response1.get(0));

		// assert command output contains no ERROR
		Assert.assertTrue(response1.get(1).isEmpty());

		List<String> response2 = KarafCommandHelper.executeCommand("chassis:showInterfaces " + resourceFriendlyID, commandprocessor);
		log.info(response2.get(0));

		// assert command output contains no ERROR
		Assert.assertTrue(response2.get(1).isEmpty());
		if (!isMock) {
			ComputerSystem system = (ComputerSystem) resource.getModel();
			List<LogicalDevice> ld = system.getLogicalDevices();
			for (LogicalDevice logicalDevice : ld) {
				if (logicalDevice instanceof LogicalPort && logicalDevice.getName().equals(interfaceToConfigure)) {
					LogicalPort logicalPort = (LogicalPort) logicalDevice;
					Assert.assertTrue(logicalPort.getOperationalStatus() == OperationalStatus.STOPPED);
				}
			}
		}
	}

	/**
	 * This test change the interface status to down. It try to enable the administrative mode, and it will be able to be configured. Estimation: 5
	 * (the operation can be cloned fromt he upInterface) tasks: -> Create unitary test, and integration test to new feature -> Implement operation ->
	 * create template -> add modifications in the parser -> create karaf command -> test to a real router
	 * 
	 * 
	 */
	@Test
	public void DownInterfaceLTTest() throws Exception {
		String interfaceToConfigure = "lt-0/1/2";
		// chassis:setVLAN interface VLANid
		List<String> response = KarafCommandHelper.executeCommand("chassis:down " + resourceFriendlyID + " " + interfaceToConfigure,
				commandprocessor);
		log.info(response.get(0));

		// assert command output contains no ERROR
		Assert.assertTrue(response.get(1).isEmpty());

		List<String> response1 = KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID, commandprocessor);
		log.info(response1.get(0));

		// assert command output contains no ERROR
		Assert.assertTrue(response.get(1).isEmpty());

		List<String> response2 = KarafCommandHelper.executeCommand("chassis:showInterfaces " + resourceFriendlyID, commandprocessor);
		log.info(response2.get(0));

		// assert command output contains no ERROR
		Assert.assertTrue(response.get(1).isEmpty());
		if (!isMock) {
			ComputerSystem system = (ComputerSystem) resource.getModel();
			List<LogicalDevice> ld = system.getLogicalDevices();
			for (LogicalDevice logicalDevice : ld) {
				if (logicalDevice instanceof LogicalPort && logicalDevice.getName().equals(interfaceToConfigure)) {
					LogicalPort logicalPort = (LogicalPort) logicalDevice;
					Assert.assertTrue(logicalPort.getOperationalStatus() == OperationalStatus.STOPPED);
				}
			}
		}
	}

	@Test
	public void DownUPInterfaceLoTest() throws Exception {
		DownInterfaceLo();
		UPInterfaceLo();
	}

	public void DownInterfaceLo() throws Exception {

		// chassis:setVLAN interface VLANid
		List<String> response = KarafCommandHelper.executeCommand("chassis:down " + resourceFriendlyID + " lo0", commandprocessor);
		log.info(response.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());

		List<String> response1 = KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID, commandprocessor);
		log.info(response1.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());

		List<String> response2 = KarafCommandHelper.executeCommand("chassis:showInterfaces " + resourceFriendlyID, commandprocessor);
		log.info(response2.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());

		ComputerSystem system = (ComputerSystem) resource.getModel();
		List<LogicalDevice> ld = system.getLogicalDevices();

		if (!isMock) {
			for (LogicalDevice logicalDevice : ld) {
				if (logicalDevice instanceof LogicalPort && logicalDevice.getName().equals("lo0.0")) {
					LogicalPort logicalPort = (LogicalPort) logicalDevice;
					Assert.assertTrue(logicalPort.getOperationalStatus() == OperationalStatus.STOPPED);
				}
			}

		}
	}

	public void UPInterfaceLo() throws Exception {

		// chassis:setVLAN interface VLANid
		List<String> response = KarafCommandHelper.executeCommand("chassis:up " + resourceFriendlyID + " lo0", commandprocessor);
		log.info(response.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());

		List<String> response1 = KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID, commandprocessor);
		log.info(response1.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response1.get(1).isEmpty());

		List<String> response2 = KarafCommandHelper.executeCommand("chassis:showInterfaces " + resourceFriendlyID, commandprocessor);
		log.info(response2.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response2.get(1).isEmpty());
		if (!isMock) {
			// assert model updated
			ComputerSystem system = (ComputerSystem) resource.getModel();
			List<LogicalDevice> ld = system.getLogicalDevices();
			for (LogicalDevice logicalDevice : ld) {
				if (logicalDevice instanceof LogicalPort && logicalDevice.getName().equals("lo0.0")) {
					LogicalPort logicalPort = (LogicalPort) logicalDevice;
					Assert.assertTrue(logicalPort.getOperationalStatus() == OperationalStatus.OK);
				}
			}

		}
	}

	@Test
	public void DownUpEthernet() throws Exception {
		// String ethernet = "fe-0/3/0";
		// REal test
		// String ethernet = "fe-0/0/1";

		// MMOCK TEST
		String ethernet = "fe-0/1/3";

		/* down a logical tunnel */
		DownInterfaceETH(ethernet);
		/* up a logical tunnel */
		UpInterfaceETH(ethernet);
	}

	/**
	 * This test change the interface status to up. It try to enable the administrative mode, and it will be able to be configured. Estimation: 15
	 * 
	 * tasks:
	 * 
	 * -> Create unitary test, and integration test to new feature
	 * 
	 * -> Implement operation
	 * 
	 * -> create template
	 * 
	 * -> add modifications in the parser
	 * 
	 * -> create karaf command -
	 * 
	 * -> test to a real router
	 * 
	 * @throws Exception
	 * 
	 * 
	 */
	public void DownInterfaceETH(String interfaceToConfigure) throws Exception {

		// try {
		// chassis:setVLAN interface VLANid
		List<String> response = KarafCommandHelper.executeCommand("chassis:down " + resourceFriendlyID + " " + interfaceToConfigure,
				commandprocessor);
		log.info(response.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());
		List<String> response1 = KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID, commandprocessor);
		log.info(response1.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response1.get(1).isEmpty());

		List<String> response2 = KarafCommandHelper.executeCommand("chassis:showInterfaces " + resourceFriendlyID, commandprocessor);
		log.info(response2.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response2.get(1).isEmpty());
		if (!isMock) {
			ComputerSystem system = (ComputerSystem) resource.getModel();
			List<LogicalDevice> ld = system.getLogicalDevices();
			for (LogicalDevice logicalDevice : ld) {
				if (logicalDevice instanceof LogicalPort && logicalDevice.getName().equals(interfaceToConfigure)) {
					LogicalPort logicalPort = (LogicalPort) logicalDevice;
					Assert.assertTrue(logicalPort.getOperationalStatus() == OperationalStatus.STOPPED);
				}
			}
		}
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// Assert.fail(e.getMessage());
		// }

	}

	/**
	 * This test change the interface status to down. It try to enable the administrative mode, and it will be able to be configured. Estimation: 5
	 * (the operation can be cloned fromt he upInterface) tasks: -> Create unitary test, and integration test to new feature -> Implement operation ->
	 * create template -> add modifications in the parser -> create karaf command -> test to a real router
	 * 
	 * @throws Exception
	 * 
	 * 
	 */
	public void DownInterfaceLT(String interfaceToConfigure) throws Exception {
		// try {
		// chassis:setVLAN interface VLANid
		List<String> response = KarafCommandHelper.executeCommand("chassis:down " + resourceFriendlyID + " " + interfaceToConfigure,
				commandprocessor);
		log.info(response.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());

		List<String> response1 = KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID, commandprocessor);
		log.info(response1.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());

		List<String> response2 = KarafCommandHelper.executeCommand("chassis:showInterfaces " + resourceFriendlyID, commandprocessor);
		log.info(response2.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());
		if (!isMock) {
			ComputerSystem system = (ComputerSystem) resource.getModel();
			List<LogicalDevice> ld = system.getLogicalDevices();
			for (LogicalDevice logicalDevice : ld) {
				if (logicalDevice instanceof LogicalPort && logicalDevice.getName().equals(interfaceToConfigure)) {
					LogicalPort logicalPort = (LogicalPort) logicalDevice;
					Assert.assertTrue(logicalPort.getOperationalStatus() == OperationalStatus.STOPPED);
				}
			}
		}
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// Assert.fail(e.getMessage());
		// }

	}

	public void UpInterfaceETH(String interfaceToConfigure) throws Exception {

		// chassis:setVLAN interface VLANid
		List<String> response = KarafCommandHelper.executeCommand("chassis:up " + resourceFriendlyID + " " + interfaceToConfigure,
				commandprocessor);
		log.info(response.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());

		// assert command output no contains ERROR tag

		List<String> response1 = KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID, commandprocessor);
		log.info(response1.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response1.get(1).isEmpty());

		// assert command output no contains ERROR tag

		List<String> response2 = KarafCommandHelper.executeCommand("chassis:showInterfaces " + resourceFriendlyID, commandprocessor);
		log.info(response2.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response2.get(1).isEmpty());

		// assert command output no contains ERROR tag

		// assert model updated
		if (!isMock) {
			ComputerSystem system = (ComputerSystem) resource.getModel();
			List<LogicalDevice> ld = system.getLogicalDevices();
			for (LogicalDevice logicalDevice : ld) {
				if (logicalDevice instanceof LogicalPort && logicalDevice.getName().equals(interfaceToConfigure)) {
					LogicalPort logicalPort = (LogicalPort) logicalDevice;
					Assert.assertTrue(logicalPort.getOperationalStatus() == OperationalStatus.OK);
				}
			}
		}
	}

	/**
	 * This test change the interface status to down. It try to enable the administrative mode, and it will be able to be configured. Estimation: 5
	 * (the operation can be cloned fromt he upInterface) tasks: -> Create unitary test, and integration test to new feature -> Implement operation ->
	 * create template -> add modifications in the parser -> create karaf command -> test to a real router
	 * 
	 * @throws Exception
	 * 
	 * 
	 */
	public void UpInterfaceLT(String interfaceToConfigure) throws Exception {

		// chassis:setVLAN interface VLANid
		List<String> response = KarafCommandHelper.executeCommand("chassis:up " + resourceFriendlyID + " " + interfaceToConfigure,
				commandprocessor);
		log.info(response.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());

		List<String> response1 = KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID, commandprocessor);
		log.info(response1.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());
		List<String> response2 = KarafCommandHelper.executeCommand("chassis:showInterfaces " + resourceFriendlyID, commandprocessor);
		log.info(response2.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());
		if (!isMock) {
			// assert model updated
			ComputerSystem system = (ComputerSystem) resource.getModel();
			List<LogicalDevice> ld = system.getLogicalDevices();
			for (LogicalDevice logicalDevice : ld) {
				if (logicalDevice instanceof LogicalPort && logicalDevice.getName().equals(interfaceToConfigure)) {
					LogicalPort logicalPort = (LogicalPort) logicalDevice;
					Assert.assertTrue(logicalPort.getOperationalStatus() == OperationalStatus.OK);
				}
			}
		}
	}

	@Test
	public void setVLANtest() throws Exception {

		int VLANid = 50;
		String tag = "tagged-ethernet";
		String inter = "lt-0/1/2";
		String subport = "12";

		testingMethod(inter, subport, VLANid, tag);

		// set LO

		List<String> responseError = KarafCommandHelper.executeCommand("chassis:setEncapsulation " + resourceFriendlyID + " lo0.1 1",
				commandprocessor);
		Assert.assertTrue(responseError.get(1).contains("Encapsulation in loopback interfaces is not supported."));

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

	@Test
	public void listLogicalRoutersOnResourceTest() throws Exception {

		List<String> response;
		if (isMock) {
			logicalRouterName = "cpe1";
		} else {
			logicalRouterName = "pepito";
		}
		// chassis:listLogicalRouters
		response =
				KarafCommandHelper.executeCommand("chassis:listLogicalRouters " + resourceFriendlyID,
						commandprocessor);
		// assert command output no contains ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());

		Assert.assertTrue(response.get(0).contains(logicalRouterName));

		if (!isMock) {
			Assert.assertTrue(ExistanceHelper.checkExistLogicalRouter((ComputerSystem) resource.getModel(), logicalRouterName));
		}
	}

	@Test
	public void discoveryOnBootstrapLogicalRoutersTest() throws Exception {
		if (isMock) {
			logicalRouterName = "cpe1";
		} else {
			logicalRouterName = "pepito";
		}

		// resource:list
		List<String> response =
				KarafCommandHelper.executeCommand("resource:list ", commandprocessor);
		// assert command output no contains ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());

		// check that the logical router is on the list
		Assert.assertTrue(response.get(0).contains(logicalRouterName));

		response =
				KarafCommandHelper.executeCommand("resource:info " + "router:" + logicalRouterName,
						commandprocessor);
		// assert command output no contains ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());

		// check resource initialized
		if (!isMock) {
			Assert.assertTrue(response.get(0).contains("INITIALIZED"));
		}
	}

	@Test
	public void createLogicalRouterOnRealRouterTest() throws Exception {

		List<String> response;
		List<String> response1;

		if (isMock) {
			logicalRouterName = "cpe1";
		} else {
			logicalRouterName = "pepito";
		}

		// creating LogicalRouter
		response =
				KarafCommandHelper.executeCommand("chassis:createLogicalRouter " + resourceFriendlyID + " " + logicalRouterName,
						commandprocessor);
		// assert command output no contains ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());
		response =
				KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID,
						commandprocessor);
		Assert.assertTrue(response.get(1).isEmpty());

		response1 = KarafCommandHelper.executeCommand("resource:list ",
				commandprocessor);
		Assert.assertTrue(response1.get(1).isEmpty());
		if (!isMock) {
			Assert.assertTrue(ExistanceHelper.checkExistLogicalRouter((ComputerSystem) resource.getModel(), logicalRouterName));
			Assert.assertTrue(response1.get(0).contains(logicalRouterName));
		}
	}

	@Test
	@Ignore
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

		// chassis:addInterfaceToLR R1 L1 fe-0/0/1.1
		// check interface is included in the L1
		List<String> response4 = KarafCommandHelper.executeCommand(
				"chassis:addInterfaceToLR " + resourceFriendlyID + " " + "router:" + logicalRouterName + " " + interfId4,
				commandprocessor);
		log.info(response4.get(0));

		// chassis:removeInterfaceFromLR R1 L1 fe-0/0/1.1
		// check interface is not included in the L1
		List<String> response5 = KarafCommandHelper.executeCommand(
				"chassis:removeInterfaceFromLR " + resourceFriendlyID + " " + "router:" + logicalRouterName + " " + interfId4,
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

	public void checkModel(String inter, String port, int vlanid, IResource resource) {

		Boolean found = false;
		if (!isMock) {
			ComputerSystem system = (ComputerSystem) resource.getModel();
			List<LogicalDevice> ld = system.getLogicalDevices();
			Assert.assertNotNull(ld);
			Assert.assertFalse(ld.isEmpty());
			for (LogicalDevice l : ld) {
				// Only check the modified interface
				if (l.getName().equalsIgnoreCase(inter)) {
					if (l instanceof EthernetPort) {
						EthernetPort eth = (EthernetPort) l;
						if (eth.getPortNumber() == Integer.parseInt(port)) {
							found = true;
							List<ProtocolEndpoint> pp = eth.getProtocolEndpoint();
							Assert.assertNotNull(pp);
							for (ProtocolEndpoint p : pp) {
								if (p instanceof VLANEndpoint) {
									Assert.assertEquals(vlanid, ((VLANEndpoint) p).getVlanID());
								}
							}
						}
					} else if (l instanceof LogicalTunnelPort) {
						LogicalTunnelPort lt = (LogicalTunnelPort) l;
						Assert.assertNotNull(lt.getPeer_unit());
						if (lt.getPortNumber() == Integer.parseInt(port)) {
							found = true;
							List<ProtocolEndpoint> pp = lt.getProtocolEndpoint();
							Assert.assertNotNull(pp);
							for (ProtocolEndpoint p : pp) {
								if (p instanceof VLANEndpoint) {
									Assert.assertEquals(vlanid, ((VLANEndpoint) p).getVlanID());
								}
							}

						}
					}

				}
			}
			// Look if exits the interface to be checked
			Assert.assertTrue(found);
		}

	}

	public int getOldInterface(IResource resource, String inter, String port) {

		ComputerSystem system = (ComputerSystem) resource.getModel();
		List<LogicalDevice> ld = system.getLogicalDevices();
		Assert.assertNotNull(ld);
		Assert.assertFalse(ld.isEmpty());
		LogicalPort iface = null;
		for (LogicalDevice l : ld) {
			// Only check the modified interface
			if (l.getName().equalsIgnoreCase(inter)) {

				if (l instanceof EthernetPort) {
					EthernetPort eth = (EthernetPort) l;
					if (eth.getPortNumber() == Integer.parseInt(port)) {
						iface = eth;
						break;
					}
				} else if (l instanceof LogicalTunnelPort) {
					LogicalTunnelPort lt = (LogicalTunnelPort) l;
					if (lt.getPortNumber() == Integer.parseInt(port)) {
						iface = lt;
						break;
					}
				}
			}
		}
		if (iface == null)
			Assert.fail("Interface not found");

		List<ProtocolEndpoint> pp = iface.getProtocolEndpoint();
		Assert.assertNotNull(pp);
		for (ProtocolEndpoint p : pp) {
			if (p instanceof VLANEndpoint) {
				return ((VLANEndpoint) p).getVlanID();
			}
		}

		Assert.fail("Interface has no VLAN");
		return 0;
	}

	public void testingMethod(String inter, String subport, int VLANid, String tag) throws Exception {

		// REFRESH to fill up the model
		List<String> responseError = KarafCommandHelper.executeCommand("chassis:showInterfaces " + resourceFriendlyID, commandprocessor);
		// assert command output no contains ERROR tag
		Assert.assertTrue(responseError.get(1).isEmpty());

		// Obtain the previous IP/MASK make the rollback of the test
		int OldVLAN = getOldInterface(resource, inter, subport);

		// SET NEW VLAN
		responseError = KarafCommandHelper.executeCommand(
				"chassis:setEncapsulation " + resourceFriendlyID + " " + inter + "." + subport + " " + tag
				, commandprocessor);
		// assert command output no contains ERROR tag
		if (!responseError.get(1).isEmpty()) {
			Assert.fail(responseError.get(1));
		}
		Assert.assertTrue(responseError.get(1).isEmpty());

		responseError = KarafCommandHelper.executeCommand(
				"chassis:setEncapsulationLabel " + resourceFriendlyID + " " + inter + "." + subport + " " + VLANid
				, commandprocessor);

		if (!responseError.get(1).isEmpty()) {
			Assert.fail(responseError.get(1));
		}
		Assert.assertTrue(responseError.get(1).isEmpty());

		responseError = KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID, commandprocessor);
		// assert command output no contains ERROR tag
		Assert.assertTrue(responseError.get(1).isEmpty());

		// Check that the resource have the old VLAN in the model despite of have send the command
		checkModel(inter, subport, OldVLAN, resource);

		// REFRESH to fill up the model
		responseError = KarafCommandHelper.executeCommand("chassis:showInterfaces " + resourceFriendlyID, commandprocessor);
		// assert command output no contains ERROR tag
		Assert.assertTrue(responseError.get(1).isEmpty());

		// CHECK CHANGES IN THE INTERFACE with new VLAN
		checkModel(inter, subport, VLANid, resource);

		// ROLLBACK OF THE INTERFACE
		responseError = KarafCommandHelper.executeCommand(
				"chassis:setEncapsulation " + resourceFriendlyID + " " + inter + "." + subport + " " + tag
				, commandprocessor);
		Assert.assertTrue(responseError.get(1).isEmpty());
		responseError = KarafCommandHelper.executeCommand("queue:execute  " + resourceFriendlyID, commandprocessor);
		Assert.assertTrue(responseError.get(1).isEmpty());

		// REFRESH to fill up the model
		responseError = KarafCommandHelper.executeCommand("chassis:showInterfaces " + resourceFriendlyID, commandprocessor);
		// assert command output no contains ERROR tag
		Assert.assertTrue(responseError.get(1).isEmpty());

		// CHECK THe ROLLBACK IS DONE
		checkModel(inter, subport, OldVLAN, resource);

	}

	public static boolean checkExistLogicalRouter(ComputerSystem physicalRouter, String logicalRouterName) {
		List<System> logicalRouters = physicalRouter.getSystems();
		for (System logicalRouter : logicalRouters) {
			if (logicalRouter.getName().equals(logicalRouterName))
				return true;
		}
		return false;
	}
}