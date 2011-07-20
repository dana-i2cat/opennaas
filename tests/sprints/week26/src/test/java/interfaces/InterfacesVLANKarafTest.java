package interfaces;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;
import helpers.IntegrationTestsHelper;
import helpers.KarafCommandHelper;
import helpers.ProtocolSessionHelper;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.model.LogicalTunnelPort;
import net.i2cat.mantychore.model.ProtocolEndpoint;
import net.i2cat.mantychore.model.VLANEndpoint;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;
import net.i2cat.nexus.resources.helpers.ResourceDescriptorFactory;
import net.i2cat.nexus.resources.protocol.IProtocolManager;
import net.i2cat.nexus.resources.protocol.ProtocolException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.service.command.CommandProcessor;

@RunWith(JUnit4TestRunner.class)
public class InterfacesVLANKarafTest extends AbstractIntegrationTest {
	static Log					log	= LogFactory
														.getLog(InterfacesVLANKarafTest.class);

	String						resourceFriendlyID;
	IResource					resource;
	private CommandProcessor	commandprocessor;
	private IResourceManager	resourceManager;

	@Configuration
	public static Option[] configuration() throws Exception {

		Option[] options = combine(
				IntegrationTestsHelper.getMantychoreTestOptions(),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.tests.helper")
					// , vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
					);

		return options;
	}

	@Before
	public void initBundles() {
		log.info("Waiting to load all bundles");
		/* Wait for the activation of all the bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		log.info("Loaded all bundles");
		resourceManager = getOsgiService(IResourceManager.class, 5000);
		commandprocessor = getOsgiService(CommandProcessor.class);
		initTest();

	}

	public void createProtocolForResource(String resourceId) throws ProtocolException {
		IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 5000);
		protocolManager.getProtocolSessionManagerWithContext(resourceId, ProtocolSessionHelper.newSessionContextNetconf());

	}

	public void initTest() {

		List<String> capabilities = new ArrayList<String>();

		capabilities.add("chassis");
		capabilities.add("queue");

		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("junosm20", "router", capabilities);
		resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		try {
			resource = resourceManager.createResource(resourceDescriptor);
			createProtocolForResource(resource.getResourceIdentifier().getId());
			resourceManager.startResource(resource.getResourceIdentifier());

			// call the command to initialize the model

		} catch (ResourceException e) {

			e.printStackTrace();
			Assert.fail(e.getMessage());
		} catch (ProtocolException e) {

			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	@After
	public void deleteResource() {
		try {
			resourceManager.stopResource(resource.getResourceIdentifier());
			resourceManager.removeResource(resource.getResourceIdentifier());
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ResourceException e) {
			Assert.fail();
		}

	}

	@Test
	public void setVLANtest() {
		// SET ETH
		int VLANid = 50;
		String inter = "fe-0/3/1";
		String subport = "30";

		try {
			testingMethod(inter, subport, VLANid);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}

		// SET LT
		VLANid = 223;
		inter = "lt-1/2/0";
		subport = "121";

		try {
			testingMethod(inter, subport, VLANid);
		} catch (Exception e1) {
			e1.printStackTrace();
			Assert.fail(e1.getLocalizedMessage());
		}

		// set LO
		try {
			List<String> responseError = KarafCommandHelper.executeCommand("chassis:setVLAN " + resourceFriendlyID + " lo0.0 1",
					commandprocessor);
			Assert.assertTrue(responseError.get(1).contains("[ERROR] Not allowed VLAN configuration for loopback interface"));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	public void checkModel(String inter, String port, int vlanid, IResource resource) {

		Boolean found = false;

		ComputerSystem system = (ComputerSystem) resource.getModel();
		List<LogicalDevice> ld = system.getLogicalDevices();
		Assert.assertNotNull(ld);
		for (LogicalDevice l : ld) {
			// Only check the modified interface
			if (l.getElementName().equalsIgnoreCase(inter)) {
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

	public int getOldInterface(IResource resource, String inter, String port) {

		ComputerSystem system = (ComputerSystem) resource.getModel();
		List<LogicalDevice> ld = system.getLogicalDevices();
		Assert.assertNotNull(ld);
		for (LogicalDevice l : ld) {
			// Only check the modified interface
			if (l.getElementName().equalsIgnoreCase(inter)) {

				if (l instanceof EthernetPort) {
					EthernetPort eth = (EthernetPort) l;
					if (eth.getPortNumber() == Integer.parseInt(port)) {
						List<ProtocolEndpoint> pp = eth.getProtocolEndpoint();
						Assert.assertNotNull(pp);
						for (ProtocolEndpoint p : pp) {
							if (p instanceof VLANEndpoint) {
								return ((VLANEndpoint) p).getVlanID();
							}
						}
					}
				} else if (l instanceof LogicalTunnelPort) {
					LogicalTunnelPort lt = (LogicalTunnelPort) l;
					if (lt.getPortNumber() == Integer.parseInt(port)) {
						List<ProtocolEndpoint> pp = lt.getProtocolEndpoint();
						Assert.assertNotNull(pp);
						for (ProtocolEndpoint p : pp) {
							if (p instanceof VLANEndpoint) {
								return ((VLANEndpoint) p).getVlanID();
							}
						}

					}
				}
			}
		}
		Assert.fail("Interface not found");
		return 0;
	}

	public void testingMethod(String inter, String subport, int VLANid) throws Exception {

		// REFRESH to fill up the model
		List<String> responseError = KarafCommandHelper.executeCommand("chassis:showInterfaces -r " + resourceFriendlyID, commandprocessor);
		// assert command output no contains ERROR tag
		Assert.assertTrue(responseError.get(1).isEmpty());

		// Obtain the previous IP/MASK make the rollback of the test
		int OldVLAN = getOldInterface(resource, inter, subport);

		// SET NEW VLAN
		responseError = KarafCommandHelper.executeCommand("chassis:setVLAN " + resourceFriendlyID + " " + inter + "." + subport + " " + VLANid
					, commandprocessor);
		// assert command output no contains ERROR tag
		Assert.assertTrue(responseError.get(1).isEmpty());
		responseError = KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID, commandprocessor);
		// assert command output no contains ERROR tag
		Assert.assertTrue(responseError.get(1).isEmpty());

		// Check that the resource have the old VLAN in the model despite of have send the command
		checkModel(inter, subport, OldVLAN, resource);

		// REFRESH to fill up the model
		responseError = KarafCommandHelper.executeCommand("chassis:showInterfaces -r " + resourceFriendlyID, commandprocessor);
		// assert command output no contains ERROR tag
		Assert.assertTrue(responseError.get(1).isEmpty());

		// CHECK CHANGES IN THE INTERFACE with new VLAN
		checkModel(inter, subport, VLANid, resource);

		// ROLLBACK OF THE INTERFACE
		responseError = KarafCommandHelper.executeCommand("chassis:setVLAN " + resourceFriendlyID + " " + inter + "." + subport + " " + OldVLAN
							, commandprocessor);
		Assert.assertTrue(responseError.get(1).isEmpty());
		responseError = KarafCommandHelper.executeCommand("queue:execute  " + resourceFriendlyID, commandprocessor);
		Assert.assertTrue(responseError.get(1).isEmpty());

		// REFRESH to fill up the model
		responseError = KarafCommandHelper.executeCommand("chassis:showInterfaces -r " + resourceFriendlyID, commandprocessor);
		// assert command output no contains ERROR tag
		Assert.assertTrue(responseError.get(1).isEmpty());

		// CHECK THe ROLLBACK IS DONE
		checkModel(inter, subport, OldVLAN, resource);

	}
}
