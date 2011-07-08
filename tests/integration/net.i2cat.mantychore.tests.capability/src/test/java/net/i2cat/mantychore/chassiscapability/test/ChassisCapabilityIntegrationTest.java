package net.i2cat.mantychore.chassiscapability.test;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.capability.chassis.ChassisCapability;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.VLANEndpoint;
import net.i2cat.mantychore.queuemanager.QueueManager;
import net.i2cat.nexus.resources.action.ActionResponse;
import net.i2cat.nexus.resources.action.IAction;
import net.i2cat.nexus.resources.capability.CapabilityException;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.capability.ICapabilityFactory;
import net.i2cat.nexus.resources.command.Response;
import net.i2cat.nexus.resources.command.Response.Status;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptorConstants;
import net.i2cat.nexus.resources.protocol.IProtocolManager;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;
import net.i2cat.nexus.resources.queue.QueueConstants;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

import org.apache.commons.lang.exception.ExceptionUtils;
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

@RunWith(JUnit4TestRunner.class)
public class ChassisCapabilityIntegrationTest extends AbstractIntegrationTest {
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;
	static Log			log				= LogFactory
															.getLog(ChassisCapabilityIntegrationTest.class);
	static MockResource	mockResource;
	String				deviceID		= "junos";
	String				queueID			= "queue";
	static ICapability	chassisCapability;

	@Inject
	BundleContext		bundleContext	= null;
	private ICapability	queueCapability;

	@Configuration
	public static Option[] configure() {

		Option[] options = combine(
				IntegrationTestsHelper.getMantychoreTestOptions(),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.tests.helper")
					// , vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
					);
		// TODO IS IT EXIT A BETTER METHOD TO PASS THE URI
		String uri = System.getProperty("protocol.uri");
		if (uri != null && !uri.equals("${protocol.uri}")) {
			Option[] optionsWithURI = options(systemProperty("protocol.uri").value(uri));
			options = combine(options, optionsWithURI);
		}
		return options;
	}

	public void initResource() {
		log.info("This is running inside Equinox. With all configuration set up like you specified. ");

		/* initialize model */
		mockResource = new MockResource();
		mockResource.setModel(new ComputerSystem());

		ResourceDescriptor resourceDescriptor = new ResourceDescriptor();

		Map<String, String> properties = new HashMap<String, String>();
		properties.put(ResourceDescriptorConstants.PROTOCOL_URI,
				"user:pass@host.net:2212");
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		/* chassis descriptor */
		capabilityDescriptors.add(MockResource.createCapabilityDescriptor(
				ChassisCapability.CHASSIS, "chassis"));

		/* queue descriptor */
		capabilityDescriptors.add(MockResource.createCapabilityDescriptor(
				QueueManager.QUEUE, "queue"));

		resourceDescriptor.setProperties(properties);
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);
		resourceDescriptor.setId(deviceID);

		mockResource.setResourceDescriptor(resourceDescriptor);
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

