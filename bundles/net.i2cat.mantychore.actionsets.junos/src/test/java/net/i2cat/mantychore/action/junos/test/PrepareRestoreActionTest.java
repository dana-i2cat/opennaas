package net.i2cat.mantychore.action.junos.test;

import java.util.HashMap;

import junit.framework.Assert;
import net.i2cat.mantychore.actionsets.junos.actions.PrepareAction;
import net.i2cat.mantychore.actionsets.junos.actions.RestoreAction;
import net.i2cat.mantychore.protocols.netconf.NetconfProtocolSessionFactory;
import net.i2cat.nexus.protocols.sessionmanager.impl.ProtocolManager;
import net.i2cat.nexus.protocols.sessionmanager.impl.ProtocolSessionManager;
import net.i2cat.nexus.resources.action.ActionException;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;
import net.i2cat.nexus.resources.queue.QueueConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

public class PrepareRestoreActionTest {
	private RestoreAction	restoreAction;
	private PrepareAction	prepareAction;
	Log						log			= LogFactory.getLog(GetConfigActionTest.class);
	String					resourceId	= "RandomDevice";

	ProtocolManager			protocolManager;
	ProtocolSessionManager	protocolSessionManager;

	ProtocolSessionContext	netconfContext;

	@Before
	public void init() {
		restoreAction = new RestoreAction();
		prepareAction = new PrepareAction();
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
		Assert.assertEquals("Wrong ActionID", QueueConstants.RESTORE, restoreAction.getActionID());
		Assert.assertEquals("Wrong ActionID", QueueConstants.PREPARE, prepareAction.getActionID());

	}

	@Test
	public void paramsTest() {
		// this action always have null params
		Assert.assertNull("Not accepted param", restoreAction.getParams());
		Assert.assertNull("Not accepted param", prepareAction.getParams());

	}

	@Test
	public void prepareVelocityMessage() {

	}

	@Test
	public void testExecute() {

		try {
			prepareAction.execute(protocolSessionManager);
			restoreAction.execute(protocolSessionManager);
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
}
