package net.i2cat.mantychore.simpleclient.tests;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.actionsets.junos.ChassisActionSetFactory;
import net.i2cat.mantychore.capability.chassis.ChassisCapability;
import net.i2cat.mantychore.capability.chassis.IChassisCapabilityFactory;
import net.i2cat.mantychore.commons.ActionResponse;
import net.i2cat.mantychore.commons.IActionSetFactory;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.ProtocolEndpoint;
import net.i2cat.mantychore.queuemanager.IQueueManagerFactory;
import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import net.i2cat.mantychore.queuemanager.QueueManager;
import net.i2cat.mantychore.queuemanager.QueueManagerConstants;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolSessionContext;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptorConstants;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

import org.apache.commons.lang.exception.ExceptionUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(JUnit4TestRunner.class)
public class SimpleClientTest extends AbstractIntegrationTest {
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

	static Logger			log				= LoggerFactory
													.getLogger(SimpleClientTest.class);
	IQueueManagerService	queueManager;

	String					deviceID		= "junos";
	String					queueID			= "queue";
	String					ethernetName	= "fe-0/3/2";
	String					ipConfigured	= "192.168.33.2";

	ProtocolSessionContext	protocolSessionContext;
	ChassisCapability		chassisCapability;

	IActionSetFactory		actionFactory	= new ChassisActionSetFactory();

	@Inject
	BundleContext			bundleContext	= null;

	static String			testVariable	= "test.variable";

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

	public static String getSystemProperty(String name) {
		Properties p = System.getProperties();
		return p.getProperty(name);
	}

	@Before
	public void initBundles() {
		log.info("Waiting to load all bundles");
		/* Wait for the activation of all the bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		log.info("Loaded all bundles");

	}

	@Test
	public void testActions() {
		log.info("This is running inside Equinox. With all configuration set up like you specified. ");

		/* initialize model */
		MockResource mockResource = new MockResource();

		mockResource.setModel(new ComputerSystem());

		ResourceDescriptor resourceDescriptor = new ResourceDescriptor();
		Map<String, String> properties = new HashMap<String, String>();
		properties.put(ResourceDescriptorConstants.PROTOCOL_URI,
				"user:pass@host.net:2212");
		resourceDescriptor.setProperties(properties);

		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(MockResource.createCapabilityDescriptor(
				QueueManager.QUEUE, "")); // TODO SECOND PARAMETER TO FILL
		capabilityDescriptors.add(MockResource.createCapabilityDescriptor(
				ChassisCapability.CHASSIS, "chassis"));

		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);
		resourceDescriptor.setId(deviceID);
		mockResource.setResourceDescriptor(resourceDescriptor);

		/* initialize queue manager */
		try {
			log.info("Starting the test");

			/* get queueManager as a service */
			log.info("Getting queue manager factory...");
			IQueueManagerFactory queueManagerFactory = getOsgiService(
					IQueueManagerFactory.class, 5000);
			log.info("Getting queue manager...");

			List<String> actionsQueue = actionFactory.getActionNames();
			actionsQueue.add(QueueManagerConstants.EXECUTE);
			actionsQueue.add(QueueManagerConstants.GETQUEUE);
			queueManager = queueManagerFactory.createQueueManager(actionsQueue,
					mockResource);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			log.error(ExceptionUtils.getRootCause(e).getMessage());
			Assert.fail();
		}

		/* initialize chassis capability */

		log.info("Starting the test");

		/* get queueManager as a service */
		log.info("Getting chassis capability factory...");

		IChassisCapabilityFactory chassisFactory = getOsgiService(
				IChassisCapabilityFactory.class, 5000);

		log.info("Getting chassis capability...");

		/* identify opers to can use */
		List<String> actionsChassis = actionFactory.getActionNames();
		actionsChassis.add(ActionConstants.GETCONFIG);
		actionsChassis.add(ActionConstants.SETINTERFACE);

		chassisCapability = chassisFactory.createChassisCapability(
				actionsChassis, mockResource);

