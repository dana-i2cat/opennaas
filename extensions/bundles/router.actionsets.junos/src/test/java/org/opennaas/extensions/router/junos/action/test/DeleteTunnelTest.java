package org.opennaas.extensions.router.junos.action.test;

import java.io.IOException;

import junit.framework.Assert;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.gretunnel.DeleteTunnelAction;
import org.opennaas.extensions.router.junos.actionssets.actions.test.ActionTestHelper;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.GRETunnelService;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.core.protocols.sessionmanager.impl.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;

public class DeleteTunnelTest {
	private static DeleteTunnelAction		action;
	private static ActionTestHelper			helper;
	private static ProtocolSessionManager	protocolsessionmanager;

	@BeforeClass
	public static void init() {
		action = new DeleteTunnelAction();
		action.setModelToUpdate(new ComputerSystem());
		helper = new ActionTestHelper();
		action.setParams(helper.newParamsInterfaceEthernet());
		helper = new ActionTestHelper();
		protocolsessionmanager = helper.getProtocolSessionManager();
	}

	@Test
	public void actionIDTest() {
		Assert.assertEquals("Wrong ActionID", ActionConstants.DELETETUNNEL,
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
		Assert.assertEquals("Not accepted param", "/VM_files/deleteTunnel.vm", action.getTemplate());
	}

	/**
	 * Execute the action
	 * 
	 * @throws IOException
	 */
	@Test
	public void executeActionTest() throws IOException {
		action.setModelToUpdate(new ComputerSystem());

		// Add params
		GRETunnelService greTunnelService = getGRETunnelService();
		action.setParams(greTunnelService);

		try {
			ActionResponse response = action.execute(protocolsessionmanager);
			Assert.assertTrue(response.getActionID()
					.equals(ActionConstants.DELETETUNNEL));
		} catch (ActionException e) {
			e.printStackTrace();
			Assert.fail();
		}

		org.opennaas.extensions.router.model.System computerSystem = (org.opennaas.extensions.router.model.System) action.getModelToUpdate();
		Assert.assertNotNull(computerSystem);
	}

	/**
	 * Get the GRETunnelService
	 * 
	 * @return GRETunnelService
	 * @throws IOException
	 */
	private GRETunnelService getGRETunnelService() throws IOException {
		GRETunnelService greTunnelService = new GRETunnelService();
		greTunnelService.setName("gre.1");
		return greTunnelService;
	}

}
