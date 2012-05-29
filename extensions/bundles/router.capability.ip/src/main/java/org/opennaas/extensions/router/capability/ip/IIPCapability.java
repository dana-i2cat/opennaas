package org.opennaas.extensions.router.capability.ip;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.LogicalPort;

public interface IIPCapability extends ICapability {

	/**
	 * Set the given ip to the logical device
	 * 
	 * @param params
	 * @throws CapabilityException
	 */
	public void setIPv4(LogicalDevice logicalDevice, IPProtocolEndpoint ip) throws CapabilityException;

	/**
	 * Set the description for the given interface
	 * 
	 * @param iface
	 * @param ip
	 * @throws CapabilityException
	 */
	public void setInterfaceDescription(LogicalPort iface) throws CapabilityException;

}
