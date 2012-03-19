package net.i2cat.mantychore.chassiscapability.test;

import static net.i2cat.nexus.tests.OpennaasExamOptions.includeFeatures;
import static net.i2cat.nexus.tests.OpennaasExamOptions.noConsole;
import static net.i2cat.nexus.tests.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.chassiscapability.test.mock.MockBootstrapper;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.LogicalPort;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.VLANEndpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.ResourceIdentifier;
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
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class ChassisCapabilityIntegrationTest
{
	private final static Log	log			= LogFactory.getLog(ChassisCapabilityIntegrationTest.class);

	private final String		deviceID	= "junos";
	private final String		queueID		= "queue";

	private MockResource		mockResource;
	private ICapability			chassisCapability;
	private ICapability			queueCapability;

	@Inject
	private BundleContext		bundleContext;

	@Inject
	@Filter("(capability=queue)")
	private ICapabilityFactory	queueManagerFactory;

	@Inject
	private IProtocolManager	protocolManager;

	@Inject
	@Filter("(capability=chassis)")
	private ICapabilityFactory	chassisFactory;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.repository)")
	private BlueprintContainer	routerService;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-router"),
				noConsole(),
				keepRuntimeFolder());
	}

	public void initResource() {

		/* initialize model */
		mockResource = new MockResource();
		mockResource.setModel(new ComputerSystem());
		mockResource.setBootstrapper(new MockBootstrapper());

		List<String> capabilities = new ArrayList<String>();

		capabilities.add("chassis");
		capabilities.add("queue");
		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor(deviceID, "router", capabilities);
		mockResource.setResourceDescriptor(resourceDescriptor);
		mockResource.setResourceIdentifier(new ResourceIdentifier(resourceDescriptor.getInformation().getType(), resourceDescriptor.getId()));
	}

	/**
	 * Configure the protocol to connect
	 */
	private ProtocolSessionContext newSessionContextNetconf() {
		String uri = System.getProperty("protocol.uri");
		if (uri == null || uri.equals("${protocol.uri}") || uri.isEmpty()) {
			uri = "mock://user:pass@host.net:2212/mocksubsystem";
		}
		log.debug("FFFF test setup uri: " + uri + "Length: " + uri.length());
		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();

		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL_URI, uri);
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"netconf");
		// ADDED
		return protocolSessionContext;

	}

	public void initCapability() throws Exception {

		log.info("INFO: Before test, getting queue...");
		Assert.assertNotNull(queueManagerFactory);

		queueCapability = queueManagerFactory.create(mockResource);
		queueCapability.initialize();

		protocolManager.getProtocolSessionManagerWithContext(mockResource.getResourceId(), newSessionContextNetconf());

		// Test elements not null
		log.info("Checking chassis factory");
		Assert.assertNotNull(chassisFactory);
		log.info("Checking capability descriptor");
		Assert.assertNotNull(mockResource.getResourceDescriptor().getCapabilityDescriptor("chassis"));
		log.info("Creating chassis capability");
		chassisCapability = chassisFactory.create(mockResource);
		Assert.assertNotNull(chassisCapability);
		chassisCapability.initialize();

		mockResource.addCapability(chassisCapability);
		mockResource.addCapability(queueCapability);
	}

	@Before
	public void setup() throws Exception {
		initResource();
		initCapability();
	}

	@Test
	@Ignore
	// FIXME this tests fails because of a vlan-tagging limitation describes at OPENNAAS-95 issue.
	public void TestChassisAction() throws CapabilityException {
		log.info("TEST CHASSIS ACTION");

		int actionCount = 0;

		Response resp = (Response) chassisCapability.sendMessage(ActionConstants.GETCONFIG, null);
		Assert.assertTrue(resp.getStatus() == Status.OK);
		Assert.assertTrue(resp.getErrors().size() == 0);
		actionCount++;

		resp = (Response) chassisCapability.sendMessage(ActionConstants.CONFIGURESUBINTERFACE, newParamsInterfaceEthernetPort("fe-0/1/0", 13));
		Assert.assertTrue(resp.getStatus() == Status.OK);
		Assert.assertTrue(resp.getErrors().size() == 0);
		actionCount++;

		resp = (Response) chassisCapability.sendMessage(ActionConstants.DELETESUBINTERFACE, newParamsInterfaceEthernetPort("fe-0/1/0", 13));
		Assert.assertTrue(resp.getStatus() == Status.OK);
		Assert.assertTrue(resp.getErrors().size() == 0);
		actionCount++;

		resp = (Response) chassisCapability.sendMessage(ActionConstants.SETENCAPSULATION, newParamsInterfaceEthernetPort("fe-0/1/0", 13));
		Assert.assertTrue(resp.getStatus() == Status.OK);
		Assert.assertTrue(resp.getErrors().size() == 0);
		actionCount++;

		// setInterfaceDescriptionAction was moved to ipv4 capab
		// resp = (Response) chassisCapability.sendMessage(ActionConstants.SETINTERFACEDESCRIPTION, newParamsInterfaceEthernetPort("fe-0/1/0",
		// 13));
		// Assert.assertTrue(resp.getStatus() == Status.OK);
		// Assert.assertTrue(resp.getErrors().size() == 0);

		// resp = (Response) chassisCapability.sendMessage(ActionConstants.SETINTERFACEDESCRIPTION, newParamsInterfaceLogicalPort("fe-0/1/0"));
		// Assert.assertTrue(resp.getStatus() == Status.OK);
		// Assert.assertTrue(resp.getErrors().size() == 0);

		List<IAction> queue = (List<IAction>) queueCapability.sendMessage(QueueConstants.GETQUEUE, null);
		Assert.assertTrue(queue.size() == 4);

		QueueResponse queueResponse = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);
		Assert.assertTrue(queueResponse.getResponses().size() == actionCount);

		for (int i = 0; i < queueResponse.getResponses().size(); i++) {
			Assert.assertTrue(queueResponse.getResponses().get(i).getStatus() == ActionResponse.STATUS.OK);
			for (Response response : queueResponse.getResponses().get(i).getResponses()) {
				Assert.assertTrue(response.getStatus() == Response.Status.OK);
			}
		}

		Assert.assertTrue(queueResponse.getPrepareResponse().getStatus() == ActionResponse.STATUS.OK);
		Assert.assertTrue(queueResponse.getConfirmResponse().getStatus() == ActionResponse.STATUS.OK);
		Assert.assertTrue(queueResponse.getRefreshResponse().getStatus() == ActionResponse.STATUS.OK);
		Assert.assertTrue(queueResponse.getRestoreResponse().getStatus() == ActionResponse.STATUS.PENDING);

		Assert.assertTrue(queueResponse.isOk());

		queue = (List<IAction>) queueCapability.sendMessage(QueueConstants.GETQUEUE, null);
		Assert.assertTrue(queue.size() == 0);
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
		// ugly crap which should work
		eth.setDescription("capability test");
		return eth;
	}

	public Object newParamsInterfaceLogicalPort(String name) {
		LogicalPort eth = new LogicalPort();
		// eth.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		eth.setName(name);
		// eth.setPortNumber(port);
		// ugly crap which should work
		eth.setDescription("capability test description phy");
		return eth;
	}

	public Object newParamsInterfaceEthernetPortVLAN(String name, int port, int vlanID) {
		EthernetPort eth = new EthernetPort();
		eth.setLinkTechnology(NetworkPort.LinkTechnology.OTHER);
		eth.setName(name);
		eth.setPortNumber(port);
		// vlan specific
		VLANEndpoint vlan = new VLANEndpoint();
		vlan.setVlanID(vlanID);
		eth.addProtocolEndpoint(vlan);
		// ugly crap which should work
		eth.setDescription("capability test vlan");
		return eth;
	}
}
