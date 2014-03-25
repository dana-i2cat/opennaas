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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.opennaas.extensions.router.capabilities.api.helper.VLANBridgeApiHelper;
import org.opennaas.extensions.router.capabilities.api.model.vlanbridge.BridgeDomain;
import org.opennaas.extensions.router.capabilities.api.model.vlanbridge.BridgeDomains;
import org.opennaas.extensions.router.capabilities.api.model.vlanbridge.InterfaceVLANOptions;
import org.opennaas.extensions.router.model.NetworkPortVLANSettingData;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class VLANBridgeApiHelperTest {

	private final static String	BD_NAME_100		= "vlan.100";
	private static final String	BD_DSC_100		= "VLAN Bridge with vlan 100";
	private static final int	BD_VLAN_100		= 100;

	private final static String	BD_NAME_200		= "vlan.200";

	private static final String	IFACE_1			= "fe-0/1/1.1";
	private static final String	IFACE_2			= "fe-0/3/2.2";

	private static final String	PORT_MODE_TRUNK	= "trunk";

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

	/**
	 * This test and the buildApiIfaceVlanOptionsWithNativeVlanIdTest one are used to distinguish between the default value of the native-vlan-id and
	 * the one set to 0.
	 */
	@Test
	public void buildApiIfaceVlanOptionsWithoutInfoTest() {

		NetworkPortVLANSettingData settingData = new NetworkPortVLANSettingData();
		InterfaceVLANOptions apiVlanOpts = VLANBridgeApiHelper.buildApiIfaceVlanOptions(settingData);

		Assert.assertNotNull("Generated InterfaceVlanOpts should not be null", apiVlanOpts);
		Assert.assertNotNull("Generated InterfaceVlanOpts should not be null", apiVlanOpts.getVlanOptions());
		Assert.assertTrue("Generated InterfaceVlanOpts should not have vlan options", apiVlanOpts.getVlanOptions().isEmpty());

	}

	@Test
	public void buildApiIfaceVlanOptionsWithNativeVlanIdTest() {

		NetworkPortVLANSettingData settingData = new NetworkPortVLANSettingData();
		settingData.setNativeVlanId(0);

		InterfaceVLANOptions apiVlanOpts = VLANBridgeApiHelper.buildApiIfaceVlanOptions(settingData);

		Assert.assertNotNull("Generated InterfaceVlanOpts should not be null", apiVlanOpts);
		Assert.assertNotNull("Generated InterfaceVlanOpts should not be null", apiVlanOpts.getVlanOptions());
		Assert.assertEquals("Generated InterfaceVlanOpts should contain one vlan option", 1, apiVlanOpts.getVlanOptions().size());

		Assert.assertEquals("Generated InterfaceVlanOpts should contain natiVlanId=0 as vlan option", String.valueOf("0"), apiVlanOpts
				.getVlanOptions().get(VLANBridgeApiHelper.NATIVE_VLAN_KEY));

	}

	@Test
	public void buildApiIfaceVlanOptionsTest() {

		NetworkPortVLANSettingData settingData = new NetworkPortVLANSettingData();
		settingData.setNativeVlanId(BD_VLAN_100);
		settingData.setPortMode(PORT_MODE_TRUNK);

		InterfaceVLANOptions apiVlanOpts = VLANBridgeApiHelper.buildApiIfaceVlanOptions(settingData);

		Assert.assertNotNull("Generated InterfaceVlanOpts should not be null", apiVlanOpts);
		Assert.assertNotNull("Generated InterfaceVlanOpts should not be null", apiVlanOpts.getVlanOptions());
		Assert.assertEquals("Generated InterfaceVlanOpts should contain two vlan options", 2, apiVlanOpts.getVlanOptions().size());

		Assert.assertEquals("Generated InterfaceVlanOpts should contain natiVlanId=100 as vlan option", String.valueOf(BD_VLAN_100), apiVlanOpts
				.getVlanOptions().get(VLANBridgeApiHelper.NATIVE_VLAN_KEY));
		Assert.assertEquals("Generated InterfaceVlanOpts should contain port-mode=\"trunk\" as vlan option", PORT_MODE_TRUNK, apiVlanOpts
				.getVlanOptions().get(VLANBridgeApiHelper.PORT_MODE_KEY));

	}

	@Test
	public void buildModelIfaceVlanOptionsTest() {

		InterfaceVLANOptions apiVlanOpts = new InterfaceVLANOptions();
		Map<String, String> vlanOptions = new HashMap<String, String>();

		vlanOptions.put(VLANBridgeApiHelper.PORT_MODE_KEY, PORT_MODE_TRUNK);
		vlanOptions.put(VLANBridgeApiHelper.NATIVE_VLAN_KEY, String.valueOf(BD_VLAN_100));
		apiVlanOpts.setVlanOptions(vlanOptions);

		NetworkPortVLANSettingData settingData = VLANBridgeApiHelper.buildModelIfaceVlanOptions(apiVlanOpts);

		Assert.assertNotNull("Generated NetworkPortVLANSettingData should not be null.", settingData);
		Assert.assertFalse("Generated NetworkPortVLANSettingData should contain portMode", StringUtils.isEmpty(settingData.getPortMode()));
		Assert.assertEquals("Generated NetworkPortVLANSettingData should contain portMode \"" + PORT_MODE_TRUNK + "\"", PORT_MODE_TRUNK,
				settingData.getPortMode());

		Assert.assertTrue("Generated NetworkPortVLANSettingData should contain natiVelanId \"" + BD_VLAN_100 + "\"",
				settingData.getNativeVlanId() == BD_VLAN_100);

	}

	@Test
	public void buildModelIfaceVlanOptionsWithoutInfoTest() {

		InterfaceVLANOptions apiVlanOpts = new InterfaceVLANOptions();
		Map<String, String> vlanOptions = new HashMap<String, String>();

		apiVlanOpts.setVlanOptions(vlanOptions);

		NetworkPortVLANSettingData settingData = VLANBridgeApiHelper.buildModelIfaceVlanOptions(apiVlanOpts);

		Assert.assertNotNull("Generated NetworkPortVLANSettingData should not be null.", settingData);
		Assert.assertTrue("Generated NetworkPortVLANSettingData should contain portMode", StringUtils.isEmpty(settingData.getPortMode()));
		Assert.assertTrue(
				"Generated NetworkPortVLANSettingData should contain natiVelanId \"" + NetworkPortVLANSettingData.NATIVE_VLAN_DEFAULT_VALUE + "\"",
				settingData.getNativeVlanId() == NetworkPortVLANSettingData.NATIVE_VLAN_DEFAULT_VALUE);

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
