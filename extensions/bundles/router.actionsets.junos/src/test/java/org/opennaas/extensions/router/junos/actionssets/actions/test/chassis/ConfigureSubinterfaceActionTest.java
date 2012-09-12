package org.opennaas.extensions.router.junos.actionssets.actions.test.chassis;

import static org.junit.Assert.assertEquals;
import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.chassis.ConfigureSubInterfaceAction;
import org.opennaas.extensions.router.junos.actionssets.actions.test.ActionTestHelper;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;

public class ConfigureSubinterfaceActionTest {

	private static ConfigureSubInterfaceAction	action;
	private static ActionTestHelper				helper;
	private static ProtocolSessionManager		protocolsessionmanager;
	private final static String					iface1	= "fe-2/0/1.0";

	Log											log		= LogFactory.getLog(ConfigureSubinterfaceActionTest.class);

	@BeforeClass
	public static void init() {
		action = new ConfigureSubInterfaceAction();
		action.setModelToUpdate(new ComputerSystem());
		helper = new ActionTestHelper();
		protocolsessionmanager = helper.getProtocolSessionManager();
	}

	@Test
	public void actionIDTest() {

		Assert.assertEquals("Wrong ActionID", ActionConstants.CONFIGURESUBINTERFACE,
				action.getActionID());
	}

	@Test
	public void UntaggedEthernetInterfaceTest() throws ActionException {
		action.setParams(newUntaggedInterface(iface1));
		action.checkParams(action.getParams());

		Assert.assertEquals("Unvalid template.", "/VM_files/configureEthWithoutVLAN.vm", action.getTemplate());

		ActionResponse response = action.execute(protocolsessionmanager);

		Assert.assertTrue(response.getActionID()
				.equals(ActionConstants.CONFIGURESUBINTERFACE));

		assertEquals(STATUS.OK, response.getStatus());
	}

	private EthernetPort newUntaggedInterface(String interfaceName) {
		EthernetPort eth = new EthernetPort();
		String[] args = interfaceName.split("\\.");
		eth.setName(args[0]);
		eth.setPortNumber(Integer.parseInt(args[1]));
		return eth;
	}
}
