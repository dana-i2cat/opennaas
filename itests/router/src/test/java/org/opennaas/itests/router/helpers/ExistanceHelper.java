package org.opennaas.itests.router.helpers;

import java.util.List;

import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.LogicalTunnelPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.System;

public class ExistanceHelper {

	/**
	 * Checks if a logical router exists.
	 * 
	 * @param physicalRouter
	 *            physical router that contains it.
	 * @param logicalRouterName
	 *            name of the logical route
	 * @return true if the logical router exists
	 */
	public static boolean checkExistLogicalRouter(ComputerSystem physicalRouter, String logicalRouterName) {
		List<org.opennaas.extensions.router.model.System> logicalRouters = physicalRouter.getSystems();
		for (System logicalRouter : logicalRouters) {
			if (logicalRouter.getName().equals(logicalRouterName))
				return true;
		}
		return false;
	}

	/**
	 * Checks if an interface exists.
	 * 
	 * @param inter
	 * @param port
	 * @param Ip
	 * @param mask
	 * @param system
	 * @return true if interface exists
	 */
	public static boolean checkInterface(String inter, String port, String Ip, String mask, ComputerSystem system) {
		Boolean found = false;
		List<LogicalDevice> ld = system.getLogicalDevices();
		if (ld == null)
			return false;
		for (LogicalDevice l : ld) {
			// Only check the modified interface
			if (l.getElementName().equalsIgnoreCase(inter)) {
				if (l instanceof EthernetPort) {
					EthernetPort eth = (EthernetPort) l;
					if (eth.getPortNumber() == Integer.parseInt(port)) {
						found = true;
						if (Ip == null || mask == null)
							return true;
						List<ProtocolEndpoint> pp = eth.getProtocolEndpoint();
						if (pp == null)
							return false;
						for (ProtocolEndpoint p : pp) {
							if (p instanceof IPProtocolEndpoint) {
								return (Ip.equals(((IPProtocolEndpoint) p).getIPv4Address()) && mask.equals(((IPProtocolEndpoint) p).getSubnetMask()));
							}
						}
					}
				} else if (l instanceof LogicalTunnelPort) {
					LogicalTunnelPort lt = (LogicalTunnelPort) l;
					if (lt.getPortNumber() == Integer.parseInt(port)) {
						found = true;
						if (Ip == null || mask == null)
							return true;
						List<ProtocolEndpoint> pp = lt.getProtocolEndpoint();
						if (pp == null)
							return false;
						for (ProtocolEndpoint p : pp) {
							if (p instanceof IPProtocolEndpoint) {
								return (Ip.equals(((IPProtocolEndpoint) p).getIPv4Address()) && mask.equals(((IPProtocolEndpoint) p).getSubnetMask()));
							}
						}
					}
				}
			}
		}
		// true if interface exists
		return found;
	}

	/* check if it exists the Interface in the ComputerSystem */
	public static int containsSubInterface(ComputerSystem model, EthernetPort ethernetPort) {
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

	/* check if it exists the Interface in the ComputerSystem */
	public static int containsInterface(ComputerSystem model, LogicalPort logicalPort) {
		int pos = 0;
		for (LogicalDevice logicalDevice : model.getLogicalDevices()) {
			if (logicalDevice instanceof LogicalPort
					&& ((LogicalPort) logicalDevice).getName().equals(logicalPort.getName())) {
				return pos;
			}
			pos++;
		}
		return -1;
	}
}
