package net.i2cat.mantychore.chassiscapability.test;

import static net.i2cat.nexus.tests.OpennaasExamOptions.includeFeatures;
import static net.i2cat.nexus.tests.OpennaasExamOptions.noConsole;
import static net.i2cat.nexus.tests.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
import net.i2cat.mantychore.model.LogicalDevice;
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

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.actionsets.junos)")
	private BlueprintContainer	junosActionsetService;

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
	public void testSetEncapsulationAction() throws CapabilityException {

		int actionCount = 0;

		Response resp = (Response) chassisCapability.sendMessage(ActionConstants.GETCONFIG, null);
		assertEquals(Status.OK, resp.getStatus());
		assertTrue("There should be no errors", resp.getErrors().isEmpty());
		actionCount++;

		resp = (Response) chassisCapability.sendMessage(ActionConstants.SETENCAPSULATION, newParamsInterfaceEthernetPort("fe-0/1/0", 13));
		assertEquals(Status.OK, resp.getStatus());
		assertTrue("There should be no errors", resp.getErrors().isEmpty());
		actionCount++;

		List<IAction> queue = (List<IAction>) queueCapability.sendMessage(QueueConstants.GETQUEUE, null);
		assertEquals(actionCount, queue.size());

		QueueResponse queueResponse = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);
		assertEquals(actionCount, queueResponse.getResponses().size());

		for (int i = 0; i < queueResponse.getResponses().size(); i++) {
			assertEquals(ActionResponse.STATUS.OK, queueResponse.getResponses().get(i).getStatus());
			for (Response response : queueResponse.getResponses().get(i).getResponses()) {
				assertEquals(Response.Status.OK, response.getStatus());
			}
		}

		assertEquals(ActionResponse.STATUS.OK, queueResponse.getPrepareResponse().getStatus());
		assertEquals(ActionResponse.STATUS.OK, queueResponse.getConfirmResponse().getStatus());
		assertEquals(ActionResponse.STATUS.OK, queueResponse.getRefreshResponse().getStatus());
		assertEquals(ActionResponse.STATUS.PENDING, queueResponse.getRestoreResponse().getStatus());

		assertTrue("Response should be ok", queueResponse.isOk());
	}

	@Test
	public void TestChassisAction() throws CapabilityException {
		log.info("TEST CHASSIS ACTIONS");

		int actionCount = 0;

		Response resp = (Response) chassisCapability.sendMessage(ActionConstants.GETCONFIG, null);
		assertEquals(Status.OK, resp.getStatus());
		assertTrue("There should be no errors", resp.getErrors().isEmpty());
		actionCount++;

		resp = (Response) chassisCapability.sendMessage(ActionConstants.CONFIGURESUBINTERFACE, newParamsInterfaceEthernetPort("fe-0/1/0", 13));
		assertEquals(Status.OK, resp.getStatus());
		assertTrue("There should be no errors", resp.getErrors().isEmpty());
		actionCount++;

		resp = (Response) chassisCapability.sendMessage(ActionConstants.DELETESUBINTERFACE, newParamsInterfaceEthernetPort("fe-0/1/0", 13));
		assertEquals(Status.OK, resp.getStatus());
		assertTrue("There should be no errors", resp.getErrors().isEmpty());
		actionCount++;

		// // FIXME disabled as it fails (it is tested in testSetEncapsulationAction, now ignored)
		// resp = (Response) chassisCapability.sendMessage(ActionConstants.SETENCAPSULATION, newParamsInterfaceEthernetPort("fe-0/1/0", 13));
		// Assert.assertTrue(resp.getStatus() == Status.OK);
		// Assert.assertTrue(resp.getErrors().size() == 0);
		// actionCount++;

		// setInterfaceDescriptionAction was moved to ipv4 capab
		// resp = (Response) chassisCapability.sendMessage(ActionConstants.SETINTERFACEDESCRIPTION, newParamsInterfaceEthernetPort("fe-0/1/0",
		// 13));
		// Assert.assertTrue(resp.getStatus() == Status.OK);
		// Assert.assertTrue(resp.getErrors().size() == 0);

		// resp = (Response) chassisCapability.sendMessage(ActionConstants.SETINTERFACEDESCRIPTION, newParamsInterfaceLogicalPort("fe-0/1/0"));
		// Assert.assertTrue(resp.getStatus() == Status.OK);
		// Assert.assertTrue(resp.getErrors().size() == 0);

		resp = (Response) chassisCapability
				.sendMessage(ActionConstants.ADDINTERFACETOLOGICALROUTER, newParamsLRWithInterface("cpe1", "fe-0/1/0", 13));
		assertEquals(Status.OK, resp.getStatus());
		assertTrue("There should be no errors", resp.getErrors().isEmpty());
		actionCount++;

		resp = (Response) chassisCapability.sendMessage(ActionConstants.REMOVEINTERFACEFROMLOGICALROUTER,
				newParamsLRWithInterface("cpe2", "fe-0/0/3", 13));
		assertEquals(Status.OK, resp.getStatus());
		assertTrue("There should be no errors", resp.getErrors().isEmpty());
		actionCount++;

		List<IAction> queue = (List<IAction>) queueCapability.sendMessage(QueueConstants.GETQUEUE, null);
		assertEquals(actionCount, queue.size());

		QueueResponse queueResponse = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);
		assertEquals(actionCount, queueResponse.getResponses().size());

		for (int i = 0; i < queueResponse.getResponses().size(); i++) {
			assertEquals(ActionResponse.STATUS.OK, queueResponse.getResponses().get(i).getStatus());
			for (Response response : queueResponse.getResponses().get(i).getResponses()) {
				assertEquals(Response.Status.OK, response.getStatus());
			}
		}

		assertEquals(ActionResponse.STATUS.OK, queueResponse.getPrepareResponse().getStatus());
		assertEquals(ActionResponse.STATUS.OK, queueResponse.getConfirmResponse().getStatus());
		assertEquals(ActionResponse.STATUS.OK, queueResponse.getRefreshResponse().getStatus());
		assertEquals(ActionResponse.STATUS.PENDING, queueResponse.getRestoreResponse().getStatus());

		assertTrue("Response should be ok", queueResponse.isOk());

		queue = (List<IAction>) queueCapability.sendMessage(QueueConstants.GETQUEUE, null);
		assertTrue("Queue should be empty", queue.isEmpty());
	}

	private Object newParamsInterfaceEthernet(String name, String ipName, String mask) {
		EthernetPort eth = new EthernetPort();
		eth.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		eth.setName(name);
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address(ipName);
		ip.setSubnetMask(mask);
		eth.addProtocolEndpoint(ip);
		return eth;
	}

	private Object newParamsInterfaceEthernetPort(String name, int port) {
		EthernetPort eth = new EthernetPort();
		eth.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		eth.setName(name);
		eth.setPortNumber(port);
		eth.setDescription("capability test");
		return eth;
	}

	private Object newParamsInterfaceLogicalPort(String name) {
		LogicalPort eth = new LogicalPort();
		// eth.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		eth.setName(name);
		// eth.setPortNumber(port);
		eth.setDescription("capability test description phy");
		return eth;
	}

	private Object newParamsInterfaceEthernetPortVLAN(String name, int port, int vlanID) {
		EthernetPort eth = new EthernetPort();
		eth.setLinkTechnology(NetworkPort.LinkTechnology.OTHER);
		eth.setName(name);
		eth.setPortNumber(port);
		// vlan specific
		VLANEndpoint vlan = new VLANEndpoint();
		vlan.setVlanID(vlanID);
		eth.addProtocolEndpoint(vlan);
		eth.setDescription("capability test vlan");
		return eth;
	}

	private ComputerSystem newParamsLRWithInterface(String lrName, String ifaceName, int ifacePortNum) {

		ComputerSystem lrModel = new ComputerSystem();
		lrModel.setName(lrName);
		lrModel.setElementName(lrName);

		lrModel.addLogicalDevice((LogicalDevice) newParamsInterfaceEthernetPort(ifaceName, ifacePortNum));
		return lrModel;
	}

}
