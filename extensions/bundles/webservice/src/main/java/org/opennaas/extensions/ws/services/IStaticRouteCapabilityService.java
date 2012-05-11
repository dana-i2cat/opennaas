package org.opennaas.extensions.ws.services;

import javax.jws.WebService;

import org.opennaas.core.resources.capability.CapabilityException;

/**
 * 
 * @author Adrian Rosello
 * 
 */

@WebService(portName = "StaticRouteCapabilityPort", serviceName = "StaticRouteCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
public interface IStaticRouteCapabilityService {

	/**
	 * Creates a traffic redirection from the given netIdIpAddress through the interface containing nextHopIpAddress address.
	 * 
	 * Note: This call uses the driver to communicate with the physical device this capability belongs to, and uses actions to modify the device
	 * state. This call end by adding required actions to the device queue, hence device state is not modified yet. An execution of this device queue
	 * is required for queued actions to take effect.
	 * 
	 * @param iface
	 *            to activate (must be a physical one)
	 * @throws CapabilityException
	 * 
	 * */
	public void createStaticRoute(String resourceId, String netIdIpAdress, String maskIpAdress, String nextHopIpAddress) throws CapabilityException;

}
