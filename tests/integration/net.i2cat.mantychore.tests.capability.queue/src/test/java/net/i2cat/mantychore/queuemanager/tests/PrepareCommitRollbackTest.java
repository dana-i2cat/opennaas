package net.i2cat.mantychore.queuemanager.tests;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import net.i2cat.mantychore.queuemanager.QueueManager;
import net.i2cat.netconf.NetconfSession;
import net.i2cat.netconf.SessionContext;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;
import net.i2cat.netconf.rpc.Reply;
import net.i2cat.nexus.resources.action.ActionException;
import net.i2cat.nexus.resources.action.IAction;
import net.i2cat.nexus.resources.action.IActionSet;
import net.i2cat.nexus.resources.capability.CapabilityException;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.capability.ICapabilityFactory;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptorConstants;
import net.i2cat.nexus.resources.protocol.IProtocolManager;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;

@RunWith(JUnit4TestRunner.class)
public class PrepareCommitRollbackTest extends AbstractIntegrationTest {
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

	static Log				log				= LogFactory
													.getLog(PrepareCommitRollbackTest.class);

	MockResource			mockResource;
	String					resourceID		= "junosResource";
	// QueueManager queueManagerService;
	ICapability				queueCapability;
	IQueueManagerService	queueManagerService;

	@Inject
	BundleContext			bundleContext	= null;

	@Configuration
	public static Option[] configure() {

		Option[] options = combine(
				IntegrationTestsHelper.getMantychoreTestOptions(),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.tests.helper")
				// , vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
				);
		return options;
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

	public void initBundles() throws ProtocolException {
		log.info("Waiting to load all bundles");
		/* Wait for the activation of all the bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		log.info("Loaded all bundles");

		/* init capability */

		log.info("This is running inside Equinox. With all configuration set up like you specified. ");

		/* initialize model */
		mockResource = new MockResource();
		mockResource.setModel(new ComputerSystem());

		ResourceDescriptor resourceDescriptor = new ResourceDescriptor();

		/* add queue capability */
		Map<String, String> properties = new HashMap<String, String>();
		properties.put(ResourceDescriptorConstants.PROTOCOL_URI,
				"user:pass@host.net:2212");
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(MockResource.createCapabilityDescriptor(
				QueueManager.QUEUE, "queue"));

		resourceDescriptor.setProperties(properties);
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);
		resourceDescriptor.setId(resourceID);

		mockResource.setResourceDescriptor(resourceDescriptor);

		IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 50000);
		protocolManager.getProtocolSessionManagerWithContext(resourceID, newSessionContextNetconf());

		log.info("INFO: Initialized!");

	}

	// FIXME Before and After does not work with linux

	public void before() throws ProtocolException, CapabilityException {
		initBundles();
		log.info("INFO: Before test, getting queue...");
		ICapabilityFactory queueManagerFactory = getOsgiService(ICapabilityFactory.class, "capability=queue", 50000);
		queueCapability = queueManagerFactory.create(mockResource);
		queueManagerService = (IQueueManagerService) getOsgiService(IQueueManagerService.class,
				"(capability=queue)(capability.name=" + resourceID + ")", 50000);

	}

	// @After
	// public void after() {
	// log.info("INFO: After test, cleaning queue...");
	// queueManagerService.empty();
	//
	// }

	@Test
	public void testCheckConfigurationAction() {
		try {
			before();
		} catch (Exception e1) {
			Assert.fail(e1.getMessage());
		}
		// Try to do a configuration with the router is corrupted
		 String uri = System.getProperty("protocol.uri");
		

		if (uri == null || uri.equals("${protocol.uri}")) {
			uri = "mock://user:pass@host.net:2212/mocksubsystem";
		}
		String backup = null;
		/* 1.- Backup */
		Query query;
		try {
			query = QueryFactory.newGetConfig("running", "<configuration></configuration>", null);
			Reply reply = sendNetconfMessage(query, uri);
			backup = reply.getContain();

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}

		/* 2.- Prepare configuration. Corrupt config */
		try {
			query = QueryFactory.newEditConfig("candidate", null, null, null, readStringFromFile("/corruptconfig.xml"));
			sendNetconfMessage(query, uri);
			// query = QueryFactory.newGetConfig(source, filter, attrFilter)
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}

		/* 3.- Test configuration. Corrupt config */
		IActionSet actionSet = getOsgiService(IActionSet.class, "actionset.capability=chassis", 5000);
		IAction action = null;
		try {
			action = actionSet.obtainAction(ActionConstants.SETINTERFACE);
			action.setParams(newParamsInterfaceEthernet());
		} catch (ActionException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}

		queueManagerService.queueAction(action);
		try {
			queueManagerService.execute();
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

	}

	/*
	 * test of an interface ethernet without vlan encapsulation
	 */
	private Object newParamsInterfaceEthernet() {
		EthernetPort eth = new EthernetPort();
		eth.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		eth.setElementName("fe-0/1/2");
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address("192.168.32.1");
		ip.setSubnetMask("255.255.255.0");
		eth.addProtocolEndpoint(ip);
		System.out.println(eth.getLinkTechnology().toString());

		return eth;
	}

	@Test
	public void testPrepareRestoreAction() {
		try {
			before();
		} catch (Exception e1) {
			Assert.fail(e1.getMessage());
		}

		 String uri = System.getProperty("protocol.uri");
		

		if (uri == null || uri.equals("${protocol.uri}")) {
			uri = "mock://user:pass@host.net:2212/mocksubsystem";
		}
		String compare = null;
		String toCompare = null;
		Query query = QueryFactory.newGetConfig("running", "<configuration></configuration>", null);
		try {
			Reply reply = sendNetconfMessage(query, uri);
			compare = reply.getContain();

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}

		IAction action = new MockAction();
		action.setActionID("mockAction");
		queueManagerService.queueAction(action);
		action = new CorruptedAction();
		action.setActionID("corruptedAction");
		queueManagerService.queueAction(action);

		boolean isChecked = false;
		try {

			queueManagerService.execute();
		} catch (Exception e) {
			if (e instanceof CapabilityException)
				isChecked = true;
		}
		if (!uri.startsWith("mock:"))
			Assert.assertTrue(isChecked);

		try {
			Reply reply = sendNetconfMessage(query, uri);
			toCompare = reply.getContain();

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}

		Assert.assertEquals(compare, toCompare);

	}

	public Reply sendNetconfMessage(Query query, String uri) throws Exception {
		SessionContext sessionContext = new SessionContext();
		sessionContext.setURI(new URI(uri));
		NetconfSession session = new NetconfSession(sessionContext);
		session.connect();
		Reply reply = session.sendSyncQuery(query);
		if (reply.containsErrors())
			throw new Exception();
		session.disconnect();
		return reply;

	}

	public String readStringFromFile(String pathFile) throws Exception {
		String answer = null;
		InputStream inputFile = getClass().getResourceAsStream(pathFile);
		InputStreamReader streamReader = new InputStreamReader(inputFile);
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(streamReader);
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		answer = fileData.toString();

		return answer;
	}

}
