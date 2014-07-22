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
		int min = 1;
		int max = 4095;

		List<Integer> vlans = new ArrayList<Integer>(max - min + 1);
		for (int i = min; i <= max; i++) {
			vlans.add(i);
		}
		return vlans;
	}

}
