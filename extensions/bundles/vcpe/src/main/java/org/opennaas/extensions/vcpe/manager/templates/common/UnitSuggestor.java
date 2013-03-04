package org.opennaas.extensions.vcpe.manager.templates.common;

import java.util.List;

import org.apache.commons.lang.math.IntRange;
import org.opennaas.extensions.vcpe.manager.VCPENetworkManagerException;
import org.opennaas.extensions.vcpe.manager.isfree.IsFreeChecker;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.VCPENetworkElement;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;

import com.google.common.primitives.Ints;

public class UnitSuggestor {

	public static int suggestUnitFromVLAN(VCPENetworkElement phyElement, Interface phyIface, SuggestedValues suggestedUnits) {
		int selectedUnit = Long.valueOf(phyIface.getVlan()).intValue();
		if (isUnitAvailable(selectedUnit, phyElement, phyIface, suggestedUnits)) {
			suggestedUnits.markAsSuggested(VCPENetworkModelHelper.generatePhysicalInterfaceKey(phyElement, phyIface), selectedUnit);
			return selectedUnit;
		}

		return suggestUnit(phyElement, phyIface, suggestedUnits);
	}

	public static int suggestUnit(VCPENetworkElement phyElement, Interface phyIface, SuggestedValues suggestedUnits) {

		List<Integer> candidates = getUnits(phyElement, phyIface);
		Integer selectedUnit = null;
		for (Integer unit : candidates) {
			if (isUnitAvailable(unit, phyElement, phyIface, suggestedUnits)) {
				selectedUnit = unit;
				break;
			}
		}
		if (selectedUnit == null)
			throw new VCPENetworkManagerException(
					"Unable to find an available unit for interface " + VCPENetworkModelHelper.generatePhysicalInterfaceKey(
							phyElement, phyIface));

		suggestedUnits.markAsSuggested(VCPENetworkModelHelper.generatePhysicalInterfaceKey(phyElement, phyIface), selectedUnit);

		return selectedUnit;
	}

	public static boolean isUnitAvailable(int unitNum, VCPENetworkElement phyElement, Interface iface, SuggestedValues suggestedUnits) {
		return (!suggestedUnits.isAlreadySuggested(VCPENetworkModelHelper.generatePhysicalInterfaceKey(phyElement, iface), unitNum))
				&& IsFreeChecker.isInterfaceFree(null, phyElement.getName(), iface.getPhysicalInterfaceName() + "." + unitNum);
	}

	/**
	 * Obtain units given phyInterface in given phyElement is capable to work with.
	 * 
	 * TODO should read from config file. TODO It may happen that each interface has different unit ranges, or that different users can access
	 * different units
	 * 
	 * @param phyRouter
	 * @param phyIface
	 * @return
	 */
	private static List<Integer> getUnits(VCPENetworkElement phyElement, Interface phyIface) {
		IntRange vlanRange = new IntRange(1, 4095);
		return Ints.asList(vlanRange.toArray());
	}

}
