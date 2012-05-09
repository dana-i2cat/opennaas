package org.opennaas.extensions.bod.capability.l2bod;

import java.util.List;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.network.model.topology.Interface;

public interface IL2BoDCapability extends ICapability {

	/**
	 * @throws CapabilityException
	 */
	public void requestConnection(RequestConnectionParameters parameters) throws CapabilityException;

	/**
	 * @throws CapabilityException
	 */
	public void shutDownConnection(List<Interface> listInterfaces) throws CapabilityException;
}
