package org.opennaas.extensions.ws.impl;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.roadm.capability.connections.ConnectionsCapability;
import org.opennaas.extensions.roadm.capability.connections.IConnectionsCapability;
import org.opennaas.extensions.router.model.opticalSwitch.FiberConnection;
import org.opennaas.extensions.ws.services.IConnectionsCapabilityService;

/**
 * 
 * @author Eli Rigol
 * 
 */

@WebService(portName = "ConnectionsCapabilityPort", serviceName = "ConnectionsCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
public class ConnectionsCapabilityServiceImpl extends GenericCapabilityService implements IConnectionsCapabilityService {

	Log	log	= LogFactory.getLog(ConnectionsCapabilityServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IConnectionsCapabilityService#makeConnection(java.lang.String,
	 * org.opennaas.extensions.router.model.opticalSwitch.FiberConnection)
	 */
	@Override
	public void makeConnection(String resourceId, FiberConnection connectionRequest) throws CapabilityException {
		try {
			IConnectionsCapability iConnectionsCapability = (IConnectionsCapability) getCapability(resourceId, ConnectionsCapability.class);
			iConnectionsCapability.makeConnection(connectionRequest);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IConnectionsCapabilityService#removeConnection(java.lang.String,
	 * org.opennaas.extensions.router.model.opticalSwitch.FiberConnection)
	 */
	@Override
	public void removeConnection(String resourceId, FiberConnection connectionRequest) throws CapabilityException {
		try {
			IConnectionsCapability iConnectionsCapability = (IConnectionsCapability) getCapability(resourceId, ConnectionsCapability.class);
			iConnectionsCapability.removeConnection(connectionRequest);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

}
