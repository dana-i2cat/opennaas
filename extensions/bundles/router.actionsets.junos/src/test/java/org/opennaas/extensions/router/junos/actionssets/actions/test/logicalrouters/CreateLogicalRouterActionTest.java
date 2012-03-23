package org.opennaas.extensions.router.junos.actionssets.actions.test.logicalrouters;

import java.util.HashMap;

import junit.framework.Assert;
import mock.MockEventManager;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.logicalrouters.CreateLogicalRouterAction;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.protocols.netconf.NetconfProtocolSessionFactory;
import org.opennaas.core.protocols.sessionmanager.impl.ProtocolManager;
import org.opennaas.core.protocols.sessionmanager.impl.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;

public class CreateLogicalRouterActionTest {
	static CreateLogicalRouterAction	action;
	Log									log			= LogFactory.getLog(CreateLogicalRouterActionTest.class);
	static String						resourceId	= "RandomDevice";

	static ProtocolManager				protocolManager;
	static ProtocolSessionManager		protocolSessionManager;

	static ProtocolSessionContext		netconfContext;

	@BeforeClass
	public static void init() {
		action = new CreateLogicalRouterAction();
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
		Assert.assertEquals("Wrong ActionID", ActionConstants.CREATELOGICALROUTER, action.getActionID());
	}

	@Test
	public void checkParamsTest() {

		// only accept EthernetPort and LogicalTunnelPort type
		try {
			Assert.assertTrue(action.checkParams(new String("L1")));
		} catch (ActionException a) {
			a.printStackTrace();
			Assert.fail(a.getMessage());
		}

	}

	public void checkTemplate() {
		try {
			Assert.assertTrue(action.checkParams(new String("L1")));

			action.prepareMessage();

			Assert.assertEquals(action.getTemplate(), "/VM_files/createLogicalRouter.vm");

		} catch (ActionException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		action.setParams(null);

	}

	@Test
	public void createLRTest() {

		try {

			action.setParams(new String("L1"));
			ActionResponse response = action.execute(protocolSessionManager);
			System.out.println(action.getVelocityMessage());
		} catch (ActionException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

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
