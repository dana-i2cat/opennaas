package net.i2cat.mantychore.queuemanager.tests;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import net.i2cat.netconf.NetconfSession;
import net.i2cat.netconf.SessionContext;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;
import net.i2cat.netconf.rpc.Reply;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.MockAction;
import org.opennaas.core.resources.helpers.MockResource;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
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

		/* Wait for the activation of all the bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);

		/* initialize model */
		mockResource = new MockResource();
		mockResource.setModel(new ComputerSystem());
		List<String> capabilities = new ArrayList<String>();

		capabilities.add("queue");
		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("mockresource", "router", capabilities);

		mockResource.setResourceDescriptor(resourceDescriptor);

		IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 50000);
		protocolManager.getProtocolSessionManagerWithContext(mockResource.getResourceId(), newSessionContextNetconf());

		log.info("INFO: Initialized!");

	}

	public void before() throws ProtocolException, CapabilityException {
		initBundles();
		log.info("INFO: Before test, getting queue...");
		ICapabilityFactory queueManagerFactory = getOsgiService(ICapabilityFactory.class, "capability=queue", 50000);
		queueCapability = queueManagerFactory.create(mockResource);
		queueManagerService = getOsgiService(IQueueManagerService.class,
				"(capability=queue)(capability.name=" + mockResource.getResourceId() + ")", 50000);

	}

	/*
	 * test of an interface ethernet without vlan encapsulation
	 */
	private Object newParamsInterfaceEthernet() {
		EthernetPort eth = new EthernetPort();
		eth.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		eth.setName("fe-0/1/2");
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
