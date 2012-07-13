package org.opennaas.extensions.router.junos.actionssets.actions.test.chassis;

import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.core.protocols.sessionmanager.ProtocolManager;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.mock.MockEventManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.protocols.netconf.NetconfProtocolSessionFactory;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.chassis.RemoveTaggedEthernetEncapsulationAction;
import org.opennaas.extensions.router.junos.actionssets.actions.chassis.SetTaggedEthernetEncapsulationAction;
import org.opennaas.extensions.router.junos.actionssets.actions.chassis.SetVlanIdAction;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.LogicalTunnelPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.NetworkPort.LinkTechnology;
import org.opennaas.extensions.router.model.VLANEndpoint;

public class ConfigureEncapsulationActionTest {

	private static SetTaggedEthernetEncapsulationAction		setEncapsulationAction;
	private static RemoveTaggedEthernetEncapsulationAction	removeEncapsulationAction;
	private static SetVlanIdAction							setVlanAction;

	Log														log			= LogFactory.getLog(ConfigureEncapsulationActionTest.class);
	static String											resourceId	= "RandomDevice";

	static ProtocolManager									protocolManager;
	static ProtocolSessionManager							protocolSessionManager;

	static ProtocolSessionContext							netconfContext;

	@BeforeClass
	public static void init() {

		setEncapsulationAction = new SetTaggedEthernetEncapsulationAction();
		removeEncapsulationAction = new RemoveTaggedEthernetEncapsulationAction();
		setVlanAction = new SetVlanIdAction();

		setEncapsulationAction.setModelToUpdate(new ComputerSystem());
		removeEncapsulationAction.setModelToUpdate(new ComputerSystem());
		setVlanAction.setModelToUpdate(new ComputerSystem());

		protocolManager = new ProtocolManager();
		try {
			protocolSessionManager = (ProtocolSessionManager) protocolManager.getProtocolSessionManager(resourceId);
			protocolSessionManager.setEventManager(new MockEventManager());
			netconfContext = newSessionContextNetconf();
			protocolManager.sessionFactoryAdded(new NetconfProtocolSessionFactory(), new HashMap<String, String>() {
				{
					put(ProtocolSessionContext.PROTOCOL, "netconf");
				}
			});
			protocolSessionManager.registerContext(netconfContext);
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void TestActionID() {
		Assert.assertEquals(ActionConstants.SET_TAGGEDETHERNET_ENCAPSULATION, setEncapsulationAction.getActionID());
		Assert.assertEquals(ActionConstants.REMOVE_TAGGEDETHERNET_ENCAPSULATION, removeEncapsulationAction.getActionID());
		Assert.assertEquals(ActionConstants.SET_VLANID, setVlanAction.getActionID());
	}

	@Test
	public void checkParamsTest() throws ActionException {

		LogicalPort phyFeIface = createPhysicalInterface("fe-0/3/1");
		LogicalPort phyLtIface = createPhysicalInterface("lt-1/2/0");
		LogicalPort feIface = createEthernetPort("fe-0/3/1", 2);
		LogicalPort ltIface = createLogicalTunnelPort("lt-1/2/0", 2);

		Assert.assertTrue(setEncapsulationAction.checkParams(phyFeIface));
		Assert.assertTrue(setEncapsulationAction.checkParams(ltIface));
		Assert.assertTrue(removeEncapsulationAction.checkParams(phyFeIface));
		Assert.assertTrue(removeEncapsulationAction.checkParams(ltIface));

		try {
			setEncapsulationAction.checkParams(phyLtIface);
			Assert.fail("SetTaggedEthEncapsulation on physical lt interface should fail");
		} catch (ActionException e) {
		}
		try {
			setEncapsulationAction.checkParams(feIface);
			Assert.fail("SetTaggedEthEncapsulation on logical fe interface should fail");
		} catch (ActionException e) {
		}
		try {
			removeEncapsulationAction.checkParams(phyLtIface);
			Assert.fail("RemoveTaggedEthEncapsulation on physical lt interface should fail");
		} catch (ActionException e) {
		}
		try {
			removeEncapsulationAction.checkParams(feIface);
			Assert.fail("RemoveTaggedEthEncapsulation on logical fe interface should fail");
		} catch (ActionException e) {
		}

		try {
			setVlanAction.checkParams(phyFeIface);
			Assert.fail("Set vlanid action on physical interface should fail");
		} catch (ActionException e) {
		}
		try {
			setVlanAction.checkParams(phyLtIface);
			Assert.fail("Set vlanid action on physical interface should fail");
		} catch (ActionException e) {
		}

		try {
			setVlanAction.checkParams(feIface);
			Assert.fail("Set vlanid action without vlan should fail");
		} catch (ActionException e) {
		}
		try {
			setVlanAction.checkParams(ltIface);
			Assert.fail("Set vlanid action without vlan should fail");
		} catch (ActionException e) {
		}

		// add vlans
		phyFeIface = addVlanToIface(phyFeIface, 12);
		phyLtIface = addVlanToIface(phyLtIface, 12);
		feIface = addVlanToIface(feIface, 12);
		ltIface = addVlanToIface(ltIface, 12);

		try {
			setVlanAction.checkParams(phyFeIface);
			Assert.fail("Set vlanid action on physical interface should fail");
		} catch (ActionException e) {
		}
		try {
			setVlanAction.checkParams(phyLtIface);
			Assert.fail("Set vlanid action on physical interface should fail");
		} catch (ActionException e) {
		}
		Assert.assertTrue(setVlanAction.checkParams(feIface));
		Assert.assertTrue(setVlanAction.checkParams(ltIface));

		// use invalid vlans
		LogicalPort feIface2 = createEthernetPort("fe-0/3/1", 2);
		LogicalPort ltIface2 = createLogicalTunnelPort("lt-1/2/0", 2);
		feIface2 = addVlanToIface(feIface2, 8888);
		ltIface2 = addVlanToIface(ltIface2, 8888);

		try {
			setVlanAction.checkParams(feIface2);
			Assert.fail("Set vlanid action with invalid vlan id should fail");
		} catch (ActionException e) {
		}
		try {
			setVlanAction.checkParams(ltIface2);
			Assert.fail("Set vlanid action with invalid vlan id should fail");
		} catch (ActionException e) {
		}
	}

	@Test
	public void checkTemplate() throws ActionException {
		try {
			// in lt interfaces
			LogicalPort ltIface = createLogicalTunnelPort("lt-1/2/0", 2);
			ltIface = addVlanToIface(ltIface, 101);

			setVlanAction.setParams(ltIface);
			setVlanAction.checkParams(ltIface);
			setVlanAction.prepareMessage();

			Assert.assertEquals(setVlanAction.getTemplate(), "/VM_files/setVlanId.vm");

			// in eth interfaces
			LogicalPort feIface = createEthernetPort("fe-1/2/0", 2);
			feIface = addVlanToIface(feIface, 101);

			setVlanAction.setParams(feIface);
			setVlanAction.checkParams(feIface);
			setVlanAction.prepareMessage();

			Assert.assertEquals(setVlanAction.getTemplate(), "/VM_files/setVlanId.vm");

		} finally {
			setVlanAction.setParams(null);
		}
	}

	/**
	 * TODO, it is necessary to implement the dummy state to configure vlans
	 * 
	 * @throws ActionException
	 */
	@Test
	public void testExecuteforETH() throws ActionException {

		LogicalPort feIface = createEthernetPort("fe-0/3/2", 4);
		feIface = addVlanToIface(feIface, 3);

		setVlanAction.setParams(feIface);
		ActionResponse response = setVlanAction.execute(protocolSessionManager);
		Assert.assertEquals(ActionConstants.SET_VLANID, response.getActionID());
		List<Response> responses = response.getResponses();
		for (Response resp : responses) {
			Assert.assertEquals(Response.Status.OK, resp.getStatus());
		}

		// An interface with vlan-tagging and subinterfaces with vlans
		LogicalPort phyFeIface = createPhysicalInterface("fe-0/1/3");

		// FIXME This test fails due to mock router not filering config
		// setEncapsulationAction.setParams(phyFeIface);
		// response = setEncapsulationAction.execute(protocolSessionManager);
		// Assert.assertEquals(ActionConstants.SET_TAGGEDETHERNET_ENCAPSULATION, response.getActionID());
		// responses = response.getResponses();
		// for (Response resp : responses) {
		// Assert.assertEquals(Response.Status.OK, resp.getStatus());
		// }

		try {
			removeEncapsulationAction.setParams(phyFeIface);
			response = removeEncapsulationAction.execute(protocolSessionManager);
			// Assert.assertEquals(ActionConstants.REMOVE_TAGGEDETHERNET_ENCAPSULATION, response.getActionID());
			// responses = response.getResponses();
			// for (Response resp : responses) {
			// Assert.assertEquals(Response.Status.OK, resp.getStatus());
			// }
		} catch (ActionException e) {
			Assert.assertTrue(e.getLocalizedMessage().contains(
					"Interface has subinterfaces with vlanId. Please remove them before changing encapsulation."));
		}
	}

	@Test
	public void testExecuteforLogicalTunnel() throws ActionException {

		LogicalPort ltIface = createLogicalTunnelPort("lt-1/2/0", 12);
		ltIface = addVlanToIface(ltIface, 2);

		setVlanAction.setParams(ltIface);
		ActionResponse response = setVlanAction.execute(protocolSessionManager);
		Assert.assertEquals(ActionConstants.SET_VLANID, response.getActionID());
		List<Response> responses = response.getResponses();
		for (Response resp : responses) {
			Assert.assertEquals(Response.Status.OK, resp.getStatus());
		}

		// FIXME This test fails due to mock router not filering config
		// setEncapsulationAction.setParams(ltIface);
		// response = setEncapsulationAction.execute(protocolSessionManager);
		// Assert.assertEquals(ActionConstants.SET_TAGGEDETHERNET_ENCAPSULATION, response.getActionID());
		// responses = response.getResponses();
		// for (Response resp : responses) {
		// Assert.assertEquals(Response.Status.OK, resp.getStatus());
		// }

		removeEncapsulationAction.setParams(ltIface);
		response = removeEncapsulationAction.execute(protocolSessionManager);
		Assert.assertEquals(ActionConstants.REMOVE_TAGGEDETHERNET_ENCAPSULATION, response.getActionID());
		responses = response.getResponses();
		for (Response resp : responses) {
			Assert.assertEquals(Response.Status.OK, resp.getStatus());
		}
	}

	private LogicalPort createLogicalInterface(String ifaceName, int portNumber) {
		NetworkPort iface = new NetworkPort();
		iface.setName(ifaceName);
		iface.setPortNumber(portNumber);
		return iface;
	}

	private LogicalPort createPhysicalInterface(String ifaceName) {
		LogicalPort iface = new LogicalPort();
		iface.setName(ifaceName);
		return iface;
	}

	private LogicalPort createEthernetPort(String ifaceName, int portNumber) {
		EthernetPort eth = new EthernetPort();
		eth.setName(ifaceName);
		eth.setPortNumber(portNumber);
		return eth;
	}

	private LogicalPort createLogicalTunnelPort(String ifaceName, int portNumber) {
		LogicalTunnelPort lt = new LogicalTunnelPort();
		lt.setName(ifaceName);
		lt.setPortNumber(portNumber);
		return lt;
	}

	private LogicalPort addVlanToIface(LogicalPort iface, int vlanId) {
		VLANEndpoint vlan = new VLANEndpoint();
		vlan.setName(String.valueOf(vlanId)); // it's read from here
		vlan.setVlanID(vlanId);
		iface.addProtocolEndpoint(vlan);
		return iface;
	}

	private EthernetPort newParamEthernetPort(String elementname, int portNumber, int vlanID) {
		EthernetPort eth = new EthernetPort();
		eth.setLinkTechnology(LinkTechnology.OTHER);// VLAN
		eth.setName(elementname);
		eth.setPortNumber(portNumber);

		VLANEndpoint vlan = new VLANEndpoint();
		vlan.setVlanID(vlanID);
		eth.addProtocolEndpoint(vlan);
		return eth;
	}

	private LogicalTunnelPort newParamLogicalTunnetPort(String elementname, int portNumber, int vlanID) {
		LogicalTunnelPort lt = new LogicalTunnelPort();
		lt.setName(elementname);

		lt.setPortNumber(portNumber);

		lt.setLinkTechnology(LinkTechnology.OTHER);// VLAN
		VLANEndpoint vlan = new VLANEndpoint();
		vlan.setVlanID(vlanID);
		lt.addProtocolEndpoint(vlan);
		return lt;
	}

	/**
	 * Configure the protocol to connect
	 */
	private static ProtocolSessionContext newSessionContextNetconf() {
		String uri = System.getProperty("protocol.uri");
		if (uri == null || uri.equals("${protocol.uri}")) {
			uri = "mock://user:pass@host.net:2212/mocksubsystem";
		}

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();

		protocolSessionContext.addParameter(
				ProtocolSessionContext.PROTOCOL_URI, uri);
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"netconf");
		protocolSessionContext.addParameter(ProtocolSessionContext.AUTH_TYPE,
				"password");
		// ADDED
		return protocolSessionContext;
	}
}
