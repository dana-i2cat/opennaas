package org.opennaas.extensions.router.junos.actionssets.actions.test.bgp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.helpers.XmlHelper;
import org.opennaas.extensions.router.capability.bgp.BGPModelFactory;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.bgp.ConfigureBGPAction;
import org.opennaas.extensions.router.junos.actionssets.actions.test.ActionTestHelper;
import org.opennaas.extensions.router.model.ComputerSystem;

public class ConfigureBGPActionTest {

	private static ConfigureBGPAction		action;
	private static ActionTestHelper			helper;
	private static ProtocolSessionManager	protocolsessionmanager;
	private static final String				BGP_v4_PROPERTIES_FILE	= "/actions/bgp_v4.properties";
	private static final String				BGP_v6_PROPERTIES_FILE	= "/actions/bgp_v6.properties";

	@BeforeClass
	public static void init() throws IOException {
		action = new ConfigureBGPAction();
		action.setModelToUpdate(new ComputerSystem());
		helper = new ActionTestHelper();
		protocolsessionmanager = helper.getProtocolSessionManager();
	}

	@Test
	public void actionIDTest() {
		Assert.assertEquals("Wrong ActionID", ActionConstants.CONFIGURE_BGP,
				action.getActionID());
	}

	@Test
	public void velocityMessageTestIpv4() throws ActionException {
		try {

			ComputerSystem bgpConfig = getBGPConfigurationIPv4();

			Assert.assertTrue(action.checkParams(bgpConfig));

			action.setParams(bgpConfig);
			action.prepareMessage();
			String configBGPMessage = action.prepareConfigBGPMessage();
			String configPolicyOptionsMessage = action.prepareConfigPolicyOptionsMessage();

			String expectedBGPMessage = XmlHelper.formatXML(textFileToString("/actions/bgpConfig_bgp_v4.xml"));
			Assert.assertEquals(expectedBGPMessage, XmlHelper.formatXML(configBGPMessage));

			String expectedPolicyOptionsMessage = XmlHelper.formatXML(textFileToString("/actions/bgpConfig_policies_v4.xml"));
			Assert.assertEquals(expectedPolicyOptionsMessage, XmlHelper.formatXML(configPolicyOptionsMessage));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void velocityMessageTestIpv6() throws ActionException {
		try {

			ComputerSystem bgpConfig = getBGPConfigurationIPv6();
			Assert.assertTrue(action.checkParams(bgpConfig));

			action.setParams(bgpConfig);
			action.prepareMessage();
			String configBGPMessage = action.prepareConfigBGPMessage();
			String configPolicyOptionsMessage = action.prepareConfigPolicyOptionsMessage();

			String expectedBGPMessage = XmlHelper.formatXML(textFileToString("/actions/bgpConfig_bgp_v6.xml"));
			Assert.assertEquals(expectedBGPMessage, XmlHelper.formatXML(configBGPMessage));

			String expectedPolicyOptionsMessage = XmlHelper.formatXML(textFileToString("/actions/bgpConfig_policies_v6.xml"));
			Assert.assertEquals(expectedPolicyOptionsMessage, XmlHelper.formatXML(configPolicyOptionsMessage));

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

	private ComputerSystem getBGPConfigurationIPv4() throws IOException {
		BGPModelFactory factory = new BGPModelFactory(getClass().getResourceAsStream(BGP_v4_PROPERTIES_FILE));
		return factory.createRouterWithBGP();
	}

	private ComputerSystem getBGPConfigurationIPv6() throws IOException {
		BGPModelFactory factory = new BGPModelFactory(getClass().getResourceAsStream(BGP_v6_PROPERTIES_FILE));
		return factory.createRouterWithBGP();
	}

}
