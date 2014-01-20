package org.opennaas.extensions.vcpe.manager.templates.common;

/*
 * #%L
 * OpenNaaS :: vCPENetwork
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

import org.opennaas.extensions.vcpe.manager.VCPENetworkManagerException;
import org.opennaas.extensions.vcpe.manager.isfree.IsFreeChecker;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.VCPENetworkElement;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;

public class VLANSuggestor {

	public static int suggestVLANWithPreference(VCPENetworkElement phyElement, Interface phyIface, SuggestedValues suggestedVLANs, int preferredVlan)
			throws VCPENetworkManagerException {

		if (!suggestedVLANs.isAlreadySuggested(VCPENetworkModelHelper.generatePhysicalInterfaceKey(phyElement, phyIface), preferredVlan)) {
			if (IsFreeChecker.isVLANFree(null, phyElement.getName(), Integer.toString(preferredVlan), phyIface.getPhysicalInterfaceName())) {
				suggestedVLANs.markAsSuggested(VCPENetworkModelHelper.generatePhysicalInterfaceKey(phyElement, phyIface), preferredVlan);
				return preferredVlan;
			}
		}

		return suggestVLAN(phyElement, phyIface, suggestedVLANs);
	}

	/**
	 * Returns first vlan in vlanRange that is free in given interface of given router, and it's not in given suggestedVLANs. After this call,
	 * returned vlan is already included in suggestedVLANs.
	 * 
	 * @param phyElement
	 * @param phyIface
	 * @param suggestedVLANs
	 * @return
	 * @return first vlan in vlanRange that is free in given interface of given router, and it's not in given suggestedVLANs
	 * @throws VCPENetworkManagerException
	 *             if there is no available vlan in specified vlanRange
	 */
	public static int suggestVLAN(VCPENetworkElement phyElement, Interface phyIface, SuggestedValues suggestedVLANs)
			throws VCPENetworkManagerException {

		List<Integer> candidates = getVLANs(phyElement, phyIface);
		Integer selectedVlan = null;
		for (Integer vlan : candidates) {
			if (!suggestedVLANs.isAlreadySuggested(VCPENetworkModelHelper.generatePhysicalInterfaceKey(phyElement, phyIface), vlan)) {
				if (IsFreeChecker.isVLANFree(null, phyElement.getName(), Integer.toString(vlan), phyIface.getPhysicalInterfaceName())) {
					selectedVlan = vlan;
					break;
				}
			}
		}
		if (selectedVlan == null)
			throw new VCPENetworkManagerException(
					"Unable to find an available vlan for interface " + VCPENetworkModelHelper.generatePhysicalInterfaceKey(
							phyElement, phyIface));

		suggestedVLANs.markAsSuggested(VCPENetworkModelHelper.generatePhysicalInterfaceKey(phyElement, phyIface), selectedVlan);

		return selectedVlan;
	}

	/**
	 * Obtain VLANs given phyInterface in given phyElement is capable to work with.
	 * 
	 * TODO should read from config file. TODO It may happen that each link has different vlan ranges, or that different users can access different
	 * VLANs
	 * 
	 * @param phyRouter
	 * @param phyIface
	 * @return
	 */
	private static List<Integer> getVLANs(VCPENetworkElement phyElement, Interface phyIface) {
		int min = 1;
		int max = 4095;

		List<Integer> vlans = new ArrayList<Integer>(max - min + 1);
		for (int i = min; i <= max; i++) {
			vlans.add(i);
		}
		return vlans;
	}

}
