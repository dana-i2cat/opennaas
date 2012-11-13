package org.opennaas.extensions.router.junos.actionssets.actions.test.chassis;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.chassis.DeleteSubInterfaceAction;
import org.opennaas.extensions.router.junos.actionssets.actions.test.ActionTestHelper;
import org.opennaas.extensions.router.junos.actionssets.actions.test.GetConfigActionTest;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;

public class DeleteSubInterfaceActionTest {
	private static DeleteSubInterfaceAction	action;
	Log										log	= LogFactory.getLog(GetConfigActionTest.class);
	static ActionTestHelper					helper;
	static ProtocolSessionManager			protocolsessionmanager;

	@BeforeClass
	public static void init() {

		helper = new ActionTestHelper();
		protocolsessionmanager = helper.getProtocolSessionManager();
	}

	@Before
	public void setNewInterface() {
		// Set new interface
		// get configuration

	}

	@Test
	public void testExecuteAction() {
		// delete new interface
		// get configuration to check it is deleted
		action = new DeleteSubInterfaceAction();
		action.setModelToUpdate(new ComputerSystem());
		// elements to delete
		EthernetPort eth = new EthernetPort();
		eth.setName("fe-0/0/2");
		eth.setPortNumber(13);

		action.setParams(eth);
		try {
			ActionResponse response = action.execute(protocolsessionmanager);
			Assert.assertTrue(response.getActionID().equals(ActionConstants.DELETESUBINTERFACE));

		} catch (ActionException e) {

			e.printStackTrace();
			Assert.fail();
		}
		org.opennaas.extensions.router.model.System routerModel = (org.opennaas.extensions.router.model.System) action.getModelToUpdate();
		Assert.assertNotNull(routerModel);

	}
}
