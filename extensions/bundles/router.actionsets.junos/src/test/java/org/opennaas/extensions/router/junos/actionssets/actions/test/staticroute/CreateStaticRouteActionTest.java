package org.opennaas.extensions.router.junos.actionssets.actions.test.staticroute;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.staticroute.CreateStaticRouteAction;
import org.opennaas.extensions.router.junos.actionssets.actions.test.ActionTestHelper;
import org.opennaas.extensions.router.model.ComputerSystem;

public class CreateStaticRouteActionTest {
	Log										log	= LogFactory.getLog(CreateStaticRouteActionTest.class);
	private static CreateStaticRouteAction	action;
	static ActionTestHelper					helper;
	static ProtocolSessionManager			protocolsessionmanager;

	@BeforeClass
	public static void init() {
		action = new CreateStaticRouteAction();
		action.setModelToUpdate(new ComputerSystem());
		helper = new ActionTestHelper();
		action.setParams(helper.newParamsInterfaceEthernet());
		helper = new ActionTestHelper();
		protocolsessionmanager = helper.getProtocolSessionManager();
	}

	@Test
	public void actionIDTest() {
		Assert.assertEquals("Wrong ActionID", ActionConstants.STATIC_ROUTE_CREATE,
				action.getActionID());
	}

	@Test
	public void paramsTest() {
		// this action always have null params
		Assert.assertNotNull("Null parameters", action.getParams());
	}

	/**
	 * Create static route v4.
	 * 
	 * @throws IOException
	 */
	@Test
	public void executeActionTest() throws IOException {
		action.setModelToUpdate(new ComputerSystem());
		action.setParams(getParams());
		try {
			ActionResponse response = action.execute(protocolsessionmanager);
			Assert.assertTrue(response.getActionID()
					.equals(ActionConstants.STATIC_ROUTE_CREATE));
		} catch (ActionException e) {
			e.printStackTrace();
			Assert.fail();
		}
		org.opennaas.extensions.router.model.System computerSystem = (org.opennaas.extensions.router.model.System) action.getModelToUpdate();
		Assert.assertNotNull(computerSystem);
	}

	/**
	 * 
	 * 
	 * @throws IOException
	 * @throws ActionException
	 */
	@Test(expected = ActionException.class)
	public void executeActionWrongParamsTest() throws ActionException {

		action.setModelToUpdate(new ComputerSystem());
		action.setParams(wrongParams());

		ActionResponse response = action.execute(protocolsessionmanager);

	}

	/**
	 * @return
	 */
	private String[] getParams() {
		String[] params = new String[3];
		params[0] = "0.0.0.0/0";
		params[1] = "192.168.1.1";
		params[2] = "false";
		return params;
	}

	private String[] wrongParams() {
		String[] params = new String[3];
		params[0] = "0.0.0.0/0";
		params[1] = "FDEC:34:52::A6/64";
		params[2] = "false";
		return params;
	}

}
