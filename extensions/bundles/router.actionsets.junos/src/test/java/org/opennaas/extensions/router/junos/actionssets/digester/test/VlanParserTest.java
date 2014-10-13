package org.opennaas.extensions.router.junos.actionssets.digester.test;

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

import java.io.ByteArrayInputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.opennaas.extensions.router.junos.commandsets.digester.VLANParser;
import org.opennaas.extensions.router.model.BridgeDomain;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.System;
import org.opennaas.extensions.router.model.SystemSpecificCollection;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class VlanParserTest {

	private static final String	VLAN_CONFIG_URL			= "/parsers/vlans.xml";

	private final static String	BRIDGE_NAME_12			= "vlan12";
	private final static String	BRIDGE_NAME_20			= "vlan20";
	private static final String	BRIDGE_DESCRIPTION_12	= "VLANBridge with VLAN 12";
	private static final String	BRIDGE_DESCRIPTION_20	= "VLANBridge with VLAN 20";
	private static final int	BRIDGE_VLANID_12		= 12;
	private static final int	BRIDGE_VLANID_20		= 20;
	private static final String	BRIDGE_INTERFACE_12		= "fe-0/4/1.0";
	private static final String	BRIDGE_INTERFACE_20_1	= "ge-1/1/1.0";
	private static final String	BRIDGE_INTERFACE_20_2	= "fe-1/2/1.0";

	@Test
	public void vlanParserTest() throws Exception {

		String message = IOUtils.toString(this.getClass().getResourceAsStream(VLAN_CONFIG_URL));

		System model = new ComputerSystem();

		VLANParser parser = new VLANParser(model);
		parser.init();
		parser.configurableParse(new ByteArrayInputStream(message.getBytes()));

		System updatedModel = parser.getModel();

		Assert.assertNotNull("Model should not be null", updatedModel);
		List<SystemSpecificCollection> hostedCollections = updatedModel.getHostedCollection();

		Assert.assertNotNull("Model should contain hosted collections.", hostedCollections);
		Assert.assertEquals("Model should contain two hosted collections.", 2, hostedCollections.size());

		Assert.assertTrue("Hosted collection shold be instance of BridgeDomain", hostedCollections.get(0) instanceof BridgeDomain);
		Assert.assertTrue("Hosted collection shold be instance of BridgeDomain", hostedCollections.get(1) instanceof BridgeDomain);

		BridgeDomain brDomain1 = (BridgeDomain) hostedCollections.get(0);
		BridgeDomain brDomain2 = (BridgeDomain) hostedCollections.get(1);

		Assert.assertFalse("Both bridge domains should be different.", brDomain1.equals(brDomain2));
		Assert.assertFalse("Both bridge domains names should be different.", brDomain1.getElementName().equals(brDomain2.getElementName()));

		for (SystemSpecificCollection domain : hostedCollections) {
			BridgeDomain brDomain = (BridgeDomain) domain;

			if (brDomain.getElementName().equals(BRIDGE_NAME_12)) {

				Assert.assertEquals("Description does not match expected one for vlanBridge " + BRIDGE_NAME_12, BRIDGE_DESCRIPTION_12,
						brDomain.getDescription());
				Assert.assertEquals("Bridge Domain " + BRIDGE_NAME_12 + " should contain only 1 network port.", 1, brDomain.getNetworkPorts().size());
				Assert.assertTrue("Bridge Domain " + BRIDGE_NAME_12 + " should contain interface " + BRIDGE_INTERFACE_12, brDomain
						.getNetworkPorts().contains(BRIDGE_INTERFACE_12));
				Assert.assertTrue("Bridge Domain " + BRIDGE_NAME_12 + " should contain vlanid " + BRIDGE_VLANID_12,
						BRIDGE_VLANID_12 == brDomain.getVlanId());

			} else if (brDomain.getElementName().equals(BRIDGE_NAME_20)) {

				Assert.assertEquals("Description does not match expected one for vlanBridge " + BRIDGE_NAME_20, BRIDGE_DESCRIPTION_20,
						brDomain.getDescription());
				Assert.assertEquals("Bridge Domain " + BRIDGE_NAME_20 + " should contain 2 network ports.", 2, brDomain.getNetworkPorts().size());
				Assert.assertTrue("Bridge Domain " + BRIDGE_NAME_20 + " should contain interface " + BRIDGE_INTERFACE_20_1, brDomain
						.getNetworkPorts().contains(BRIDGE_INTERFACE_20_1));
				Assert.assertTrue("Bridge Domain " + BRIDGE_NAME_20 + " should contain interface " + BRIDGE_INTERFACE_20_2, brDomain
						.getNetworkPorts().contains(BRIDGE_INTERFACE_20_2));
				Assert.assertTrue("Bridge Domain " + BRIDGE_NAME_20 + " should contain vlanid " + BRIDGE_VLANID_20,
						BRIDGE_VLANID_20 == brDomain.getVlanId());

			} else
				Assert.fail("Unexpected name for parsed Bridge Domain: " + brDomain.getElementName());

		}

	}
}
