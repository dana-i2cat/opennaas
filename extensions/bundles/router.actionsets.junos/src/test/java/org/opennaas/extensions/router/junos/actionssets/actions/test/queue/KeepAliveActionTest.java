package org.opennaas.extensions.router.junos.actionssets.actions.test.queue;

import java.util.HashMap;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.junit.Before;
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
import org.opennaas.extensions.router.junos.actionssets.actions.queue.IsAliveAction;
import org.opennaas.extensions.router.model.ComputerSystem;

public class KeepAliveActionTest {

	private IsAliveAction	action;
	Log						log			= LogFactory.getLog(KeepAliveActionTest.class);
	String					resourceId	= "RandomDevice";

	ProtocolManager			protocolManager;
	ProtocolSessionManager	protocolSessionManager;

	ProtocolSessionContext	netconfContext;

	@Before
	public void init() {
		action = new IsAliveAction();
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
		Assert.assertEquals("Wrong ActionID", ActionConstants.ISALIVE, action.getActionID());
	}

	@Test
	public void paramsTest() {
		// this action always have null params
		Assert.assertNull("Not accepted param", action.getParams());
	}

	@Test
	public void prepareVelocityMessage() {

	}

	@Test
	public void testExecute() {

		try {
			ActionResponse response = action.execute(protocolSessionManager);
		} catch (ActionException e) {
			e.printStackTrace();
			Assert.fail();
		}
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

	protected void prepareMessage() throws ResourceNotFoundException, ParseErrorException, ActionException, Exception {

	}

}
