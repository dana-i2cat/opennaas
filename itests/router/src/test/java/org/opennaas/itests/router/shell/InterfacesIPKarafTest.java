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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IResource;
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
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.LogicalTunnelPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
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

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class InterfacesIPKarafTest
{
	private final static Log	log		= LogFactory.getLog(InterfacesIPKarafTest.class);
	private String				resourceFriendlyID;
	private IResource			resource;
	private boolean				isMock	= false;

	@Inject
	private IProtocolManager	protocolManager;

	@Inject
	private IResourceManager	resourceManager;

	@Inject
	private CommandProcessor	commandprocessor;

	@Inject
	private BundleContext		bundleContext;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.repository)")
	private BlueprintContainer	routerService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.queuemanager)")
	private BlueprintContainer	queueService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.capability.ip)")
	private BlueprintContainer	ipService;

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

	@After
	public void deleteResource() throws InterruptedException, ResourceException {
		resourceManager.stopResource(resource.getResourceIdentifier());
		resourceManager.removeResource(resource.getResourceIdentifier());
		for (IResource resource : resourceManager.listResources()) {
			resourceManager.removeResource(resource.getResourceIdentifier());
		}
	}

	@Before
	public void initTest() throws ProtocolException, ResourceException {

		List<String> capabilities = new ArrayList<String>();

		capabilities.add("ipv4");
		capabilities.add("queue");

		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("junosm20", "router", capabilities);
		resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();
		resource = resourceManager.createResource(resourceDescriptor);
		isMock = createProtocolForResource(resource.getResourceIdentifier().getId());
		resourceManager.startResource(resource.getResourceIdentifier());
	}

	@Test
	public void setIPTest() throws Exception {

		// SEt LT
		String newIp = "192.168.1.6";
		String newMask = "255.255.255.0";

		// For Real Router Test
		// String inter = "lt-0/1/2";

		// For mock TEST
		String inter = "lt-0/1/2";
		String subport = "12";

		testingMethod(inter, subport, newIp, newMask);

		// SEt ETH
		newIp = "192.168.1.4";
		newMask = "255.255.255.0";

		// For Real Router Test
		// inter = "fe-0/3/1";

		// For mock TEST
		inter = "fe-0/1/3";
		subport = "0";
		testingMethod(inter, subport, newIp, newMask);

		// LO

		List<String> response = KarafCommandHelper.executeCommand("ipv4:setIP " + resourceFriendlyID + " lo0.1 192.168.1.1 255.255.255.0",
				commandprocessor);
		//
		// assert command output contains "[ERROR] Configuration for Loopback interface not allowed"
		// Assert.assertTrue(response.contains("[ERROR] Configuration for Loopback interface not allowed"));
		Assert.assertTrue(response.get(1).contains("[ERROR] Configuration for Loopback interface not allowed"));
	}

	/**
	 * Configure a IP in a lt interface
	 * 
	 */

	public void setIPlt() throws Exception {
		String newIp = "192.168.1.6";
		String newMask = "255.255.255.0";
		String inter = "lt-0/1/2";
		String subport = "12";

		testingMethod(inter, subport, newIp, newMask);

		// SET LO
		List<String> response = KarafCommandHelper.executeCommand("ipv4:setIP " + resourceFriendlyID + " lo0.1 192.168.1.1 255.255.255.0",
				commandprocessor);
		//
		// assert command output contains "[ERROR] Configuration for Loopback interface not allowed"
		Assert.assertTrue(response.get(1).contains("[ERROR] Configuration for Loopback interface not allowed"));
	}

	/**
	 * Configure a IP in a ETh interface
	 * 
	 */

	public void setIPETH() throws Exception {

		String newIp = "192.168.1.4";
		String newMask = "255.255.255.0";
		String inter = "fe-0/3/1";
		String subport = "0";

		testingMethod(inter, subport, newIp, newMask);
	}

	public void testingMethod(String inter, String subport, String newIp, String newMask) throws Exception {
		// REFRESH to fill up the model
		List<String> response = KarafCommandHelper.executeCommand("ipv4:list " + resourceFriendlyID, commandprocessor);
		Assert.assertTrue(response.get(1).isEmpty());

		// Obtain the previous IP/MASK make the rollback of the test
		List<String> OldIPMask = getOldInterface(resource, inter, subport);
		// SET NEW IP
		response = KarafCommandHelper.executeCommand(
				"ipv4:setIP  " + resourceFriendlyID + " " + inter + "." + subport + " " + newIp + " " + newMask,
				commandprocessor);
		Assert.assertTrue(response.get(1).isEmpty());
		response = KarafCommandHelper.executeCommand("queue:execute  " + resourceFriendlyID, commandprocessor);
		Assert.assertTrue(response.get(1).isEmpty());

		// Check that the resource have the old IP configured in the model despite of have send the command
		checkModel(inter, subport, OldIPMask.get(0), OldIPMask.get(1), resource);

		// REFRESH to fill up the model
		response = KarafCommandHelper.executeCommand("ipv4:list " + resourceFriendlyID, commandprocessor);
		Assert.assertTrue(response.get(1).isEmpty());

		// CHECK CHANGES IN THE INTERFACE
		checkModel(inter, subport, newIp, newMask, resource);

		// ROLLBACK OF THE INTERFACE
		response = KarafCommandHelper.executeCommand(
				"ipv4:setIP  " + resourceFriendlyID + " " + inter + "." + subport + " " + OldIPMask.get(0) + " " + OldIPMask.get(1),
				commandprocessor);
		Assert.assertTrue(response.get(1).isEmpty());
		response = KarafCommandHelper.executeCommand("queue:execute  " + resourceFriendlyID, commandprocessor);
		Assert.assertTrue(response.get(1).isEmpty());

		// REFRESH
		response = KarafCommandHelper.executeCommand("ipv4:list " + resourceFriendlyID, commandprocessor);
		Assert.assertTrue(response.get(1).isEmpty());
		// CHECK THe ROLLBACK IS DONE
		checkModel(inter, subport, OldIPMask.get(0), OldIPMask.get(1), resource);

	}

	public void checkModel(String inter, String port, String Ip, String mask, IResource resource) {

		Boolean found = false;
		if (!isMock) {
			ComputerSystem system = (ComputerSystem) resource.getModel();
			List<LogicalDevice> ld = system.getLogicalDevices();
			Assert.assertNotNull(ld);
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
								if (p instanceof IPProtocolEndpoint) {
									Assert.assertEquals(Ip, ((IPProtocolEndpoint) p).getIPv4Address());
									Assert.assertEquals(mask, ((IPProtocolEndpoint) p).getSubnetMask());
								}
							}
						}
					} else if (l instanceof LogicalTunnelPort) {
						LogicalTunnelPort lt = (LogicalTunnelPort) l;
						if (lt.getPortNumber() == Integer.parseInt(port)) {
							found = true;
							List<ProtocolEndpoint> pp = lt.getProtocolEndpoint();
							Assert.assertNotNull(pp);
							for (ProtocolEndpoint p : pp) {
								if (p instanceof IPProtocolEndpoint) {
									Assert.assertEquals(Ip, ((IPProtocolEndpoint) p).getIPv4Address());
									Assert.assertEquals(mask, ((IPProtocolEndpoint) p).getSubnetMask());
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

	public List<String> getOldInterface(IResource resource, String inter, String port) {

		List<String> result = new ArrayList<String>();
		ComputerSystem system = (ComputerSystem) resource.getModel();
		List<LogicalDevice> ld = system.getLogicalDevices();
		Assert.assertNotNull(ld);
		for (LogicalDevice l : ld) {
			// Only check the modified interface
			if (l.getName().equalsIgnoreCase(inter)) {

				if (l instanceof EthernetPort) {
					EthernetPort eth = (EthernetPort) l;
					if (eth.getPortNumber() == Integer.parseInt(port)) {
						List<ProtocolEndpoint> pp = eth.getProtocolEndpoint();
						Assert.assertNotNull(pp);
						for (ProtocolEndpoint p : pp) {
							if (p instanceof IPProtocolEndpoint) {
								result.add(((IPProtocolEndpoint) p).getIPv4Address());
								result.add(((IPProtocolEndpoint) p).getSubnetMask());
							}
						}
					}
				} else if (l instanceof LogicalTunnelPort) {
					LogicalTunnelPort lt = (LogicalTunnelPort) l;
					if (lt.getPortNumber() == Integer.parseInt(port)) {
						List<ProtocolEndpoint> pp = lt.getProtocolEndpoint();
						Assert.assertNotNull(pp);
						for (ProtocolEndpoint p : pp) {
							if (p instanceof IPProtocolEndpoint) {
								result.add(((IPProtocolEndpoint) p).getIPv4Address());
								result.add(((IPProtocolEndpoint) p).getSubnetMask());
							}
						}

					}
				}
			}
		}
		Assert.assertFalse(result.isEmpty());
		Assert.assertNotNull(result);

		return result;

	}

}
