package org.opennaas.extensions.router.junos.actionssets.actions.test.vrrp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import junit.framework.Assert;
import net.i2cat.netconf.rpc.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.helpers.XmlHelper;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.JunosAction;
import org.opennaas.extensions.router.junos.actionssets.actions.test.ActionTestHelper;
import org.opennaas.extensions.router.junos.actionssets.actions.vrrp.ConfigureVRRPAction;
import org.opennaas.extensions.router.junos.actionssets.actions.vrrp.UnconfigureVRRPAction;
import org.opennaas.extensions.router.junos.actionssets.actions.vrrp.UpdateVRRPPriorityAction;
import org.opennaas.extensions.router.junos.actionssets.actions.vrrp.UpdateVRRPVirtualIPAddressAction;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.xml.sax.SAXException;

/**
 * @author Julio Carlos Barrera
 */
public class VRRPActionTest {

	private Log						log	= LogFactory.getLog(VRRPActionTest.class);
	private JunosAction				action;
	private IProtocolSessionManager	mockProtocolSessionManager;
	private IProtocolSession		mockProtocolSession;
	private Capture<Query>			message;

	@Before
	public void init() {
		try {
			prepareMocks();
		} catch (ProtocolException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	private void prepareMocks() throws ProtocolException {
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
	public void configureVRRPTest() {
		log.info("Testing ConfigureVRRP");
		action = new ConfigureVRRPAction();
		action.setModelToUpdate(new ComputerSystem());
		action.setParams(ActionTestHelper.newParamsVRRPGroupWithOneEndpoint().getProtocolEndpoint().get(0));
		try {
			ActionResponse response = action.execute(mockProtocolSessionManager);
			Assert.assertTrue(response.getActionID().equals(ActionConstants.VRRP_CONFIGURE));
			Assert.assertEquals(action.getTemplate(), "/VM_files/configureVRRP.vm");

			// read expected message into a String
			String expectedMessage = textFileToString("/actions/configureVRRP.xml");

			assertEqualXMLConfigurations(expectedMessage, message.getValue().getConfig());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void unconfigureVRRPTest() {
		log.info("Testing UnconfigureVRRP");
		action = new UnconfigureVRRPAction();
		action.setModelToUpdate(new ComputerSystem());
		action.setParams(ActionTestHelper.newParamsVRRPGroupWithOneEndpoint().getProtocolEndpoint().get(0));
		try {
			ActionResponse response = action.execute(mockProtocolSessionManager);
			Assert.assertTrue(response.getActionID().equals(ActionConstants.VRRP_UNCONFIGURE));
			Assert.assertEquals(action.getTemplate(), "/VM_files/unconfigureVRRP.vm");

			// read expected message into a String
			String expectedMessage = textFileToString("/actions/unconfigureVRRP.xml");

			assertEqualXMLConfigurations(expectedMessage, message.getValue().getConfig());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void updateVirtualIPAddressTest() {
		log.info("Testing UpdateVirtualIPAddress");
		action = new UpdateVRRPVirtualIPAddressAction();
		action.setModelToUpdate(new ComputerSystem());
		action.setParams(ActionTestHelper.newParamsVRRPGroupWithOneEndpoint().getProtocolEndpoint().get(0));
		try {
			ActionResponse response = action.execute(mockProtocolSessionManager);
			Assert.assertTrue(response.getActionID().equals(ActionConstants.VRRP_UPDATE_IP_ADDRESS));
			Assert.assertEquals(action.getTemplate(), "/VM_files/updateVRRPIPAddress.vm");

			// read expected message into a String
			String expectedMessage = textFileToString("/actions/updateVRRPIPAddress.xml");

			assertEqualXMLConfigurations(expectedMessage, message.getValue().getConfig());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void updatePriorityTest() {
		log.info("Testing UpdatePriority");
		action = new UpdateVRRPPriorityAction();
		action.setModelToUpdate(new ComputerSystem());
		action.setParams(ActionTestHelper.newParamsVRRPGroupWithOneEndpoint().getProtocolEndpoint().get(0));
		try {
			ActionResponse response = action.execute(mockProtocolSessionManager);
			Assert.assertTrue(response.getActionID().equals(ActionConstants.VRRP_UPDATE_PRIORITY));
			Assert.assertEquals(action.getTemplate(), "/VM_files/updateVRRPPriority.vm");

			// read expected message into a String
			String expectedMessage = textFileToString("/actions/updateVRRPPriority.xml");

			assertEqualXMLConfigurations(expectedMessage, message.getValue().getConfig());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	private void assertEqualXMLConfigurations(String expectedXMLConfiguration, String generatedXMLConfiguration) throws IOException, SAXException,
			TransformerException, ParserConfigurationException {
		// XML-format messages and compare both
		log.trace("Produced XML configuration:\n" + generatedXMLConfiguration);
		log.trace("Expected XML configuration:\n" + expectedXMLConfiguration);
		Assert.assertTrue("Generated configuration XML and expected configuration XML must be equal",
				XmlHelper.compareXMLStrings(generatedXMLConfiguration, expectedXMLConfiguration));
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

}
