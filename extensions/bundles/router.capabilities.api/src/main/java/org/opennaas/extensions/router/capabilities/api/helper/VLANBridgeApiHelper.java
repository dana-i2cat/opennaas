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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.opennaas.extensions.router.capabilities.api.model.vlanbridge.BridgeDomain;
import org.opennaas.extensions.router.capabilities.api.model.vlanbridge.BridgeDomains;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * @author Julio Carlos Barrera
 * 
 */
public abstract class VLANBridgeApiHelper {

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

}
