package org.opennaas.extensions.ws.services;

import javax.jws.WebService;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.router.model.opticalSwitch.FiberConnection;

/**
 * 
 * @author Eli Rigol
 * 
 */
@WebService(portName = "ConnectionsCapabilityPort", serviceName = "ConnectionsCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
public interface IConnectionsCapabilityService {

	/*
	 * Interfaces
	 */
	/**
	 * 
	 * @param resourceId
	 * @param connectionRequest
	 * @throws CapabilityException
	 */
	public void makeConnection(String resourceId, FiberConnection connectionRequest) throws CapabilityException;

	/**
	 * 
	 * @param resourceId
	 * @param connectionRequest
	 * @throws CapabilityException
	 */
	public void removeConnection(String resourceId, FiberConnection connectionRequest) throws CapabilityException;
}
