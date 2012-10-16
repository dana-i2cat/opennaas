package org.opennaas.extensions.router.junos.actionssets.actions.test.gretunnel;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.gretunnel.DeleteTunnelAction;
import org.opennaas.extensions.router.junos.actionssets.actions.test.ActionTestHelper;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.GRETunnelService;

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
	 * @throws ActionException
	 */
	@Test
	public void executeActionTest() throws IOException, ActionException {
		action.setModelToUpdate(new ComputerSystem());

		// Add params
		GRETunnelService greTunnelService = getGRETunnelService();
		action.setParams(greTunnelService);

		ActionResponse response = action.execute(protocolsessionmanager);
		Assert.assertTrue(response.getActionID()
				.equals(ActionConstants.DELETETUNNEL));

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
		greTunnelService.setName("gr-0/1/3.2");
		return greTunnelService;
	}

}
