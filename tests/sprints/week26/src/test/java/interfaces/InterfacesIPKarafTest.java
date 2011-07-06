package interfaces;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.model.ProtocolEndpoint;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceRepository;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;
import net.i2cat.nexus.resources.protocol.IProtocolManager;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.osgi.service.command.CommandProcessor;
import org.osgi.service.command.CommandSession;

public class InterfacesIPKarafTest extends AbstractIntegrationTest {
	static Log			log	= LogFactory
									.getLog(InterfacesUpDownKarafTest.class);
	IResourceRepository	repository;
	String				resourceFriendlyID;
	IResource			resource;

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

	public Object executeCommand(String command) throws Exception {
		// Run some commands to make sure they are installed properly
		CommandProcessor cp = getOsgiService(CommandProcessor.class);
		CommandSession cs = cp.createSession(System.in, System.out, System.err);
		Object commandOutput = null;
		try {
			commandOutput = cs.execute(command);
			return commandOutput;
		} catch (IllegalArgumentException e) {
			Assert.fail("Action should have thrown an exception because: " + e.toString());
		} catch (NoSuchMethodException a) {
			log.error("Method for command not found: " + a.getLocalizedMessage());
			Assert.fail("Method for command not found.");
		}

		cs.close();
		return commandOutput;
	}

	@Before
	public void initTest() {
		List<String> capabilities = new ArrayList<String>();

		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("junosm20", "junos", capabilities);
		resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		try {
			resource = repository.createResource(resourceDescriptor);
			IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 5000);
			protocolManager.getProtocolSessionManagerWithContext(resource.getResourceIdentifier().getId(), newSessionContextNetconf());
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
	 * Configure the protocol to connect
	 */
	private ProtocolSessionContext newSessionContextNetconf() {
		String uri = System.getProperty("protocol.uri");
		if (uri == null || uri.equals("${protocol.uri}")) {
			uri = "mock://user:pass@host.net:2212/mocksubsystem";
		}

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();

		protocolSessionContext.addParameter(
				ProtocolSessionContext.PROTOCOL_URI, uri);
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"netconf");
		// ADDED
		return protocolSessionContext;
	}

	/**
	 * Configure a IP in a lt interface
	 * 
	 */

	@Test
	public void setIPlt() {

		try {
			Object response = executeCommand("ip:setIPv4  " + resourceFriendlyID + " lt-0/1/2.0 192.168.1.1 255.255.255.0");
			Assert.assertNotNull(response);

			Object response1 = executeCommand("queue:execute " + resourceFriendlyID);
			Assert.assertNotNull(response1);

			Object response2 = executeCommand("ip:listInterfaces " + resourceFriendlyID);
			Assert.assertNotNull(response2);

			ComputerSystem system = (ComputerSystem) resource.getModel();
			List<LogicalDevice> ld = system.getLogicalDevices();
			for (LogicalDevice l : ld) {
				if (l instanceof EthernetPort) {
					EthernetPort ethport = (EthernetPort) l;

					// show data of ETH
					// name, linkTecnology
					// Only check the modified interface
					if (ethport.getElementName().equalsIgnoreCase("fe-0/1/2")) {
						if (ethport.getPortNumber() == 0) {
							Assert.assertEquals(ethport.getLinkTechnology().toString(), "ETHERNET");
						} else {
							Assert.assertNotSame(ethport.getLinkTechnology().toString(), "ETHERNET");
						}

						List<ProtocolEndpoint> pp = ethport.getProtocolEndpoint();
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
			Object response = executeCommand("ip:setIPv4  " + resourceFriendlyID + " l0.1 192.168.1.1 255.255.255.0");
			Assert.assertNotNull(response);

			Assert.assertEquals(response.toString(), "[ERROR]No possible to configure l0 interface");

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}
	}
}
