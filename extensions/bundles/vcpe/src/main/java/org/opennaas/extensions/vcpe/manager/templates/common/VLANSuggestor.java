package org.opennaas.extensions.vcpe.manager.templates.common;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.vcpe.manager.VCPENetworkManagerException;
import org.opennaas.extensions.vcpe.manager.isfree.IsFreeChecker;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.VCPENetworkElement;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;

public class VLANSuggestor {

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
