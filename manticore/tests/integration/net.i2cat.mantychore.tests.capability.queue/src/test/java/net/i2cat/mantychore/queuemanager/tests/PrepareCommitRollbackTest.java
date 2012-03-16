package net.i2cat.mantychore.queuemanager.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;

import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.util.Filter;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;

import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;

import static net.i2cat.nexus.tests.OpennaasExamOptions.*;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.*;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.swissbox.framework.ServiceLookup.getService;

@RunWith(JUnit4TestRunner.class)
public class PrepareCommitRollbackTest
{
	private final static Log		log				= LogFactory.getLog(PrepareCommitRollbackTest.class);

	private final String			resourceID		= "junosResource";

	private MockResource			mockResource;
	private ICapability				queueCapability;
	private IQueueManagerService	queueManagerService;

	@Inject
	private BundleContext			bundleContext;

	@Inject
	private IProtocolManager		protocolManager;

	@Inject
	@Filter("(capability=queue)")
	private ICapabilityFactory		queueManagerFactory;

    @Inject
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.repository)")
    private BlueprintContainer		routerService;

    @Inject
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.queuemanager)")
    private BlueprintContainer		queueService;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
					   includeFeatures("opennaas-router"),
					   includeSwissboxFramework(),
					   noConsole(),
					   keepRuntimeFolder());
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
		return protocolSessionContext;
	}

	@Before
	public void before() throws ProtocolException, CapabilityException {
		mockResource = new MockResource();
		mockResource.setModel(new ComputerSystem());
		List<String> capabilities = new ArrayList<String>();

		capabilities.add("queue");
		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("mockresource", "router", capabilities);

		mockResource.setResourceDescriptor(resourceDescriptor);

		protocolManager.getProtocolSessionManagerWithContext(mockResource.getResourceId(), newSessionContextNetconf());

		log.info("INFO: Before test, getting queue...");
		queueCapability = queueManagerFactory.create(mockResource);
		queueManagerService = getService(bundleContext, IQueueManagerService.class, 50000,
				"(capability=queue)(capability.name=" + mockResource.getResourceId() + ")");
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
	public void testPrepareRestoreAction() throws Exception {
		String uri = System.getProperty("protocol.uri");

		if (uri == null || uri.equals("${protocol.uri}")) {
			uri = "mock://user:pass@host.net:2212/mocksubsystem";
		}
		String compare = null;
		String toCompare = null;
		Query query = QueryFactory.newGetConfig("running", "<configuration></configuration>", null);
		Reply reply = sendNetconfMessage(query, uri);
		compare = reply.getContain();

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

		reply = sendNetconfMessage(query, uri);
		toCompare = reply.getContain();

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
