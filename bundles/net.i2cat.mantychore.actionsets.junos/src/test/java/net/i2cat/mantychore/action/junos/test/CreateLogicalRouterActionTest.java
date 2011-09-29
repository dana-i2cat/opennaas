package net.i2cat.mantychore.action.junos.test;

import java.util.HashMap;

import junit.framework.Assert;
import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.actionsets.junos.actions.CreateLogicalRouterAction;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.protocols.netconf.NetconfProtocolSessionFactory;
import net.i2cat.nexus.protocols.sessionmanager.impl.ProtocolManager;
import net.i2cat.nexus.protocols.sessionmanager.impl.ProtocolSessionManager;
import net.i2cat.nexus.resources.action.ActionException;
import net.i2cat.nexus.resources.action.ActionResponse;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;

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
