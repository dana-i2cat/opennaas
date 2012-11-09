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

	@Test
	public void templateTest() {
		// this action always have this template as a default
		Assert.assertEquals("Not accepted param", "/VM_files/createStaticRoute.vm", action.getTemplate());
	}

	/**
	 * Create two OSPFProtocolEndpoint with state to enable
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
	 * @return
	 */
	private String[] getParams() {
		String[] params = new String[4];
		params[0] = "0.0.0.0";
		params[1] = "0.0.0.0";
		params[2] = "192.168.1.1";
		params[3] = "false";
		return params;
	}
}
