package org.opennaas.extensions.router.junos.actionssets.actions.test.gretunnel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.helpers.XmlHelper;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.gretunnel.CreateTunnelAction;
import org.opennaas.extensions.router.junos.actionssets.actions.test.ActionTestHelper;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.GRETunnelConfiguration;
import org.opennaas.extensions.router.model.GRETunnelEndpoint;
import org.opennaas.extensions.router.model.GRETunnelService;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;

public class CreateTunnelActionTest {
	private static CreateTunnelAction		action;
	private static ActionTestHelper			helper;
	private static ProtocolSessionManager	protocolsessionmanager;

	@BeforeClass
	public static void init() {
		action = new CreateTunnelAction();
		action.setModelToUpdate(new ComputerSystem());
		helper = new ActionTestHelper();
		action.setParams(helper.newParamsInterfaceEthernet());
		helper = new ActionTestHelper();
		protocolsessionmanager = helper.getProtocolSessionManager();
	}

	@Test
	public void actionIDTest() {
		Assert.assertEquals("Wrong ActionID", ActionConstants.CREATETUNNEL,
				action.getActionID());
	}

	@Test
	public void velocityMessageTest() throws ActionException {
		try {
			GRETunnelService greTunnelService = getGRETunnelService();
			action.setParams(greTunnelService);

			action.prepareMessage();
			Assert.assertEquals("Wrong Velocity Template", "/VM_files/createTunnel.vm", action.getTemplate());

			String expectedMessage = XmlHelper.formatXML(textFileToString("/actions/greTunnelv4.xml"));
			String actionMessage = XmlHelper.formatXML(action.getVelocityMessage());
			Assert.assertEquals(expectedMessage, actionMessage);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

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
				.equals(ActionConstants.CREATETUNNEL));

		org.opennaas.extensions.router.model.System computerSystem = (org.opennaas.extensions.router.model.System) action.getModelToUpdate();
		Assert.assertNotNull(computerSystem);
	}

	private String textFileToString(String fileLocation) throws IOException {
		String fileString = "";
		BufferedReader br = new BufferedReader(
				new InputStreamReader(getClass().getResourceAsStream(fileLocation)));
		String line;
		while ((line = br.readLine()) != null) {
			fileString += line;
		}
		br.close();
		return fileString;
	}

	/**
	 * Get the GRETunnelService
	 * 
	 * @return GRETunnelService
	 * @throws IOException
	 */
	private GRETunnelService getGRETunnelService() {

		GRETunnelService greService = new GRETunnelService();
		greService.setElementName("");
		greService.setName("gr-0/1/1.2");

		GRETunnelConfiguration greConfig = new GRETunnelConfiguration();
		greConfig.setSourceAddress("147.56.89.62");
		greConfig.setDestinationAddress("193.45.23.1");

		GRETunnelEndpoint gE = new GRETunnelEndpoint();
		gE.setIPv4Address("192.168.32.1");
		gE.setSubnetMask("255.255.255.0");
		gE.setProtocolIFType(ProtocolIFType.IPV4);

		greService.setGRETunnelConfiguration(greConfig);
		greService.addProtocolEndpoint(gE);

		return greService;
	}

}
