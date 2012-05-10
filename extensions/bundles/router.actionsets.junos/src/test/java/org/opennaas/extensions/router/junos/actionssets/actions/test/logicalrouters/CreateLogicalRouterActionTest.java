package org.opennaas.extensions.router.junos.actionssets.actions.test.logicalrouters;

import java.util.HashMap;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.core.protocols.sessionmanager.ProtocolManager;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.mock.MockEventManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.protocols.netconf.NetconfProtocolSessionFactory;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.logicalrouters.CreateLogicalRouterAction;
import org.opennaas.extensions.router.model.ComputerSystem;

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
	public void checkParamsTest() throws ActionException {

		ComputerSystem lrModel = new ComputerSystem();
		lrModel.setName("L1");

		Assert.assertTrue(action.checkParams(lrModel));
	}

	public void checkTemplate() {
		try {
			ComputerSystem lrModel = new ComputerSystem();
			lrModel.setName("L1");

			Assert.assertTrue(action.checkParams(lrModel));

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
			ComputerSystem lrModel = new ComputerSystem();
			lrModel.setName("L1");

			action.setParams(lrModel);
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
