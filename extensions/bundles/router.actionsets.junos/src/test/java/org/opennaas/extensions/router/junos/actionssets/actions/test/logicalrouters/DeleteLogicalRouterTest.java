package org.opennaas.extensions.router.junos.actionssets.actions.test.logicalrouters;

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
import org.opennaas.core.resources.mock.MockEventManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.protocols.netconf.NetconfProtocolSessionFactory;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.logicalrouters.DeleteLogicalRouterAction;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.LogicalDevice;

public class DeleteLogicalRouterTest {
	private static DeleteLogicalRouterAction	action;
	Log											log			= LogFactory.getLog(DeleteLogicalRouterTest.class);
	static String								resourceId	= "RandomDevice";

	static ProtocolManager						protocolManager;
	static ProtocolSessionManager				protocolSessionManager;

	static ProtocolSessionContext				netconfContext;

	@BeforeClass
	public static void init() {
		action = new DeleteLogicalRouterAction();
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
		Assert.assertEquals("Wrong ActionID", ActionConstants.DELETELOGICALROUTER, action.getActionID());
	}

	@Test
	public void paramsTest() {
		// this action always have null params
		Assert.assertNull("Not accepted param", action.getParams());
	}

	public void testExecute() {

		try {
			ActionResponse response = action.execute(protocolSessionManager);
		} catch (ActionException e) {
			e.printStackTrace();
			Assert.fail();
		}
		org.opennaas.extensions.router.model.System routerModel = (org.opennaas.extensions.router.model.System) action.getModelToUpdate();
		Assert.assertNotNull(routerModel);
		List<LogicalDevice> ld = routerModel.getLogicalDevices();
		for (LogicalDevice device : ld) {
			EthernetPort ep = (EthernetPort) device;
			log.info(ep.getName());
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
