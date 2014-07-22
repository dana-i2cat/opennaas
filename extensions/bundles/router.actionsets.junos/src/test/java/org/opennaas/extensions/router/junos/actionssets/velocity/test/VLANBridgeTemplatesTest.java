package org.opennaas.extensions.router.junos.actionssets.velocity.test;

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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.opennaas.core.resources.helpers.XmlHelper;
import org.opennaas.extensions.router.model.BridgeDomain;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.NetworkPortVLANSettingData;
import org.xml.sax.SAXException;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class VLANBridgeTemplatesTest extends VelocityTemplatesTest {

	private final static String	VLAN_BRIDGE_NAME	= "vlan100";
	private final static String	VLAN_BRIDGE_DESC	= "VLAN 100";
	private final static int	VLAN_BRIDGE_VLAN_ID	= 100;

	private final static String	IFACE_NAME			= "fe-3/3/1";
	private final static int	IFACE_PORT			= 100;

	private final static String	IFACE				= IFACE_NAME + "." + IFACE_PORT;
	private static final String	POR_MODE_TRUNK		= "trunk";

	@Test
	public void createBridgeDomainTest() throws IOException, SAXException, TransformerException, ParserConfigurationException {

		template = "/VM_files/vlanBridge/bridgeDomainCreate.vm";

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("elementName", "");

		BridgeDomain brDomain = new BridgeDomain();

		brDomain.setElementName(VLAN_BRIDGE_NAME);

		String expectedXML = IOUtils.toString(this.getClass().getResourceAsStream("/actions/vlanBridge/bridgeDomainCreate.xml"));
		String message = callVelocity(template, brDomain, extraParams);

		Assert.assertTrue("Generated template does not match expected XML.", XmlHelper.compareXMLStrings(expectedXML, message));

	}

	@Test
	public void createBridgeDomainFullTest() throws IOException, SAXException, TransformerException, ParserConfigurationException {

		template = "/VM_files/vlanBridge/bridgeDomainCreate.vm";

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("elementName", "");

		BridgeDomain brDomain = new BridgeDomain();

		brDomain.setElementName(VLAN_BRIDGE_NAME);
		brDomain.setDescription(VLAN_BRIDGE_DESC);
		brDomain.setVlanId(VLAN_BRIDGE_VLAN_ID);
		brDomain.addNetworkPort(IFACE);

		String expectedXML = IOUtils.toString(this.getClass().getResourceAsStream("/actions/vlanBridge/bridgeDomainCreateFull.xml"));
		String message = callVelocity(template, brDomain, extraParams);

		Assert.assertTrue("Generated template does not match expected XML.", XmlHelper.compareXMLStrings(expectedXML, message));

	}

	@Test
	public void deleteBridgeDomainTest() throws IOException, SAXException, TransformerException, ParserConfigurationException {

		template = "/VM_files/vlanBridge/bridgeDomainDelete.vm";

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("elementName", "");

		String expectedXML = IOUtils.toString(this.getClass().getResourceAsStream("/actions/vlanBridge/bridgeDomainDelete.xml"));
		String message = callVelocity(template, VLAN_BRIDGE_NAME, extraParams);

		Assert.assertTrue("Generated template does not match expected XML.", XmlHelper.compareXMLStrings(expectedXML, message));

	}

	@Test
	public void setInterfaceVlanOptionsTest() throws IOException, SAXException, TransformerException, ParserConfigurationException {

		template = "/VM_files/vlanBridge/ifaceVlanOptsSet.vm";

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("elementName", "");

		NetworkPort netPort = new NetworkPort();
		netPort.setName(IFACE_NAME);
		netPort.setPortNumber(IFACE_PORT);

		NetworkPortVLANSettingData ifaceOpts = new NetworkPortVLANSettingData();
		ifaceOpts.setPortMode(POR_MODE_TRUNK);
		ifaceOpts.setNativeVlanId(VLAN_BRIDGE_VLAN_ID);
		netPort.addElementSettingData(ifaceOpts);

		String expectedXML = IOUtils.toString(this.getClass().getResourceAsStream("/actions/vlanBridge/ifaceVlanOptsSet.xml"));
		String message = callVelocity(template, netPort, extraParams);

		log.info(XmlHelper.formatXML(message));

		Assert.assertTrue("Generated template does not match expected XML.", XmlHelper.compareXMLStrings(expectedXML, message));

	}

	@Test
	public void setEmptyInterfaceVlanOptionsTest() throws IOException, SAXException, TransformerException, ParserConfigurationException {

		template = "/VM_files/vlanBridge/ifaceVlanOptsSet.vm";

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("elementName", "");

		NetworkPort netPort = new NetworkPort();
		netPort.setName(IFACE_NAME);
		netPort.setPortNumber(IFACE_PORT);

		NetworkPortVLANSettingData ifaceOpts = new NetworkPortVLANSettingData();
		netPort.addElementSettingData(ifaceOpts);

		String expectedXML = IOUtils.toString(this.getClass().getResourceAsStream("/actions/vlanBridge/ifaceVlanOptsSetEmpty.xml"));
		String message = callVelocity(template, netPort, extraParams);

		log.info(XmlHelper.formatXML(message));

		Assert.assertTrue("Generated template does not match expected XML.", XmlHelper.compareXMLStrings(expectedXML, message));

	}

	@Test
	public void unsetInterfaceVlanOptionsTest() throws IOException, SAXException, TransformerException, ParserConfigurationException {

		template = "/VM_files/vlanBridge/ifaceVlanOptsUnset.vm";

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("elementName", "");

		NetworkPort netPort = new NetworkPort();
		netPort.setName(IFACE_NAME);
		netPort.setPortNumber(IFACE_PORT);

		String expectedXML = IOUtils.toString(this.getClass().getResourceAsStream("/actions/vlanBridge/ifaceVlanOptsUnset.xml"));
		String message = callVelocity(template, netPort, extraParams);

		log.info(XmlHelper.formatXML(message));

		Assert.assertTrue("Generated template does not match expected XML.", XmlHelper.compareXMLStrings(expectedXML, message));

	}
}
