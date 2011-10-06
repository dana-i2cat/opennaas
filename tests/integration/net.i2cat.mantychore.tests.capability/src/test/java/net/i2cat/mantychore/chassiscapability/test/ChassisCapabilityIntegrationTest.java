package net.i2cat.mantychore.chassiscapability.test;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.VLANEndpoint;

import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.command.Response.Status;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.MockResource;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.core.resources.queue.QueueConstants;
import org.opennaas.core.resources.queue.QueueResponse;
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

		/* initialize model */

		mockResource = new MockResource();
		mockResource.setModel((IModel) new ComputerSystem());
		List<String> capabilities = new ArrayList<String>();

		capabilities.add("chassis");
		capabilities.add("queue");
		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor(deviceID, "router", capabilities);

		mockResource.setResourceDescriptor(resourceDescriptor);

	}

	/**
	 * Configure the protocol to connect
	 */
	private ProtocolSessionContext newSessionContextNetconf() {
		String uri = System.getProperty("protocol.uri");
		if (uri == null || uri.equals("${protocol.uri}") || uri.isEmpty()) {
			uri = "mock://user:pass@host.net:2212/mocksubsystem";
		}
		log.warn("FFFF test setup uri: "+uri+"Length: "+uri.length());
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
			log.error(e.getMessage(), e);
			// log.error(ExceptionUtils.getRootCause(e).getMessage());
			Assert.fail();
		}
	}

	@Before
	public void initBundles() {
		
		/* Wait for the activation of all the bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		
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
			//debug crap
			log.warn("FFFF: About to implode.");
			//next line implodes.
			QueueResponse queueResponse = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);
			log.warn("FFFF: Survived.");
			Assert.assertTrue(queueResponse.getResponses().size() == 4);

			Assert.assertTrue(queueResponse.getResponses().get(0).getStatus() == ActionResponse.STATUS.OK);
			for (Response response : queueResponse.getResponses().get(0).getResponses()) {
				Assert.assertTrue(response.getStatus() == Response.Status.OK);
			}

			Assert.assertTrue(queueResponse.getResponses().get(1).getStatus() == ActionResponse.STATUS.OK);
			for (Response response : queueResponse.getResponses().get(1).getResponses()) {
				Assert.assertTrue(response.getStatus() == Response.Status.OK);
			}

			Assert.assertTrue(queueResponse.getResponses().get(2).getStatus() == ActionResponse.STATUS.OK);
			for (Response response : queueResponse.getResponses().get(2).getResponses()) {
				Assert.assertTrue(response.getStatus() == Response.Status.OK);
			}

			Assert.assertTrue(queueResponse.getResponses().get(3).getStatus() == ActionResponse.STATUS.OK);
			for (Response response : queueResponse.getResponses().get(3).getResponses()) {
				Assert.assertTrue(response.getStatus() == Response.Status.OK);
			}

			Assert.assertTrue(queueResponse.getPrepareResponse().getStatus() == ActionResponse.STATUS.OK);

			Assert.assertTrue(queueResponse.getConfirmResponse().getStatus() == ActionResponse.STATUS.OK);
			Assert.assertTrue(queueResponse.getRestoreResponse().getStatus() == ActionResponse.STATUS.PENDING);

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
		eth.setName(name);
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address(ipName);
		ip.setSubnetMask(mask);
		eth.addProtocolEndpoint(ip);
		return eth;
	}

	public Object newParamsInterfaceEthernetPort(String name, int port) {
		EthernetPort eth = new EthernetPort();
		eth.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		eth.setName(name);
		eth.setPortNumber(port);

		return eth;
	}

	public Object newParamsInterfaceEthernetPortVLAN(String name, int port, int vlanID) {
		EthernetPort eth = new EthernetPort();
		eth.setLinkTechnology(NetworkPort.LinkTechnology.OTHER);
		eth.setName(name);
		eth.setPortNumber(port);

		VLANEndpoint vlan = new VLANEndpoint();
		vlan.setVlanID(vlanID);
		eth.addProtocolEndpoint(vlan);
		return eth;
	}
}
