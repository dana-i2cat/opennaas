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
import org.opennaas.extensions.router.junos.actionssets.actions.ipv4.SetIPv6Action;
import org.opennaas.extensions.router.junos.actionssets.actions.test.ActionTestHelper;
import org.opennaas.extensions.router.model.ComputerSystem;

public class SetIPv6ActionTest {

	Log								log				= LogFactory.getLog(SetIPv6ActionTest.class);
	private static SetIPv6Action	action;
	static ActionTestHelper			helper;
	static ProtocolSessionManager	protocolsessionmanager;

	private static final String		IP_ADDRESS		= "4BF1:0::12";
	private static short			PREFIX_LENGTH	= 32;

	@BeforeClass
	public static void init() {
		action = new SetIPv6Action();
		action.setModelToUpdate(new ComputerSystem());
		helper = new ActionTestHelper();
		action.setParams(helper.newParamsInterfaceEthernetIPv6(IP_ADDRESS, PREFIX_LENGTH));
		// TODO
		// /need to add extra params

		// NetworkPort networkPort = new NetworkPort();
		// velocityEngine.addExtraParam("networkPort", networkPort);
		//
		// IPUtilsHelper ipUtilsHelper = new IPUtilsHelper();
		// velocityEngine.addExtraParam("ipUtilsHelper", ipUtilsHelper);
		helper = new ActionTestHelper();
		protocolsessionmanager = helper.getProtocolSessionManager();
	}

	@Test
	public void testActionID() {
		Assert.assertEquals("Wrong ActionID", ActionConstants.SETIPv6, action.getActionID());
	}

	@Test
	public void paramsTest() throws ActionException {
		action.checkParams(action.getParams());
	}

	@Test
	public void templateTest() {
		Assert.assertEquals("Not accepted param", "/VM_files/configureIPv6.vm", action.getTemplate());
	}

	@Test
	public void velocityTemplateTest() {
		try {
			action.prepareMessage();

			// read expected message into a String
			String expectedMessage = XmlHelper.formatXML(textFileToString("/actions/setIPv6.xml"));
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
