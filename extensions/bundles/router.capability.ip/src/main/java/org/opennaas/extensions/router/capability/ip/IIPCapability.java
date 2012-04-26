package org.opennaas.extensions.router.capability.ip;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;

public interface IIPCapability extends ICapability {

	// TODO REMOVE
	public Object sendMessage(String idOperation, Object params) throws CapabilityException;

}
