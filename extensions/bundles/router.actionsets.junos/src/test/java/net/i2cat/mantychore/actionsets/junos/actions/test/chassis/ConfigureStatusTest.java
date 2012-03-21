package net.i2cat.mantychore.actionsets.junos.actions.test.chassis;

import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;
import mock.MockEventManager;
import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.actionsets.junos.actions.chassis.ConfigureStatusAction;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.LogicalPort;
import net.i2cat.mantychore.model.ManagedSystemElement.OperationalStatus;
import net.i2cat.mantychore.protocols.netconf.NetconfProtocolSessionFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.core.protocols.sessionmanager.impl.ProtocolManager;
import org.opennaas.core.protocols.sessionmanager.impl.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

public class ConfigureStatusTest {
	static ConfigureStatusAction	action;
	Log								log			= LogFactory.getLog(ConfigureStatusTest.class);
	static String					resourceId	= "RandomDevice";

	static ProtocolManager			protocolManager;
	static ProtocolSessionManager	protocolSessionManager;

	static ProtocolSessionContext	netconfContext;

	@BeforeClass
	public static void init() {
		action = new ConfigureStatusAction();
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
		Assert.assertEquals("Wrong ActionID", ActionConstants.CONFIGURESTATUS, action.getActionID());
	}

	@Test
	public void checkParamsTest() {

		// only accept EthernetPort and LogicalTunnelPort type
		try {
			Assert.assertTrue(action.checkParams(newParamsConfigureStatus("fe-0/3/1", OperationalStatus.STOPPED)));

		} catch (ActionException a) {
			a.printStackTrace();
			Assert.fail(a.getMessage());
		}

	}

	public void checkTemplate() {
		try {
			Assert.assertTrue(action.checkParams(newParamsConfigureStatus("fe-0/3/2", OperationalStatus.STOPPED)));
			action.prepareMessage();

			Assert.assertEquals(action.getTemplate(), "/VM_files/configureStatus.vm");

		} catch (ActionException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		action.setParams(null);

	}

	@Test
	public void configureStatusTest() {

		try {
			action.setParams(newParamsConfigureStatus("fe-0/3/2", OperationalStatus.STOPPED));
			ActionResponse response = action.execute(protocolSessionManager);

			Assert.assertEquals(ActionConstants.CONFIGURESTATUS, response.getActionID());
			List<Response> responses = response.getResponses();
			for (Response resp : responses) {
				Assert.assertEquals(Response.Status.OK, resp.getStatus());
			}

		} catch (ActionException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	private Object newParamsConfigureStatus(String interfaceName, OperationalStatus status) {

		LogicalPort logicalPort = new LogicalPort();
		logicalPort.setName(interfaceName);
		logicalPort.setOperationalStatus(status);
		return logicalPort;
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
