package net.i2cat.mantychore.action.junos.test;

import junit.framework.Assert;
import net.i2cat.mantychore.actionsets.junos.actions.DeleteSubInterfaceAction;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import org.opennaas.core.protocols.sessionmanager.impl.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
		eth.setElementName("fe-0/0/2");
		eth.setPortNumber(13);

		action.setParams(eth);
		try {
			ActionResponse response = action.execute(protocolsessionmanager);
			Assert.assertTrue(response.getActionID().equals("deletesubinterface"));

		} catch (ActionException e) {

			e.printStackTrace();
			Assert.fail();
		}
		net.i2cat.mantychore.model.System routerModel = (net.i2cat.mantychore.model.System) action.getModelToUpdate();
		Assert.assertNotNull(routerModel);

	}
}
