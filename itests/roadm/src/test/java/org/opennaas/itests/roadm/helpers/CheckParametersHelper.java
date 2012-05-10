package org.opennaas.itests.roadm.helpers;

import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.LogicalDevice;

public class CheckParametersHelper {

	/* check if it exists the Interface in the ComputerSystem */
	public static int containsInterface(ComputerSystem model, EthernetPort ethernetPort) {
		int pos = 0;
		for (LogicalDevice logicalDevice : model.getLogicalDevices()) {
			if (logicalDevice instanceof EthernetPort
					&& ((EthernetPort) logicalDevice).getName().equals(ethernetPort.getName())
					&& ((EthernetPort) logicalDevice).getPortNumber() == ethernetPort.getPortNumber()) {
				return pos;
			}
			pos++;
		}
		return -1;
	}

}