	public void initCapability() {

		try {
			log.info("INFO: Before test, getting queue...");
			ICapabilityFactory queueManagerFactory = getOsgiService(ICapabilityFactory.class, "capability=queue", 5000);
			Assert.assertNotNull(queueManagerFactory);

			queueCapability = queueManagerFactory.create(mockResource);

			// IQueueManagerService queueManagerService = (IQueueManagerService) getOsgiService(IQueueManagerService.class,
			// "(capability=queue)(capability.name=" + deviceID + ")", 5000);

			IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 5000);
			protocolManager.getProtocolSessionManagerWithContext(mockResource.getResourceId(), newSessionContextNetconf());

			ICapabilityFactory chassisFactory = getOsgiService(ICapabilityFactory.class, "capability=chassis", 10000);
			// Test elements not null
			log.info("Checking chassis factory");
			Assert.assertNotNull(chassisFactory);
			log.info("Checking capability descriptor");
			Assert.assertNotNull(mockResource.getResourceDescriptor().getCapabilityDescriptor("chassis"));
			log.info("Creating chassis capability");
			chassisCapability = chassisFactory.create(mockResource);
			Assert.assertNotNull(chassisCapability);
			chassisCapability.initialize();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			log.error(ExceptionUtils.getRootCause(e).getMessage());
			Assert.fail();
		}
	}

	@Before
	public void initBundles() {
		log.info("Waiting to load all bundles");
		/* Wait for the activation of all the bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		log.info("Loaded all bundles");
		initResource();
		initCapability();
	}

	@Test
	public void TestChassisAction() {
		log.info("TEST CHASSIS ACTION");

		try {
			Response resp = (Response) chassisCapability.sendMessage(ActionConstants.GETCONFIG, null);
			Assert.assertTrue(resp.getStatus() == Status.OK);
			Assert.assertTrue(resp.getErrors().size() == 0);

			resp = (Response) chassisCapability.sendMessage(ActionConstants.CREATESUBINTERFACE, newParamsInterfaceEthernetPort("fe-0/1/0", 13));
			Assert.assertTrue(resp.getStatus() == Status.OK);
			Assert.assertTrue(resp.getErrors().size() == 0);

			resp = (Response) chassisCapability.sendMessage(ActionConstants.DELETESUBINTERFACE, newParamsInterfaceEthernetPort("fe-0/1/0", 13));
			Assert.assertTrue(resp.getStatus() == Status.OK);
			Assert.assertTrue(resp.getErrors().size() == 0);

			resp = (Response) chassisCapability.sendMessage(ActionConstants.SETVLAN, newParamsInterfaceEthernetPort("fe-0/1/0", 13));
			Assert.assertTrue(resp.getStatus() == Status.OK);
			Assert.assertTrue(resp.getErrors().size() == 0);

			List<IAction> queue = (List<IAction>) queueCapability.sendMessage(QueueConstants.GETQUEUE, null);
			Assert.assertTrue(queue.size() == 4);
			List<ActionResponse> responses = (List<ActionResponse>) queueCapability.sendMessage(QueueConstants.EXECUTE, null);

			Assert.assertTrue(responses.size() == 5);
			ActionResponse actionResponse = responses.get(0);
			Assert.assertEquals(ActionConstants.GETCONFIG, actionResponse.getActionID());
			for (Response response : actionResponse.getResponses()) {
				Assert.assertTrue(response.getStatus() == Response.Status.OK);
			}

			ActionResponse actionResponse1 = responses.get(1);
			Assert.assertEquals(ActionConstants.CREATESUBINTERFACE, actionResponse1.getActionID());
			for (Response response : actionResponse1.getResponses()) {
				Assert.assertTrue(response.getStatus() == Response.Status.OK);
			}

			ActionResponse actionResponse2 = responses.get(2);
			Assert.assertEquals(ActionConstants.DELETESUBINTERFACE, actionResponse2.getActionID());
			for (Response response : actionResponse2.getResponses()) {
				Assert.assertTrue(response.getStatus() == Response.Status.OK);
			}
			ActionResponse actionResponse3 = responses.get(2);
			Assert.assertEquals(ActionConstants.SETVLAN, actionResponse3.getActionID());
			for (Response response : actionResponse3.getResponses()) {
				Assert.assertTrue(response.getStatus() == Response.Status.OK);
			}

			queue = (List<IAction>) queueCapability.sendMessage(QueueConstants.GETQUEUE, null);
			Assert.assertTrue(queue.size() == 0);

		} catch (CapabilityException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	public Object newParamsInterfaceEthernet(String name, String ipName, String mask) {
		EthernetPort eth = new EthernetPort();
		eth.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		eth.setElementName(name);
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address(ipName);
		ip.setSubnetMask(mask);
		eth.addProtocolEndpoint(ip);
		return eth;
	}

	public Object newParamsInterfaceEthernetPort(String name, int port) {
		EthernetPort eth = new EthernetPort();
		eth.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		eth.setElementName(name);
		eth.setPortNumber(port);

		return eth;
	}

	public Object newParamsInterfaceEthernetPortVLAN(String name, int port, int vlanID) {
		EthernetPort eth = new EthernetPort();
		eth.setLinkTechnology(NetworkPort.LinkTechnology.OTHER);
		eth.setElementName(name);
		eth.setPortNumber(port);

		VLANEndpoint vlan = new VLANEndpoint();
		vlan.setVlanID(vlanID);
		eth.addProtocolEndpoint(vlan);
		return eth;
	}
}
