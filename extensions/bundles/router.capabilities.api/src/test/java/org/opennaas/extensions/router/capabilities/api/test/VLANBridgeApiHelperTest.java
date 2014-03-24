package org.opennaas.extensions.router.capabilities.api.test;

/*
 * #%L
 * OpenNaaS :: Router :: Capabilities :: API
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.opennaas.extensions.router.capabilities.api.helper.VLANBridgeApiHelper;
import org.opennaas.extensions.router.capabilities.api.model.vlanbridge.BridgeDomain;
import org.opennaas.extensions.router.capabilities.api.model.vlanbridge.BridgeDomains;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class VLANBridgeApiHelperTest {

	private final static String	BD_NAME_100	= "vlan.100";
	private static final String	BD_DSC_100	= "VLAN Bridge with vlan 100";
	private static final int	BD_VLAN_100	= 100;

	private final static String	BD_NAME_200	= "vlan.200";

	private static final String	IFACE_1		= "fe-0/1/1.1";
	private static final String	IFACE_2		= "fe-0/3/2.2";					;

	@Test
	public void buildApiBridgeDomainsTest() {

		List<org.opennaas.extensions.router.model.BridgeDomain> modelBridgeDomains = generateSampleModelBridgeDomains();

		BridgeDomains apiBridgeDomains = VLANBridgeApiHelper.buildApiBridgeDomains(modelBridgeDomains);

		Assert.assertNotNull("Parsed Bridge Domains should not be null", apiBridgeDomains);
		Assert.assertNotNull("Parsed Bridge Domains should not be null", apiBridgeDomains.getDomainNames());
		Assert.assertEquals("BridgeDomains list should contain two elements.", 2, apiBridgeDomains.getDomainNames().size());

		Assert.assertTrue("BridgeDomains list should contain domain name " + BD_NAME_100, apiBridgeDomains.getDomainNames().contains(BD_NAME_100));
		Assert.assertTrue("BridgeDomains list should contain domain name " + BD_NAME_200, apiBridgeDomains.getDomainNames().contains(BD_NAME_200));

	}

	@Test
	public void buildApiBridgeDomain() {

		Set<String> bdIfaces = new HashSet<String>();
		bdIfaces.add(IFACE_1);
		bdIfaces.add(IFACE_2);

		org.opennaas.extensions.router.model.BridgeDomain modelBrDomain = generateSampleModelBridgeDomain(BD_NAME_100, BD_VLAN_100, BD_DSC_100,
				bdIfaces);

		BridgeDomain apiBrDomain = VLANBridgeApiHelper.buildApiBridgeDomain(modelBrDomain);

		Assert.assertNotNull("Generated BridgeDomain should not be null", apiBrDomain);
		Assert.assertEquals("Generated BridgeDomain should have following name : " + BD_NAME_100, BD_NAME_100, apiBrDomain.getDomainName());
		Assert.assertEquals("Generated BridgeDomain should have following description : " + BD_DSC_100, BD_DSC_100, apiBrDomain.getDescription());

		Assert.assertTrue("Generated BridgeDomain should have following vlanId : " + BD_VLAN_100, BD_VLAN_100 == apiBrDomain.getVlanid());

		Assert.assertNotNull("Generated BridgeDomain should contain two interfaces.", apiBrDomain.getInterfacesNames());

		Assert.assertEquals("Generated BridgeDomain should contain two interfaces.", 2, apiBrDomain.getInterfacesNames().size());

		Assert.assertTrue("Generated BridgeDomain should contain interface " + IFACE_1, apiBrDomain.getInterfacesNames().contains(IFACE_1));
		Assert.assertTrue("Generated BridgeDomain should contain interface " + IFACE_2, apiBrDomain.getInterfacesNames().contains(IFACE_2));

	}

	private List<org.opennaas.extensions.router.model.BridgeDomain> generateSampleModelBridgeDomains() {

		List<org.opennaas.extensions.router.model.BridgeDomain> modelBridgeDomains = new ArrayList<org.opennaas.extensions.router.model.BridgeDomain>();

		modelBridgeDomains.add(generateSampleModelBridgeDomain(BD_NAME_100, BD_VLAN_100, BD_DSC_100, new HashSet<String>()));
		modelBridgeDomains.add(generateSampleModelBridgeDomain(BD_NAME_200, BD_VLAN_100, BD_DSC_100, new HashSet<String>()));

		return modelBridgeDomains;
	}

	private org.opennaas.extensions.router.model.BridgeDomain generateSampleModelBridgeDomain(String domainName, int vlanId, String description,
			Set<String> ifaces) {

		org.opennaas.extensions.router.model.BridgeDomain modelBrDomain = new org.opennaas.extensions.router.model.BridgeDomain();
		modelBrDomain.setElementName(domainName);

		modelBrDomain.setDescription(description);
		modelBrDomain.setVlanId(vlanId);
		modelBrDomain.setNetworkPorts(ifaces);

		return modelBrDomain;
	}
}
