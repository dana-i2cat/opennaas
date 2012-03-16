package interfaces;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.model.LogicalPort;
import net.i2cat.mantychore.model.LogicalTunnelPort;
import net.i2cat.mantychore.model.ProtocolEndpoint;
import net.i2cat.mantychore.model.VLANEndpoint;
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
import org.osgi.framework.Constants;
import org.osgi.service.blueprint.container.BlueprintContainer;

import static net.i2cat.nexus.tests.OpennaasExamOptions.*;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.*;
import static org.ops4j.pax.exam.CoreOptions.*;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class InterfacesVLANKarafTest
{
	private final static Log	log	= LogFactory.getLog(InterfacesVLANKarafTest.class);

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
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.repository)")
    private BlueprintContainer routerService;

    @Inject
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.queuemanager)")
    private BlueprintContainer queueService;

    @Inject
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.capability.chassis)")
    private BlueprintContainer chassisService;

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
	public void initTest() throws ResourceException, ProtocolException {

		List<String> capabilities = new ArrayList<String>();

		capabilities.add("chassis");
		capabilities.add("queue");

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

	@Test
	public void setVLANtest() throws Exception {

		// SET ETH
		int VLANid = 50;
		// For Real Router Test
		// String inter = "fe-0/3/1";
		// String subport = "30";

		// For mock TEST
		String inter = "fe-0/1/3";
		String subport = "15";

		testingMethod(inter, subport, VLANid);

		// SET LT
		VLANid = 223;

		// For Real Router Test
		// inter = "lt-1/2/0";
		// subport = "121";

		// For mock TEST
		inter = "lt-0/1/2";
		subport = "12";

		testingMethod(inter, subport, VLANid);

		// set LO
		List<String> responseError = KarafCommandHelper.executeCommand("chassis:setEncapsulation " + resourceFriendlyID + " lo0.1 1",
																	   commandprocessor);
		Assert.assertTrue(responseError.get(1).contains("[ERROR] Not allowed VLAN configuration for loopback interface"));
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

	public void testingMethod(String inter, String subport, int VLANid) throws Exception {

		// REFRESH to fill up the model
		List<String> responseError = KarafCommandHelper.executeCommand("chassis:showInterfaces " + resourceFriendlyID, commandprocessor);
		// assert command output no contains ERROR tag
		Assert.assertTrue(responseError.get(1).isEmpty());

		// Obtain the previous IP/MASK make the rollback of the test
		int OldVLAN = getOldInterface(resource, inter, subport);

		// SET NEW VLAN
		responseError = KarafCommandHelper.executeCommand(
				"chassis:setEncapsulation " + resourceFriendlyID + " " + inter + "." + subport + " " + VLANid
					, commandprocessor);
		// assert command output no contains ERROR tag
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
				"chassis:setEncapsulation " + resourceFriendlyID + " " + inter + "." + subport + " " + OldVLAN
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
}
