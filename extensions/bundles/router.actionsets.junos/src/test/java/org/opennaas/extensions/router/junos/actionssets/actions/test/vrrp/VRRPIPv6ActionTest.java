package org.opennaas.extensions.router.junos.actionssets.actions.test.vrrp;

/*
 * #%L
 * OpenNaaS :: Router :: Junos ActionSet
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.helpers.XmlHelper;
import org.opennaas.extensions.router.junos.actionssets.actions.test.ActionTestHelper;
import org.opennaas.extensions.router.junos.actionssets.actions.vrrp.ConfigureVRRPAction;
import org.opennaas.extensions.router.junos.actionssets.actions.vrrp.UnconfigureVRRPAction;
import org.opennaas.extensions.router.junos.actionssets.actions.vrrp.UpdateVRRPPriorityAction;
import org.opennaas.extensions.router.junos.actionssets.actions.vrrp.UpdateVRRPVirtualIPAddressAction;
import org.opennaas.extensions.router.junos.actionssets.actions.vrrp.UpdateVRRPVirtualLinkAddressAction;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.VRRPGroup;
import org.opennaas.extensions.router.model.VRRPProtocolEndpoint;
import org.xml.sax.SAXException;

public class VRRPIPv6ActionTest {

	Log								log	= LogFactory.getLog(VRRPIPv6ActionTest.class);

	static ActionTestHelper			helper;
	static ProtocolSessionManager	protocolsessionmanager;

	@BeforeClass()
	public static void setUp() {
		helper = new ActionTestHelper();
		protocolsessionmanager = helper.getProtocolSessionManager();
	}

	@Test
	public void configureVRRPIPv6Test() throws ActionException, SAXException, IOException, TransformerException,
			ParserConfigurationException {
		ConfigureVRRPAction configureAction = new ConfigureVRRPAction();
		configureAction.setModelToUpdate(new ComputerSystem());
		configureAction.setParams(helper.newParamsVRRPGroupWithThreeEndpointsIPv6().getProtocolEndpoint().get(0));

		Assert.assertTrue("Invalid params for ConfigureVRRP action : ", configureAction.checkParams(configureAction.getParams()));
		configureAction.prepareMessage();

		Assert.assertEquals("Invalid template for configuring VRRP with IPv6", "/VM_files/configureVRRPIPv6.vm", configureAction.getTemplate());

		String expectedMessage = XmlHelper.formatXML(textFileToString("/actions/configureVRRPIPv6.xml"));
		String actionMessage = XmlHelper.formatXML(configureAction.getVelocityMessage());

		Assert.assertEquals(expectedMessage, actionMessage);
	}

	// checkParams should fail
	@Test(expected = ActionException.class)
	public void configureVRRPIPv6TestWithoutVirtualLink() throws ActionException, SAXException, IOException, TransformerException,
			ParserConfigurationException {
		ConfigureVRRPAction configureAction = new ConfigureVRRPAction();
		configureAction.setModelToUpdate(new ComputerSystem());
		VRRPProtocolEndpoint pE = (VRRPProtocolEndpoint) helper.newParamsVRRPGroupWithTwoEndpointIPv6().getProtocolEndpoint().get(0);

		((VRRPGroup) pE.getService()).setVirtualLinkAddress(null);

		configureAction.setParams(pE);

		configureAction.checkParams(configureAction.getParams());

	}

	// checkParams should fail
	@Test(expected = ActionException.class)
	public void configureVRRPIPv6TestWithOneLink() throws ActionException, SAXException, IOException, TransformerException,
			ParserConfigurationException {
		ConfigureVRRPAction configureAction = new ConfigureVRRPAction();
		configureAction.setModelToUpdate(new ComputerSystem());
		VRRPProtocolEndpoint pE = (VRRPProtocolEndpoint) helper.newParamsVRRPGroupWithOneEndpointIPv6().getProtocolEndpoint().get(0);

		configureAction.setParams(pE);

		configureAction.checkParams(configureAction.getParams());

	}

	@Test
	public void unconfigureVRRPIPv6Test() throws ActionException, SAXException, IOException, TransformerException, ParserConfigurationException {
		UnconfigureVRRPAction unconfigureAction = new UnconfigureVRRPAction();
		unconfigureAction.setModelToUpdate(new ComputerSystem());
		unconfigureAction.setParams(helper.newParamsVRRPGroupWithOneEndpointIPv6().getProtocolEndpoint().get(0));

		Assert.assertTrue("Invalid params for UnconfigureVRRP action : ", unconfigureAction.checkParams(unconfigureAction.getParams()));
		unconfigureAction.prepareMessage();

		Assert.assertEquals("Invalid template for configuring VRRP with IPv6", "/VM_files/unconfigureVRRPIPv6.vm", unconfigureAction.getTemplate());

		String expectedMessage = XmlHelper.formatXML(textFileToString("/actions/unconfigureVRRPIPv6.xml"));
		String actionMessage = XmlHelper.formatXML(unconfigureAction.getVelocityMessage());

		Assert.assertEquals(expectedMessage, actionMessage);
	}

	@Test
	public void updateVRRPPriorityIPv6Test() throws ActionException, SAXException, IOException, TransformerException, ParserConfigurationException {
		UpdateVRRPPriorityAction updateAction = new UpdateVRRPPriorityAction();
		updateAction.setModelToUpdate(new ComputerSystem());
		updateAction.setParams(helper.newParamsVRRPGroupWithOneEndpointIPv6().getProtocolEndpoint().get(0));

		Assert.assertTrue("Invalid params for UnconfigureVRRP action : ", updateAction.checkParams(updateAction.getParams()));
		updateAction.prepareMessage();

		Assert.assertEquals("Invalid template for configuring VRRP with IPv6", "/VM_files/updateVRRPPriorityIPv6.vm", updateAction.getTemplate());

		String expectedMessage = XmlHelper.formatXML(textFileToString("/actions/updateVRRPPriorityIPv6.xml"));
		String actionMessage = XmlHelper.formatXML(updateAction.getVelocityMessage());

		Assert.assertEquals(expectedMessage, actionMessage);
	}

	@Test
	public void updateVRRPVirtualIPAddressIPv6Test() throws ActionException, SAXException, IOException, TransformerException,
			ParserConfigurationException {
		UpdateVRRPVirtualIPAddressAction updateAction = new UpdateVRRPVirtualIPAddressAction();
		updateAction.setModelToUpdate(new ComputerSystem());
		updateAction.setParams(helper.newParamsVRRPGroupWithOneEndpointIPv6().getProtocolEndpoint().get(0));

		Assert.assertTrue("Invalid params for UpdateVRRPVirtualIPAddress action : ", updateAction.checkParams(updateAction.getParams()));
		updateAction.prepareMessage();

		Assert.assertEquals("Invalid template for configuring VRRP with IPv6", "/VM_files/updateVRRPIPAddressIPv6.vm", updateAction.getTemplate());

		String expectedMessage = XmlHelper.formatXML(textFileToString("/actions/updateVRRPIPAddressIPv6.xml"));
		String actionMessage = XmlHelper.formatXML(updateAction.getVelocityMessage());

		Assert.assertEquals(expectedMessage, actionMessage);
	}

	@Test
	public void updateVRRPVirtualLinkAddressTest() throws ActionException, SAXException, IOException, TransformerException,
			ParserConfigurationException {
		UpdateVRRPVirtualLinkAddressAction updateAction = new UpdateVRRPVirtualLinkAddressAction();
		updateAction.setModelToUpdate(new ComputerSystem());
		updateAction.setParams(helper.newParamsVRRPGroupWithOneEndpointIPv6());

		Assert.assertTrue("Invalid params for UpdateVRRPVirtualIPAddress action : ", updateAction.checkParams(updateAction.getParams()));
		Assert.assertEquals("Invalid template for configuring VRRP with IPv6", "/VM_files/updateVRRPVirtualLinkAddress.vm",
				updateAction.getTemplate());

		updateAction.prepareMessage();

		String expectedMessage = XmlHelper.formatXML(textFileToString("/actions/updateVRRPVirtualLinkAddress.xml"));
		String actionMessage = XmlHelper.formatXML(updateAction.getVelocityMessage());

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
