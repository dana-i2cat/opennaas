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
import net.i2cat.nexus.resources.IResourceRepository;
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
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.osgi.service.command.CommandProcessor;

@RunWith(JUnit4TestRunner.class)
public class InterfacesVLANKarafTest extends AbstractIntegrationTest {
	static Log					log				= LogFactory
														.getLog(InterfacesVLANKarafTest.class);
	IResourceRepository			repository;
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
		repository = getOsgiService(IResourceRepository.class, 50000);
		commandprocessor = getOsgiService(CommandProcessor.class);
		initTest();

	}

	public void initTest() {

		List<String> capabilities = new ArrayList<String>();

		capabilities.add("chassis");
		capabilities.add("queue");

		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("junosm20", "router", capabilities);
		resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		try {
			resource = repository.createResource(resourceDescriptor);
			IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 5000);
			protocolManager.getProtocolSessionManagerWithContext(resource.getResourceIdentifier().getId(), ProtocolSessionHelper
					.newSessionContextNetconf());
			repository.startResource(resource.getResourceDescriptor().getId());

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
	public void resetRepository() {

		try {
			repository.stopResource(resource.getResourceIdentifier().getId());
			repository.removeResource(resource.getResourceIdentifier().getId());
		} catch (ResourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}

	}

	/**
	 * Configure a VLAN in a ethernet interface
	 */
	@Test
	public void setVLANforEth() {

		try {

			// chassis:setVLAN interface VLANid
			String responseError = KarafCommandHelper.executeCommand("chassis:setVLAN " + resourceFriendlyID + " fe-0/0/1.12 1",
					getOsgiService(CommandProcessor.class));

			// assert command output no contains ERROR tag

			responseError = KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID, getOsgiService(CommandProcessor.class));
			// assert command output no contains ERROR tag

			String response2 = KarafCommandHelper.executeCommand("chassis:showInterfaces " + resourceFriendlyID + " -r", commandprocessor);

			// assert command output no contains ERROR tag

			//
			ComputerSystem system = (ComputerSystem) resource.getModel();
			List<LogicalDevice> ld = system.getLogicalDevices();
			for (LogicalDevice l : ld) {
				if (l instanceof EthernetPort) {
					EthernetPort ethport = (EthernetPort) l;

					// show data of ETH
					// name, linkTecnology
					// Only check the modified interface
					if (ethport.getElementName().equalsIgnoreCase("fe-0/0/1")) {

						if (ethport.getPortNumber() == 0) {
							Assert.assertEquals(ethport.getLinkTechnology().toString(), "ETHERNET");
						} else {
							Assert.assertNotSame(ethport.getLinkTechnology().toString(), "ETHERNET");
						}

						List<ProtocolEndpoint> pp = ethport.getProtocolEndpoint();
						for (ProtocolEndpoint p : pp) {
							if (p instanceof VLANEndpoint) {
								// show tha VLAN setted for this LT
								Assert.assertEquals(((VLANEndpoint) p).getVlanID(), 1);
							}

						}
					}
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	/**
	 * Configure a VLAN in a LT interface
	 */
	@Test
	public void setVLANforLT() {
		try {
			// chassis:setVLAN interface -pu PeerUnit
			String responseError = KarafCommandHelper.executeCommand("chassis:setVLAN " + resourceFriendlyID + " lt-0/1/2.12 1",
					getOsgiService(CommandProcessor.class));

			// assert command output no contains ERROR tag

			responseError = KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID, getOsgiService(CommandProcessor.class));
			// assert command output no contains ERROR tag

			String response2 = KarafCommandHelper.executeCommand("chassis:showInterfaces " + resourceFriendlyID + " -r", commandprocessor);

			// assert command output no contains ERROR tag

			ComputerSystem system = (ComputerSystem) resource.getModel();

			List<LogicalDevice> ld = system.getLogicalDevices();
			for (LogicalDevice l : ld) {
				if (l instanceof LogicalTunnelPort) {
					LogicalTunnelPort ltp = (LogicalTunnelPort) l;
					// show data of LT
					// name, peer-unit

					// Only check the modified interface
					if (ltp.getElementName().equalsIgnoreCase("lt-0/1/2")) {

						Assert.assertEquals(ltp.getPortNumber(), 12);
						if (ltp.getLinkTechnology().toString().equals("ETHERNET")) {
							Assert.assertNotNull(ltp.getPeer_unit());
						} else {
							Assert.assertNotNull(ltp.getPeer_unit());
							List<ProtocolEndpoint> pp = ltp.getProtocolEndpoint();
							for (ProtocolEndpoint p : pp) {
								if (p instanceof VLANEndpoint) {
									// show tha VLAN setted for this LT
									Assert.assertEquals(((VLANEndpoint) p).getVlanID(), 1);
								}

							}
						}

					}
				}
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

		String responseError = null;
		try {
			responseError = KarafCommandHelper.executeCommand("chassis:setVLAN " + resourceFriendlyID + " lo0.0 1",
					getOsgiService(CommandProcessor.class));

			// assert command output contains [ERROR]: not allowed VLAN configuration for loopback interface

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}
}
