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
import net.i2cat.nexus.resources.IResourceRepository;
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

		capabilities.add("ip");
		capabilities.add("queue");

		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("junosm20", "router", capabilities);
		resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		try {
			resource = repository.createResource(resourceDescriptor);
			IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 5000);
			protocolManager.getProtocolSessionManagerWithContext(resource.getResourceIdentifier().getId(), ProtocolSessionHelper
					.newSessionContextNetconf());
			repository.startResource(resource.getResourceDescriptor().getId());

		} catch (ResourceException e) {

			e.printStackTrace();
			Assert.fail(e.getMessage());
		} catch (ProtocolException e) {

			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	/**
	 * Configure a IP in a lt interface
	 * 
	 */

	@Test
	public void setIPlt() {

		try {
			String response = KarafCommandHelper.executeCommand("ipv4:setIP  " + resourceFriendlyID + " lt-0/1/2.5 192.168.1.1 255.255.255.0",
					commandprocessor);

			// assert command output no contains ERROR tag

			Object response1 = KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID, commandprocessor);
			// assert command output no contains ERROR tag

			Object response2 = KarafCommandHelper.executeCommand("ipv4:list " + resourceFriendlyID+" -r", commandprocessor);

			// assert command output no contains ERROR tag

			ComputerSystem system = (ComputerSystem) resource.getModel();
			List<LogicalDevice> ld = system.getLogicalDevices();
			for (LogicalDevice l : ld) {
				if (l instanceof LogicalTunnelPort) {
					LogicalTunnelPort lt = (LogicalTunnelPort) l;

					// show data of LT
					// name, linkTecnology
					// Only check the modified interface
					if (lt.getElementName().equalsIgnoreCase("lt-0/1/2")) {
						if (lt.getPortNumber() == 0) {
							Assert.assertEquals(lt.getLinkTechnology().toString(), "ETHERNET");
						} else {
							Assert.assertNotSame(lt.getLinkTechnology().toString(), "ETHERNET");
						}

						List<ProtocolEndpoint> pp = lt.getProtocolEndpoint();
						for (ProtocolEndpoint p : pp) {
							if (p instanceof IPProtocolEndpoint) {
								// show tha VLAN setted for this LT
								Assert.assertEquals(((IPProtocolEndpoint) p).getIPv4Address(), "192.168.1.1");
							}

						}
					}
				}
			}
			repository.stopResource(resource.getResourceIdentifier().getId());
			repository.removeResource(resource.getResourceIdentifier().getId());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}
	}

	/**
	 * Configure a IP in a l0 interface
	 * 
	 */

	@Test
	public void setIPl0() {
		try {
			String response = KarafCommandHelper.executeCommand("ipv4:setIP   " + resourceFriendlyID + " lo0.1 192.168.1.1 255.255.255.0",
					commandprocessor);

			// assert command output contains "[ERROR] Configuration for Loopback interface not allowed"
			// Assert.assertTrue(response.contains("[ERROR] Configuration for Loopback interface not allowed"));

			repository.stopResource(resource.getResourceIdentifier().getId());
			repository.removeResource(resource.getResourceIdentifier().getId());
		} catch (Exception e) {
			e.printStackTrace();
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
			log.debug("executeCommand(ipv4:setIP  " + resourceFriendlyID + " fe-0/1/2.0 192.168.1.1 255.255.255.0)");
			Object response = KarafCommandHelper.executeCommand(
					"ip:setIPv4  " + resourceFriendlyID + " fe-0/1/2.0 192.168.1.1 255.255.255.0", commandprocessor);
			// assert command output no contains ERROR tag

			log.debug("executeCommand(queue:execute " + resourceFriendlyID);
			response = KarafCommandHelper.executeCommand("queue:execute  " + resourceFriendlyID, commandprocessor);
			// assert command output no contains ERROR tag

			log.debug("executeCommand(ipv4:list " + resourceFriendlyID);
			response = KarafCommandHelper.executeCommand("ip:listInterfaces " + resourceFriendlyID +" -r", commandprocessor);
			// assert command output no contains ERROR tag

			ComputerSystem system = (ComputerSystem) resource.getModel();
			List<LogicalDevice> ld = system.getLogicalDevices();
			for (LogicalDevice l : ld) {
				if (l instanceof EthernetPort) {
					EthernetPort lt = (EthernetPort) l;

					// show data of LT
					// name, linkTecnology
					// Only check the modified interface
					if (lt.getElementName().equalsIgnoreCase("fe-0/1/2.0")) {
						if (lt.getPortNumber() == 0) {
							Assert.assertEquals(lt.getLinkTechnology().toString(), "ETHERNET");
						} else {
							Assert.assertNotSame(lt.getLinkTechnology().toString(), "ETHERNET");
						}

						List<ProtocolEndpoint> pp = lt.getProtocolEndpoint();
						for (ProtocolEndpoint p : pp) {
							if (p instanceof IPProtocolEndpoint) {
								// show tha VLAN setted for this LT
								Assert.assertEquals(((IPProtocolEndpoint) p).getIPv4Address(), "192.168.1.1");
							}

						}
					}
				}
			}
			repository.stopResource(resource.getResourceIdentifier().getId());
			repository.removeResource(resource.getResourceIdentifier().getId());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}

	}
}
