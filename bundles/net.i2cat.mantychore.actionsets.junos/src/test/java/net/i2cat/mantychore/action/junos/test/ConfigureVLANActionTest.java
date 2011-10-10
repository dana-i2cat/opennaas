package net.i2cat.mantychore.action.junos.test;

import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;
import mock.MockEventManager;
import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.actionsets.junos.actions.ConfigureVLANAction;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.LogicalTunnelPort;
import net.i2cat.mantychore.model.NetworkPort.LinkTechnology;
import net.i2cat.mantychore.model.VLANEndpoint;
import net.i2cat.mantychore.protocols.netconf.NetconfProtocolSessionFactory;
import org.opennaas.core.protocols.sessionmanager.impl.ProtocolManager;
import org.opennaas.core.protocols.sessionmanager.impl.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConfigureVLANActionTest {
	static ConfigureVLANAction		action;
	Log								log			= LogFactory.getLog(ConfigureVLANActionTest.class);
	static String					resourceId	= "RandomDevice";

	static ProtocolManager			protocolManager;
	static ProtocolSessionManager	protocolSessionManager;

	static ProtocolSessionContext	netconfContext;

	@BeforeClass
	public static void init() {
		action = new ConfigureVLANAction();
		action.setModelToUpdate(new ComputerSystem());
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
		Assert.assertEquals("Wrong ActionID", ActionConstants.SETVLAN, action.getActionID());
	}

	@Test
	public void checkParamsTest() {

		// only accept EthernetPort and LogicalTunnelPort type
		try {
			Assert.assertTrue(action.checkParams(newParamEthernetPort("fe-0/3/1", 2, 3)));

			Assert.assertTrue(action.checkParams(newParamLogicalTunnetPort("lt-1/2/0", 2, 101, 3)));

		} catch (ActionException a) {
			a.printStackTrace();
			Assert.fail(a.getMessage());
		}

	}

	public void checkTemplate() {
		try {
			action.checkParams(newParamEthernetPort("fe-0/3/1", 2, 3));
			action.prepareMessage();

			Assert.assertEquals(action.getTemplate(), "/VM_files/configureEthVLAN.vm");

			action.checkParams(newParamLogicalTunnetPort("lt-1/2/0", 2, 101, 3));
			action.prepareMessage();

			Assert.assertEquals(action.getTemplate(), "/VM_files/configureLogicalTunnelVLAN.vm");

		} catch (ActionException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		action.setParams(null);

	}

	@Test
	public void testExecuteforETH() {

		try {
			action.setParams(newParamEthernetPort("fe-0/3/2", 4, 3));
			ActionResponse response = action.execute(protocolSessionManager);

			Assert.assertEquals(ActionConstants.SETVLAN, response.getActionID());
			List<Response> responses = response.getResponses();
			for (Response resp : responses) {
				Assert.assertEquals(Response.Status.OK, resp.getStatus());
			}

		} catch (ActionException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testExecuteforLogicalTunnel() {

		try {
			action.setParams(newParamLogicalTunnetPort("lt-0/1/2", 12, 2, 3));
			ActionResponse response = action.execute(protocolSessionManager);

			Assert.assertEquals(ActionConstants.SETVLAN, response.getActionID());
			List<Response> responses = response.getResponses();
			for (Response resp : responses) {
				Assert.assertEquals(Response.Status.OK, resp.getStatus());
			}

		} catch (ActionException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	public EthernetPort newParamEthernetPort(String elementname, int portNumber, int vlanID) {
		EthernetPort eth = new EthernetPort();
		eth.setLinkTechnology(LinkTechnology.OTHER);// VLAN
		eth.setName(elementname);
		eth.setPortNumber(portNumber);

		VLANEndpoint vlan = new VLANEndpoint();
		vlan.setVlanID(vlanID);
		eth.addProtocolEndpoint(vlan);
		return eth;
	}

	public LogicalTunnelPort newParamLogicalTunnetPort(String elementname, int portNumber, long peer_unit, int vlanID) {
		LogicalTunnelPort lt = new LogicalTunnelPort();
		lt.setName(elementname);

		lt.setPortNumber(portNumber);
		lt.setPeer_unit(peer_unit);

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
		// ADDED
		return protocolSessionContext;

	}
}
