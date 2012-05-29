package org.opennaas.extensions.ws.services;

import javax.jws.WebService;

import org.opennaas.core.resources.capability.CapabilityException;

/**
 * 
 * @author Adrian Rosello
 * 
 */

@WebService(portName = "NetOSPFCapabilityPort", serviceName = "NetOSPFCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
public interface INetOSPFCapabilityService {

	/**
	 * Activates OSPF on the network.
	 * 
	 * Note: This call uses the driver to communicate with the physical device this capability belongs to, and uses actions to modify the device
	 * state. This call end by adding required actions to the device queue, hence device state is not modified yet. An execution of this device queue
	 * is required for queued actions to take effect.
	 * 
	 * @param resourceID
	 *            Id of the network
	 * @throws CapabilityException
	 * 
	 * */
	public void activateOSPF(String resourceId) throws CapabilityException;

	/**
	 * Deactivates OSPF on the network.
	 * 
	 * Note: This call uses the driver to communicate with the physical device this capability belongs to, and uses actions to modify the device
	 * state. This call end by adding required actions to the device queue, hence device state is not modified yet. An execution of this device queue
	 * is required for queued actions to take effect.
	 * 
	 * @param resourceId
	 *            Id of the network.
	 * @throws CapabilityException
	 * 
	 * */
	public void deactivateOSPF(String resourceId) throws CapabilityException;

}