		// FIXME CHASSIS HAVE TO COMMUNICATE WITH ITS QUEUE
		chassisCapability.setQueueManager(queueManager);

		try {
			chassisCapability.initialize();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			log.error(ExceptionUtils.getRootCause(e).getMessage());
			Assert.fail();
		}

		// chassisCapability.initialize();
		String actionId = ActionConstants.SETINTERFACE;

		Object params = newParamsInterface();
		chassisCapability.sendMessage(actionId, params);

		// check if it is added
		Assert.assertFalse(queueManager.getActions().size() != 1);

		try {
			List<ActionResponse> responses = queueManager.execute();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			log.error(ExceptionUtils.getRootCause(e).getMessage());
			Assert.fail();
		}
		// check if it is added
		Assert.assertFalse(queueManager.getActions().size() != 0);

		chassisCapability.sendMessage(ActionConstants.GETCONFIG, null);

		// chassisCapability.sendMessage(ChassisActionSetFactory.GETCONFIG,
		// null);
		// check if it is added
		Assert.assertFalse(queueManager.getActions().size() != 1);

		try {
			List<ActionResponse> responses = queueManager.execute();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			log.error(ExceptionUtils.getRootCause(e).getMessage());
			Assert.fail();
		}

		// check if it is added
		Assert.assertFalse(queueManager.getActions().size() != 0);
		// We only can test it if we are working with real routers
		// Assert.assertFalse(checkPort(model));

		ComputerSystem model = (ComputerSystem) mockResource.getModel();
		Assert.assertNotNull(model.getLogicalDevices());
		for (LogicalDevice logicalDevice : model.getLogicalDevices()) {
			EthernetPort ethernetPort = (EthernetPort) logicalDevice;
			log.info("ethernetPort name: " + ethernetPort.getElementName());
			if (ethernetPort.getElementName().equals(ethernetName)) {
				Assert.assertFalse(ethernetPort.getProtocolEndpoint() == null);
				Assert.assertFalse(ethernetPort.getProtocolEndpoint().size() == 0);
				for (ProtocolEndpoint endpoint : ethernetPort
						.getProtocolEndpoint()) {
					if (endpoint instanceof IPProtocolEndpoint) {
						log.info("IP: "
								+ ((IPProtocolEndpoint) endpoint)
										.getIPv4Address());
						// Assert.assertTrue("IP is not configured",
						// ((IPProtocolEndpoint) endpoint)
						// .getIPv4Address().equals(ipConfigured));
					}
				}

			}
			// ethernetPort.getProtocolEndpoint()
		}

		// Assert.assertFalse(model.getLogicalDevices().size() == 0);

	}

	private Object newParamsInterface() {
		EthernetPort eth = new EthernetPort();
		eth.setElementName(ethernetName);
		eth.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		eth.setPortNumber(30);
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address(ipConfigured);
		ip.setSubnetMask("255.255.255.0");
		eth.addProtocolEndpoint(ip);
		ArrayList params = new ArrayList();
		params.add(eth);
		return params;

	}

	/*
	 * private boolean checkPort(ComputerSystem model) { if (model.getLogicalDevices() == null || model.getLogicalDevices().size() == 0) return false;
	 * EthernetPort ethernetPort = (EthernetPort) model.getLogicalDevices(); boolean resultEquals = ethernetPort.getElementName().equals("ge-0/1/0");
	 * resultEquals = resultEquals && ethernetPort.getPortNumber() == 30; if (ethernetPort.getProtocolEndpoint() == null ||
	 * ethernetPort.getProtocolEndpoint().size() == 0) return false; IPProtocolEndpoint ip = (IPProtocolEndpoint) ethernetPort
	 * .getProtocolEndpoint().get(0); resultEquals = resultEquals && ip.getIPv4Address().equals("193.1.24.88"); resultEquals = resultEquals &&
	 * ip.getSubnetMask().equals("255.255.255.0");
	 * 
	 * return resultEquals;
	 * 
	 * }
	 */

}
