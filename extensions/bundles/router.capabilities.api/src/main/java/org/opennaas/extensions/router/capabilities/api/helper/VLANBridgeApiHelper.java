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

		modelBrDomain.setVlanId(modelBrDomain.getVlanId());

		for (String iface : apiBridgeDomain.getInterfacesNames())
			modelBrDomain.addNetworkPort(iface);

		return modelBrDomain;
	}

	public static InterfaceVLANOptions buildApiIfaceVlanOptions(NetworkPortVLANSettingData networkPortVLANSettingData) {

		InterfaceVLANOptions vlanOpts = new InterfaceVLANOptions();
		Map<String, String> vlanOptions = new HashMap<String, String>();

		if (!StringUtils.isEmpty(networkPortVLANSettingData.getPortMode()))
			vlanOptions.put(PORT_MODE_KEY, networkPortVLANSettingData.getPortMode());

		if (networkPortVLANSettingData.getNativeVlanId() != NetworkPortVLANSettingData.NATIVE_VLAN_DEFAULT_VALUE)
			vlanOptions.put(NATIVE_VLAN_KEY, String.valueOf(networkPortVLANSettingData.getNativeVlanId()));

		vlanOpts.setVlanOptions(vlanOptions);

		return vlanOpts;
	}

	public static NetworkPortVLANSettingData buildModelIfaceVlanOptions(InterfaceVLANOptions vlanOptions) {
		NetworkPortVLANSettingData modelVlanOpts = new NetworkPortVLANSettingData();

		if (vlanOptions.getVlanOptions() != null) {

			if (vlanOptions.getVlanOptions().containsKey(PORT_MODE_KEY)) {
				modelVlanOpts.setPortMode(vlanOptions.getVlanOptions().get(PORT_MODE_KEY));
				vlanOptions.getVlanOptions().remove(PORT_MODE_KEY);
			}

			if (vlanOptions.getVlanOptions().containsKey(NATIVE_VLAN_KEY)) {
				modelVlanOpts.setNativeVlanId((Integer.valueOf(vlanOptions.getVlanOptions().get(NATIVE_VLAN_KEY))));
				vlanOptions.getVlanOptions().remove(NATIVE_VLAN_KEY);
			}

			if ((!vlanOptions.getVlanOptions().containsKey(NATIVE_VLAN_KEY)) && (!vlanOptions.getVlanOptions().containsKey(PORT_MODE_KEY)) && (!vlanOptions
					.getVlanOptions().isEmpty()))

				log.warn("Ignoring unknown interfaceVlanOptions values : " + vlanOptions.getVlanOptions().keySet());
		}

		return modelVlanOpts;

	}
}
