package org.opennaas.extensions.router.capability.ip;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.LogicalPort;

public interface IIPCapability extends ICapability {

	/**
	 * @param params
	 * @throws CapabilityException
	 */
	public void setIPv4(LogicalDevice params) throws CapabilityException;

	/**
	 * Set the description for the given interface
	 * 
	 * @param iface
	 * @throws CapabilityException
	 */
	public void setInterfaceDescription(LogicalPort iface) throws CapabilityException;

}
