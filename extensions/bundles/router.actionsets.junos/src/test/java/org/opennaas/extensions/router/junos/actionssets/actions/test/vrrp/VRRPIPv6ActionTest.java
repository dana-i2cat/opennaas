package org.opennaas.extensions.router.junos.actionssets.actions.test.vrrp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.helpers.XmlHelper;
import org.opennaas.extensions.router.junos.actionssets.actions.test.ActionTestHelper;
import org.opennaas.extensions.router.junos.actionssets.actions.vrrp.ConfigureVRRPAction;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.xml.sax.SAXException;

public class VRRPIPv6ActionTest {

	Log								log	= LogFactory.getLog(VRRPIPv6ActionTest.class);

	ConfigureVRRPAction				configureAction;
	static ActionTestHelper			helper;
	static ProtocolSessionManager	protocolsessionmanager;

	@BeforeClass()
	public static void setUp() {
		helper = new ActionTestHelper();
		protocolsessionmanager = helper.getProtocolSessionManager();
	}

	@Test
	public void configureVRRPIPv6Test() throws ActionException, SAXException, IOException, TransformerException, ParserConfigurationException {
		configureAction = new ConfigureVRRPAction();
		configureAction.setModelToUpdate(new ComputerSystem());
		configureAction.setParams(helper.newParamsVRRPGroupWithOneEndpointIPv6().getProtocolEndpoint().get(0));

		Assert.assertTrue("Invalid params for ConfigureVRRP action : ", configureAction.checkParams(configureAction.getParams()));
		configureAction.prepareMessage();

		Assert.assertEquals("Invalid template for configuring VRRP with IPv6", "/VM_files/configureVRRPIPv6.vm", configureAction.getTemplate());

		String expectedMessage = XmlHelper.formatXML(textFileToString("/actions/configureVRRPIPv6.xml"));
		String actionMessage = XmlHelper.formatXML(configureAction.getVelocityMessage());

		Assert.assertEquals(expectedMessage, actionMessage);
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
