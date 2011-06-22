package net.i2cat.mantychore.action.junos.test;

import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;
import net.i2cat.mantychore.actionsets.junos.actions.ConfirmAction;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.protocols.netconf.NetconfProtocolSessionFactory;
import net.i2cat.nexus.protocols.sessionmanager.impl.ProtocolManager;
import net.i2cat.nexus.protocols.sessionmanager.impl.ProtocolSessionManager;
import net.i2cat.nexus.resources.action.ActionException;
import net.i2cat.nexus.resources.action.ActionResponse;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;
import net.i2cat.nexus.resources.queue.QueueConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConfirmActionTest {
	private static ConfirmAction	action;
	Log								log			= LogFactory.getLog(GetConfigActionTest.class);
	static String					resourceId	= "RandomDevice";

	static ProtocolManager			protocolManager;
	static ProtocolSessionManager	protocolSessionManager;

	static ProtocolSessionContext	netconfContext;

	@BeforeClass
	public static void init() {
		action = new ConfirmAction();
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
		Assert.assertEquals("Wrong ActionID", QueueConstants.CONFIRM, action.getActionID());
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
		net.i2cat.mantychore.model.System routerModel = (net.i2cat.mantychore.model.System) action.getModelToUpdate();
		Assert.assertNotNull(routerModel);
		List<LogicalDevice> ld = routerModel.getLogicalDevices();
		for (LogicalDevice device : ld) {
			EthernetPort ep = (EthernetPort) device;
			log.info(ep.getElementName());
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
