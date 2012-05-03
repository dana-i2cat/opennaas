package org.opennaas.extensions.roadm.capability.monitoring;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;

public interface IMonitoringCapability extends ICapability {

	// TODO REMOVE
	public Object sendMessage(String idOperation, Object params) throws CapabilityException;

}
