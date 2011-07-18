package interfaces;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;
import helpers.KarafCommandHelper;
import helpers.ProtocolSessionHelper;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.model.LogicalTunnelPort;
import net.i2cat.mantychore.model.ProtocolEndpoint;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;
import net.i2cat.nexus.resources.helpers.ResourceDescriptorFactory;
import net.i2cat.nexus.resources.protocol.IProtocolManager;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

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
public class InterfacesIPKarafTest extends AbstractIntegrationTest {
	static Log					log				= LogFactory
														.getLog(InterfacesIPKarafTest.class);
	IResourceManager			resourceManager;
	String						resourceFriendlyID;
	IResource					resource;
	private CommandProcessor	commandprocessor;
	@Inject
	BundleContext				bundleContext	= null;

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

		/* init capability */

		log.info("This is running inside Equinox. With all configuration set up like you specified. ");

		resourceManager = getOsgiService(IResourceManager.class);

		log.info("INFO: Initialized!");
		commandprocessor = getOsgiService(CommandProcessor.class);
		initTest();

	}

	public void createProtocolForResource(String resourceId) throws ProtocolException {
		IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 5000);
		protocolManager.getProtocolSessionManagerWithContext(resourceId, ProtocolSessionHelper.newSessionContextNetconf());

	}

	public void initTest() {

		List<String> capabilities = new ArrayList<String>();

		capabilities.add("ipv4");
		capabilities.add("queue");

		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("junosm20", "router", capabilities);
		resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();
		try {
			resource = resourceManager.createResource(resourceDescriptor);
			createProtocolForResource(resource.getResourceIdentifier().getId());
			resourceManager.startResource(resource.getResourceIdentifier());

			// call the command to initialize the model
		} catch (ResourceException e) {
			Assert.fail(e.getMessage());
		} catch (ProtocolException e) {
			Assert.fail(e.getMessage());
		}

	}

	/**
	 * Configure a IP in a lt interface
	 * 
	 */

	// @Test
	public void setIPlt() {

		try {

			List<String> response = KarafCommandHelper.executeCommand("ipv4:list -r " + resourceFriendlyID, commandprocessor);

			Assert.assertTrue(response.get(1).isEmpty());

			response = KarafCommandHelper.executeCommand("ipv4:setIP " + resourceFriendlyID + " lt-0/1/2.12 192.168.1.1 255.255.255.0",
					commandprocessor);

			// assert command output no contains ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());

			response = KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID, commandprocessor);
			// assert command output no contains ERROR tag

			Assert.assertTrue(response.get(1).isEmpty());

			response = KarafCommandHelper.executeCommand("ipv4:list -r " + resourceFriendlyID, commandprocessor);
			log.info(response.get(0));

			// assert command output no contains ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());
			ComputerSystem system = (ComputerSystem) resource.getModel();
			List<LogicalDevice> ld = system.getLogicalDevices();
			Assert.assertNotNull(ld);
			for (LogicalDevice l : ld) {
				if (l instanceof LogicalTunnelPort) {
					LogicalTunnelPort lt = (LogicalTunnelPort) l;
					// Only check the modified interface
					if (lt.getElementName().equalsIgnoreCase("lt-0/1/2")) {
						if (lt.getPortNumber() == 12) {
							List<ProtocolEndpoint> pp = lt.getProtocolEndpoint();
							Assert.assertNotNull(pp);
							for (ProtocolEndpoint p : pp) {
								if (p instanceof IPProtocolEndpoint) {

									Assert.assertEquals("192.168.1.1", ((IPProtocolEndpoint) p).getIPv4Address());
									Assert.assertEquals("255.255.255.0", ((IPProtocolEndpoint) p).getSubnetMask());
								}
							}
						}
					}
				}
			}

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

		} catch (Exception e) {
			Assert.fail(e.getLocalizedMessage());
		}

	}

	/**
	 * Configure a IP in a l0 interface
	 * 
	 */

	// @Test
	public void setIPl0() {

		try {

			List<String> response = KarafCommandHelper.executeCommand("ipv4:setIP " + resourceFriendlyID + " lo0.1 192.168.1.1 255.255.255.0",
					commandprocessor);
			//
			// assert command output contains "[ERROR] Configuration for Loopback interface not allowed"
			// Assert.assertTrue(response.contains("[ERROR] Configuration for Loopback interface not allowed"));
			Assert.assertTrue(response.get(1).contains("[ERROR] Configuration for Loopback interface not allowed"));
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
		} catch (Exception e) {

			Assert.fail(e.getLocalizedMessage());
		}
	}

	/**
	 * Configure a IP in a ETh interface
	 * 
	 */
	@Test
	public void setIPETH() {

		try {

			List<String> response = KarafCommandHelper.executeCommand("ipv4:list -r " + resourceFriendlyID, commandprocessor);

			Assert.assertTrue(response.get(1).isEmpty());

			response = KarafCommandHelper.executeCommand("ipv4:setIP  " + resourceFriendlyID + " fe-0/3/1.0 192.168.1.1 255.255.255.0",
					commandprocessor);

			// assert command output no contains ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());

			response = KarafCommandHelper.executeCommand("queue:execute  " + resourceFriendlyID, commandprocessor);

			// assert command output no contains ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());

			response = KarafCommandHelper.executeCommand("ipv4:list -r " + resourceFriendlyID, commandprocessor);
			// assert command output no contains ERROR tag

			// assert command output no contains ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());

			ComputerSystem system = (ComputerSystem) resource.getModel();
			List<LogicalDevice> ld = system.getLogicalDevices();
			Assert.assertNotNull(ld);
			for (LogicalDevice l : ld) {
				if (l instanceof EthernetPort) {
					EthernetPort eth = (EthernetPort) l;
					// Only check the modified interface
					if (eth.getElementName().equalsIgnoreCase("fe-0/3/1")) {
						if (eth.getPortNumber() == 0) {
							List<ProtocolEndpoint> pp = eth.getProtocolEndpoint();
							Assert.assertNotNull(pp);
							for (ProtocolEndpoint p : pp) {
								if (p instanceof IPProtocolEndpoint) {
									Assert.assertEquals("192.168.1.1", ((IPProtocolEndpoint) p).getIPv4Address());
									Assert.assertEquals("255.255.255.0", ((IPProtocolEndpoint) p).getSubnetMask());
								}
							}
						}
					}
				}
			}
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
		} catch (Exception e) {
			log.info(e.getMessage());
			Assert.fail(e.getLocalizedMessage());
		}

	}
}
