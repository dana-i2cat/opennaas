package org.opennaas.extensions.ws.services;

import javax.jws.WebService;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.LogicalPort;

/**
 * @author Jordi Puig
 */

@WebService(portName = "IPCapabilityPort", serviceName = "IPCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
public interface IIPCapabilityService {

	/**
	 * Set the given ip to the logical device
	 * 
	 * @param resourceId
	 * @param params
	 * @throws CapabilityException
	 */
	public void setIPv4(String resourceId, LogicalDevice logicalDevice, IPProtocolEndpoint ip) throws CapabilityException;

	/**
	 * Set the description for the given interface
	 * 
	 * @param resourceId
	 * @param iface
	 * @param ip
	 * @throws CapabilityException
	 */
	public void setInterfaceDescription(String resourceId, LogicalPort iface) throws CapabilityException;

}