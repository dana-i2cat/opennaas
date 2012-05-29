package org.opennaas.extensions.ws.services;

import javax.jws.WebService;

import org.opennaas.core.resources.capability.CapabilityException;

/**
 * 
 * @author Eli Rigol
 * 
 */

@WebService(portName = "MonitoringCapabilityPort", serviceName = "MonitoringCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
public interface IMonitoringCapabilityService {

	/*
	 * Interfaces
	 */

	/**
	 * Clear the alarms of the resource
	 * 
	 * @throws CapabilityException
	 */

	public void clearAlarms(String resourceId) throws CapabilityException;

	/**
	 * Get the alarms of the resource
	 * 
	 * return List<ResourceAlarm>
	 * 
	 * @throws CapabilityException
	 */
	// TODO: add it
	// It can not be easily serialized (ResourceAlarm extends Event without a no argument constructor)
	//
	// public List<ResourceAlarm> getAlarms(String resourceId) throws CapabilityException;

}
