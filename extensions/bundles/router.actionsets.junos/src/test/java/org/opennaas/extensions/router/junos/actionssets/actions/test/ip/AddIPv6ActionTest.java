package org.opennaas.extensions.router.junos.actionssets.actions.test.ip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.helpers.XmlHelper;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.ip.AddIPv6Action;
import org.opennaas.extensions.router.junos.actionssets.actions.test.ActionTestHelper;
import org.opennaas.extensions.router.model.ComputerSystem;

public class AddIPv6ActionTest {

	Log								log	= LogFactory.getLog(AddIPv6ActionTest.class);
	private static AddIPv6Action	action;
	static ActionTestHelper			helper;
	static ProtocolSessionManager	protocolsessionmanager;

	@BeforeClass
	public static void init() {

		action = new AddIPv6Action();
		action.setModelToUpdate(new ComputerSystem());
		helper = new ActionTestHelper();

		action.setParams(helper.newParamsInterfaceEthernetIPv6("fedc:4123:fa::12", (short) 64));
		protocolsessionmanager = helper.getProtocolSessionManager();
	}

	@Test
	public void TestActionID() {
		Assert.assertEquals("Wrong ActionID", ActionConstants.ADDIPv6, action.getActionID());
	}

	@Test
	public void paramsTest() throws ActionException {
		action.checkParams(action.getParams());
	}

	@Test
	public void templateTest() {
		// this action always have this template as a default
		Assert.assertEquals("Invalid template", "/VM_files/addIPv6.vm", action.getTemplate());
	}

	@Test
	public void velocityTemplateTest() {
		try {
			action.prepareMessage();

			// read expected message into a String
			String expectedMessage = XmlHelper.formatXML(textFileToString("/actions/ip/addIPv6.xml"));
			String actionMessage = XmlHelper.formatXML(action.getVelocityMessage());
			Assert.assertEquals(expectedMessage, actionMessage);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
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
