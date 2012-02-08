package net.i2cat.mantychore.action.junos.test;

import junit.framework.Assert;
import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.actionsets.junos.actions.SetIPv4Action;
import net.i2cat.mantychore.model.ComputerSystem;
import org.opennaas.core.protocols.sessionmanager.impl.ProtocolSessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;

public class SetIPv4ActionTest {
	Log								log	= LogFactory.getLog(GetConfigActionTest.class);
	private static SetIPv4Action	action;
	static ActionTestHelper			helper;
	static ProtocolSessionManager	protocolsessionmanager;

	@BeforeClass
	public static void init() {
		action = new SetIPv4Action();
		action.setModelToUpdate(new ComputerSystem());
		helper = new ActionTestHelper();
		action.setParams(helper.newParamsInterfaceEthernet());
		// TODO
		// /need to add extra params

		// NetworkPort networkPort = new NetworkPort();
		// velocityEngine.addExtraParam("networkPort", networkPort);
		//
		// IPUtilsHelper ipUtilsHelper = new IPUtilsHelper();
		// velocityEngine.addExtraParam("ipUtilsHelper", ipUtilsHelper);
		helper = new ActionTestHelper();
		protocolsessionmanager = helper.getProtocolSessionManager();
	}

	@Test
	public void TestActionID() {
		Assert.assertEquals("Wrong ActionID", ActionConstants.SETIPv4, action.getActionID());
	}

	@Test
	public void paramsTest() {
		// this action always have null params
		Assert.assertNotNull("Null parameters", action.getParams());
	}

	@Test
	public void templateTest() {
		// this action always have this template as a default
		Assert.assertEquals("Not accepted param", "/VM_files/configureIPv4.vm", action.getTemplate());
	}

}
