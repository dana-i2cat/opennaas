package org.opennaas.extensions.ws.services;

import javax.jws.WebService;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.network.model.topology.Interface;

/**
 * @author Jordi Puig
 */

@WebService(portName = "NetworkBasicCapabilityPort", serviceName = "NetworkBasicCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
public interface INetworkBasicCapabilityService {

	/**
	 * Adds resource to network topology.
	 * 
	 * @param resourceId
	 * @param resourceToAdd
	 *            IResource to be added
	 * @throws CapabilityException
	 *             if an error occurred.
	 */
	public void addResource(String resourceId, String resourceToAdd) throws CapabilityException;

	/**
	 * Removes a resource from network topology.
	 * 
	 * @param resourceId
	 * @param resourceToRemove
	 *            resource to be removed
	 * @throws CapabilityException
	 *             if an error occurred.
	 */
	public void removeResource(String resourceId, String resourceToRemove) throws CapabilityException;

	/**
	 * Creates a L2 connection between given interfaces in network topology.
	 * 
	 * This method does not interact with real interfaces, connection is only created in topology, despite having real connectivity or not.
	 * 
	 * @param resourceId
	 * @param interface1
	 * @param interface2
	 * @throws CapabilityException
	 *             if an error occurred.
	 */
	public void l2attach(String resourceId, Interface interface1, Interface interface2) throws CapabilityException;

	/**
	 * Removes a L2 connection between given interfaces in network topology.
	 * 
	 * This method does not interact with real interfaces, connection is only removed from topology, despite having real connectivity or not.
	 * 
	 * @param resourceId
	 * @param interface1
	 * @param interface2
	 * @throws CapabilityException
	 *             if an error occurred.
	 */
	public void l2detach(String resourceId, Interface interface1, Interface interface2) throws CapabilityException;
}