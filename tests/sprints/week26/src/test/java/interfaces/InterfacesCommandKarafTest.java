import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

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
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
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
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.osgi.service.command.CommandProcessor;
import org.osgi.service.command.CommandSession;

public class InterfacesCommandKarafTest extends AbstractIntegrationTest {
	static Log			log	= LogFactory
									.getLog(InterfacesCommandKarafTest.class);
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
		ResourceDescriptor resourceDescriptor = RepositoryHelper.newResourceDescriptor("router", "resource1");
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(RepositoryHelper.newIPCapabilityDescriptor());
		capabilityDescriptors.add(RepositoryHelper.newQueueCapabilityDescriptor());
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		try {
			resource = repository.createResource(resourceDescriptor);
			createProtocolForResource(resource.getResourceIdentifier().getId());
			repository.startResource(resource.getResourceDescriptor().getId());

		} catch (ResourceException e) {

			e.printStackTrace();
			Assert.fail(e.getMessage());
		} catch (ProtocolException e) {

			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	public void createProtocolForResource(String resourceId) throws ProtocolException {
		IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 5000);
		protocolManager.getProtocolSessionManagerWithContext(resourceId, newSessionContextNetconf());

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
	 * Configure the a VLNA in a ethernet interface
	 */
	public void setVLANforEth() {

		try {
			// chassis:setVLAN interface VLANid
			Object response = executeCommand("chassis:setVLAN " + resourceFriendlyID + " fe-0/1/2.12 1");
			Assert.assertNotNull(response);

			Object response1 = executeCommand("queue:execute " + resourceFriendlyID);
			Assert.assertNotNull(response1);

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

	public void setVLANforLT() {
		try {
			// chassis:setVLAN interface -pu PeerUnit
			Object response = executeCommand("chassis:setVLAN " + resourceFriendlyID + " lt-0/1/2.12 1");
			Assert.assertNotNull(response);

			Object response1 = executeCommand("queue:execute " + resourceFriendlyID);
			Assert.assertNotNull(response1);

			// Need to execute a refresh

			Object response2 = executeCommand("ip:listInterfaces " + resourceFriendlyID);
			Assert.assertNotNull(response2);

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
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}
}
