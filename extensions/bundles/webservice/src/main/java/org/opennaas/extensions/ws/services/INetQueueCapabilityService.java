package org.opennaas.extensions.ws.services;

import javax.jws.WebService;

import org.opennaas.core.resources.capability.CapabilityException;

/**
 * 
 * @author Adrian Rosello
 * 
 */

@WebService(portName = "NetQueueCapabilityPort", serviceName = "NetQueueCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
public interface INetQueueCapabilityService {

	/**
	 * Executes the queues of all routers belonging to the given network resource.
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
	public void execute(String resourceId) throws CapabilityException;

}
