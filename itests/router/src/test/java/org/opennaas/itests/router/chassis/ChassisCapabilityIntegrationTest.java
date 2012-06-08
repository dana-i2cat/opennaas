package org.opennaas.itests.router.chassis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

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
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.capability.ICapabilityLifecycle;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.mock.MockResource;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capability.chassis.IChassisCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
import org.opennaas.extensions.router.model.VLANEndpoint;
import org.opennaas.itests.router.mock.MockBootstrapper;
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
	private final static Log		log			= LogFactory.getLog(ChassisCapabilityIntegrationTest.class);

	private final String			deviceID	= "junos";
	private final String			queueID		= "queue";

	private MockResource			mockResource;
	private IChassisCapability		chassisCapability;
	private IQueueManagerCapability	queueCapability;

	@Inject
	private BundleContext			bundleContext;

	@Inject
	@Filter("(capability=queue)")
	private ICapabilityFactory		queueManagerFactory;

	@Inject
	private IProtocolManager		protocolManager;

	@Inject
	@Filter("(capability=chassis)")
	private ICapabilityFactory		chassisFactory;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.repository)")
	private BlueprintContainer		routerService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.actionsets.junos)")
	private BlueprintContainer		junosActionsetService;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-router", "opennaas-junos"),
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

		queueCapability = (IQueueManagerCapability) queueManagerFactory.create(mockResource);
		((ICapabilityLifecycle) queueCapability).initialize();

		protocolManager.getProtocolSessionManagerWithContext(mockResource.getResourceId(), newSessionContextNetconf());

		// Test elements not null
		log.info("Checking chassis factory");
		Assert.assertNotNull(chassisFactory);
		log.info("Checking capability descriptor");
		Assert.assertNotNull(mockResource.getResourceDescriptor().getCapabilityDescriptor("chassis"));
		log.info("Creating chassis capability");
		chassisCapability = (IChassisCapability) chassisFactory.create(mockResource);
		Assert.assertNotNull(chassisCapability);
		((ICapabilityLifecycle) chassisCapability).initialize();

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
	public void testSetEncapsulationAction() throws CapabilityException, ProtocolException {

		int actionCount = 0;

		chassisCapability.setEncapsulation(newParamsInterfaceEthernetPort("fe-0/1/0", 13), ProtocolIFType.LAYER_2_VLAN_USING_802_1Q);
		actionCount++;

		List<IAction> queue = (List<IAction>) queueCapability.getActions();
		assertEquals(actionCount, queue.size());

		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
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
	public void testChassisAction() throws CapabilityException, ProtocolException {
		log.info("TEST CHASSIS ACTIONS");

		int actionCount = 0;

		chassisCapability.createSubInterface(newParamsInterfaceEthernetPort("fe-0/1/0", 13));
		actionCount++;

		chassisCapability.deleteSubInterface(newParamsInterfaceEthernetPort("fe-0/1/0", 13));
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

		List<LogicalPort> lInterfaces = new ArrayList<LogicalPort>();
		lInterfaces.add(newParamsInterfaceEthernetPort("fe-0/1/0", 13));
		chassisCapability.addInterfacesToLogicalRouter(newParamsLRWithInterface("cpe1"), lInterfaces);
		actionCount++;

		lInterfaces = new ArrayList<LogicalPort>();
		lInterfaces.add(newParamsInterfaceEthernetPort("fe-0/0/3", 13));
		chassisCapability.removeInterfacesFromLogicalRouter(newParamsLRWithInterface("cpe2"), lInterfaces);
		actionCount++;

		List<IAction> queue = (List<IAction>) queueCapability.getActions();
		assertEquals(actionCount, queue.size());

		QueueResponse queueResponse = queueCapability.execute();
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

		queue = (List<IAction>) queueCapability.getActions();
		assertTrue("Queue should be empty", queue.isEmpty());
	}

	private EthernetPort newParamsInterfaceEthernet(String name, String ipName, String mask) {
		EthernetPort eth = new EthernetPort();
		eth.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		eth.setName(name);
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address(ipName);
		ip.setSubnetMask(mask);
		eth.addProtocolEndpoint(ip);
		return eth;
	}

	private EthernetPort newParamsInterfaceEthernetPort(String name, int port) {
		EthernetPort eth = new EthernetPort();
		eth.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		eth.setName(name);
		eth.setPortNumber(port);
		eth.setDescription("capability test");
		return eth;
	}

	private LogicalPort newParamsInterfaceLogicalPort(String name) {
		LogicalPort eth = new LogicalPort();
		// eth.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		eth.setName(name);
		// eth.setPortNumber(port);
		eth.setDescription("capability test description phy");
		return eth;
	}

	private EthernetPort newParamsInterfaceEthernetPortVLAN(String name, int port, int vlanID) {
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

	private ComputerSystem newParamsLRWithInterface(String lrName) {
		ComputerSystem lrModel = new ComputerSystem();
		lrModel.setName(lrName);
		lrModel.setElementName(lrName);
		return lrModel;
	}

}
