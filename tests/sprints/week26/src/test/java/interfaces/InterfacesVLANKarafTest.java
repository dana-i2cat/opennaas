package interfaces;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;
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
import net.i2cat.nexus.tests.IntegrationTestsHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
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

	/**
	 * Configure a VLAN in a ethernet interface
	 */
	// @Test
	public void setVLANforEth() {

		try {

			List<String> responseError = KarafCommandHelper.executeCommand("chassis:showInterfaces -r " + resourceFriendlyID, commandprocessor);
			log.info(responseError.get(0));

			// assert command output no contains ERROR tag
			Assert.assertTrue(responseError.get(1).isEmpty());

			// previous configuration
			// fe-0/3/1.30 vlan 30

			// chassis:setVLAN interface VLANid
			responseError = KarafCommandHelper.executeCommand("chassis:setVLAN " + resourceFriendlyID + " fe-0/3/1.30 30"
					, commandprocessor);
			// assert command output no contains ERROR tag
			Assert.assertTrue(responseError.get(1).isEmpty());

			responseError = KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID, commandprocessor);
			// assert command output no contains ERROR tag
			log.info(responseError.get(0));
			Assert.assertTrue(responseError.get(1).isEmpty());

			responseError = KarafCommandHelper.executeCommand("chassis:showInterfaces -r " + resourceFriendlyID, commandprocessor);
			log.info(responseError.get(0));
			// assert command output no contains ERROR tag
			Assert.assertTrue(responseError.get(1).isEmpty());

			ComputerSystem system = (ComputerSystem) resource.getModel();
			List<LogicalDevice> ld = system.getLogicalDevices();
			Assert.assertNotNull(ld);
			for (LogicalDevice l : ld) {
				if (l instanceof EthernetPort) {

					EthernetPort ethport = new EthernetPort();
					ethport = (EthernetPort) l;
					// Only check the modified interface
					if (ethport.getElementName().equalsIgnoreCase("fe-0/3/1")) {
						if (ethport.getPortNumber() == 30) {
							List<ProtocolEndpoint> pp = ethport.getProtocolEndpoint();
							Assert.assertNotNull(pp);
							for (ProtocolEndpoint p : pp) {
								if (p instanceof VLANEndpoint) {

									Assert.assertEquals(((VLANEndpoint) p).getVlanID(), 30);
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
			Assert.fail(e.getCause().getLocalizedMessage());
		}

	}

	/**
	 * Configure a VLAN in a LT interface
	 */
	@Test
	public void setVLANforLT() {
		try {

			List<String> responseError = KarafCommandHelper.executeCommand("chassis:showInterfaces -r " + resourceFriendlyID, commandprocessor);
			log.info(responseError.get(0));

			// assert command output no contains ERROR tag
			Assert.assertTrue(responseError.get(1).isEmpty());

			// previous configuration
			// lt-1/2/0.121 peer-unit 2 vlan 222

			// chassis:setVLAN interface VLANid
			responseError = KarafCommandHelper.executeCommand("chassis:setVLAN " + resourceFriendlyID + " lt-1/2/0.121 222"
					, commandprocessor);
			// assert command output no contains ERROR tag
			Assert.assertTrue(responseError.get(1).isEmpty());

			responseError = KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID, commandprocessor);
			// assert command output no contains ERROR tag
			log.info(responseError.get(0));
			Assert.assertTrue(responseError.get(1).isEmpty());

			responseError = KarafCommandHelper.executeCommand("chassis:showInterfaces -r " + resourceFriendlyID, commandprocessor);
			log.info(responseError.get(0));
			// assert command output no contains ERROR tag
			Assert.assertTrue(responseError.get(1).isEmpty());

			ComputerSystem system = (ComputerSystem) resource.getModel();

			List<LogicalDevice> ld = system.getLogicalDevices();
			for (LogicalDevice l : ld) {
				if (l instanceof LogicalTunnelPort) {
					LogicalTunnelPort ltp = (LogicalTunnelPort) l;
					// show data of LT
					// name, peer-unit

					// Only check the modified interface
					if (ltp.getElementName().equalsIgnoreCase("lt-0/1/2")) {
						Assert.assertNotNull(ltp.getPeer_unit());

						if (ltp.getPortNumber() == 121) {
							Assert.assertEquals(121, ltp.getPortNumber());
							List<ProtocolEndpoint> pp = ltp.getProtocolEndpoint();
							Assert.assertNotNull(pp);
							for (ProtocolEndpoint p : pp) {
								if (p instanceof VLANEndpoint) {
									log.info(((VLANEndpoint) p).getVlanID());
									// show tha VLAN setted for this LT
									Assert.assertEquals(((VLANEndpoint) p).getVlanID(), 222);
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

			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	/**
	 * Configure a VLAN in a Lo interface
	 */
	@Test
	public void setVLANforLo() {

		try {
			List<String> responseError = KarafCommandHelper.executeCommand("chassis:setVLAN " + resourceFriendlyID + " lo0.0 1",
					commandprocessor);
			Assert.assertTrue(responseError.get(1).contains("[ERROR] Not allowed VLAN configuration for loopback interface"));
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
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}
}
