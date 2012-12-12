package org.opennaas.extensions.router.junos.actionssets.actions.test.vrrp;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import junit.framework.Assert;
import net.i2cat.netconf.rpc.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.helpers.XmlHelper;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.test.ActionTestHelper;
import org.opennaas.extensions.router.junos.actionssets.actions.vrrp.UpdateVRRPVirtualIPAddressAction;
import org.opennaas.extensions.router.model.ComputerSystem;

public class VRRPActionTest {

	Log												log	= LogFactory.getLog(VRRPActionTest.class);
	private static UpdateVRRPVirtualIPAddressAction	action;
	private static IProtocolSessionManager			mockProtocolSessionManager;
	private static IProtocolSession					mockProtocolSession;
	private static Capture<Query>					message;

	// private static ActionTestHelper helper;

	@BeforeClass
	public static void init() {
		try {
			action = new UpdateVRRPVirtualIPAddressAction();
			action.setModelToUpdate(new ComputerSystem());
			action.setParams(ActionTestHelper.newParamsVRRPGroupWithOneEndpoint().getProtocolEndpoint().get(0));
			// helper = new ActionTestHelper();
			prepareMocks();
		} catch (ProtocolException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	private static void prepareMocks() throws ProtocolException {
		mockProtocolSession = EasyMock.createMock(IProtocolSession.class);
		message = new Capture<Query>();
		mockProtocolSession.sendReceive(EasyMock.capture(message));
		EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
			public Object answer() throws Throwable {
				return null;
			}
		});

		mockProtocolSessionManager = EasyMock.createMock(IProtocolSessionManager.class);
		EasyMock.expect(mockProtocolSessionManager.getSessionById(EasyMock.anyObject(String.class), EasyMock.anyBoolean())).andReturn(
				mockProtocolSession);
		EasyMock.expect(mockProtocolSessionManager.obtainSessionByProtocol(EasyMock.anyObject(String.class), EasyMock.anyBoolean())).andReturn(
				mockProtocolSession);
		EasyMock.replay(mockProtocolSession);
		EasyMock.replay(mockProtocolSessionManager);
	}

	@Test
	public void updateVirtualIPAddressTest() {
		try {
			ActionResponse response = action.execute(mockProtocolSessionManager);
			Assert.assertTrue(response.getActionID().equals(ActionConstants.VRRP_UPDATE_IP_ADDRESS));
			Assert.assertEquals(action.getTemplate(), "/VM_files/updateVRRPIPAddress.vm");

			// read expected message into a String
			String expectedMessage = "";
			BufferedReader br = new BufferedReader(
					new InputStreamReader(getClass().getResourceAsStream("/actions/updateVRRPIPAddress.xml")));
			String line;
			while ((line = br.readLine()) != null) {
				expectedMessage += line;
			}
			br.close();

			// XML-format messages and compare both
			String formattedMessage = XmlHelper.formatXML(message.getValue().getConfig());
			String formattedExpectedMessage = XmlHelper.formatXML(expectedMessage);
			log.trace("Formatted produced message:\n" + formattedMessage);
			log.trace("Formatted expected message:\n" + formattedExpectedMessage);
			Assert.assertEquals(formattedExpectedMessage, formattedMessage);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
}
