package org.opennaas.extensions.roadm.capability.connections;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.router.model.opticalSwitch.FiberConnection;

public interface IConnectionsCapability extends ICapability {

	public void makeConnection(FiberConnection connectionRequest) throws CapabilityException;
	public void removeConnection(FiberConnection connectionRequest) throws CapabilityException;

}
