package org.opennaas.extensions.router.capabilities.api.helper;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.extensions.router.capabilities.api.model.vlanbridge.BridgeDomain;
import org.opennaas.extensions.router.capabilities.api.model.vlanbridge.BridgeDomains;
import org.opennaas.extensions.router.capabilities.api.model.vlanbridge.InterfaceVLANOptions;
import org.opennaas.extensions.router.model.NetworkPortVLANSettingData;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * @author Julio Carlos Barrera
 * 
 */
public abstract class VLANBridgeApiHelper {

	static Log					log				= LogFactory.getLog(VLANBridgeApiHelper.class);

	public static final String	PORT_MODE_KEY	= "port-mode";
	public static final String	NATIVE_VLAN_KEY	= "native-vlan-id";
	public static final String	FILTER_INPUT	= "filter-input";
	public static final String	FILTER_OUTPUT	= "filter-output";
	public static final String	VLAN_MEMBERS	= "vlan-members";
	

	public static BridgeDomains buildApiBridgeDomains(List<org.opennaas.extensions.router.model.BridgeDomain> modelBridgeDomains) {

		BridgeDomains apiBridgeDomains = new BridgeDomains();
		List<String> domainNames = new ArrayList<String>();

		for (org.opennaas.extensions.router.model.BridgeDomain bridgeDomain : modelBridgeDomains)
			domainNames.add(bridgeDomain.getElementName());

		apiBridgeDomains.setDomainNames(domainNames);

		return apiBridgeDomains;

	}

	public static BridgeDomain buildApiBridgeDomain(
			org.opennaas.extensions.router.model.BridgeDomain modelBrDomain) {

		BridgeDomain brDomain = new BridgeDomain();

		brDomain.setDomainName(modelBrDomain.getElementName());
		brDomain.setVlanid(modelBrDomain.getVlanId());

		if (!StringUtils.isEmpty(modelBrDomain.getDescription()))
			brDomain.setDescription(modelBrDomain.getDescription());

		for (String iface : modelBrDomain.getNetworkPorts())
			brDomain.getInterfacesNames().add(iface);

		brDomain.setIpAddress(modelBrDomain.getIpAddress());

		return brDomain;
	}

	public static org.opennaas.extensions.router.model.BridgeDomain buildModelBridgeDomain(BridgeDomain apiBridgeDomain) {

		org.opennaas.extensions.router.model.BridgeDomain modelBrDomain = new org.opennaas.extensions.router.model.BridgeDomain();

		if (!StringUtils.isEmpty(apiBridgeDomain.getDomainName()))
			modelBrDomain.setElementName(apiBridgeDomain.getDomainName());

		if (!StringUtils.isEmpty(apiBridgeDomain.getDescription()))
			modelBrDomain.setDescription(apiBridgeDomain.getDescription());

		modelBrDomain.setVlanId(apiBridgeDomain.getVlanid());

		for (String iface : apiBridgeDomain.getInterfacesNames())
			modelBrDomain.addNetworkPort(iface);

		return modelBrDomain;
	}

	public static InterfaceVLANOptions buildApiIfaceVlanOptions(final NetworkPortVLANSettingData networkPortVLANSettingData) {

		InterfaceVLANOptions vlanOpts = new InterfaceVLANOptions();
		Map<String, String> vlanOptions = new HashMap<String, String>();

		if (!StringUtils.isEmpty(networkPortVLANSettingData.getPortMode()))
			vlanOptions.put(PORT_MODE_KEY, networkPortVLANSettingData.getPortMode());

		if (networkPortVLANSettingData.getNativeVlanId() != NetworkPortVLANSettingData.NATIVE_VLAN_DEFAULT_VALUE)
			vlanOptions.put(NATIVE_VLAN_KEY, String.valueOf(networkPortVLANSettingData.getNativeVlanId()));
		
		if (!StringUtils.isEmpty(networkPortVLANSettingData.getInputFilterName()))
			vlanOptions.put(FILTER_INPUT, networkPortVLANSettingData.getInputFilterName());
		
		if (!StringUtils.isEmpty(networkPortVLANSettingData.getOutputFilterName()))
			vlanOptions.put(FILTER_OUTPUT, networkPortVLANSettingData.getOutputFilterName());
		
		if (networkPortVLANSettingData.getVlanMembers() != null && !networkPortVLANSettingData.getVlanMembers().isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (String member : networkPortVLANSettingData.getVlanMembers()) {
				sb.append(member);
				sb.append(",");
			}
			// substring used to remove last ","
			vlanOptions.put(VLAN_MEMBERS, sb.substring(0, sb.length()-1));
		}
		vlanOpts.setVlanOptions(vlanOptions);

		return vlanOpts;
	}

	public static NetworkPortVLANSettingData buildModelIfaceVlanOptions(final InterfaceVLANOptions vlanOptions) {
		NetworkPortVLANSettingData modelVlanOpts = new NetworkPortVLANSettingData();

		List<String> ignoredOptions = new ArrayList<String>();
		if (vlanOptions.getVlanOptions() != null) {
			
			for (String key : vlanOptions.getVlanOptions().keySet()) {
				if (key.equals(PORT_MODE_KEY)) 
					modelVlanOpts.setPortMode(vlanOptions.getVlanOptions().get(PORT_MODE_KEY));
				else if (key.equals(NATIVE_VLAN_KEY)) 
					modelVlanOpts.setNativeVlanId((Integer.valueOf(vlanOptions.getVlanOptions().get(NATIVE_VLAN_KEY))));
				else if (key.equals(FILTER_INPUT))
					modelVlanOpts.setInputFilterName(vlanOptions.getVlanOptions().get(FILTER_INPUT));
				else if (key.equals(FILTER_OUTPUT))
					modelVlanOpts.setOutputFilterName(vlanOptions.getVlanOptions().get(FILTER_OUTPUT));
				else if (key.equals(VLAN_MEMBERS)) {
					String[] allMembersArray = vlanOptions.getVlanOptions().get(VLAN_MEMBERS).split(",");
					List<String> allMembers = new ArrayList<String>(Arrays.asList(allMembersArray));
					modelVlanOpts.setVlanMembers(allMembers);
				} else {
					ignoredOptions.add(key);
				}
			}
			
			if (!ignoredOptions.isEmpty())
				log.warn("Ignoring unknown interfaceVlanOptions values : " + ignoredOptions);
		}

		return modelVlanOpts;

	}
}
