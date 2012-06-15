package org.opennaas.extensions.capability.macbridge.vlanawarebridge.ws;

import javax.jws.WebService;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.capability.macbridge.model.StaticVLANRegistrationEntry;
import org.opennaas.extensions.capability.macbridge.model.VLANConfiguration;

/**
 * A capability that partially implements the management functions specified in 
 * the standard IEEE 802.1q
 * 
 * @author Eduard Grasa
 */
@WebService(portName = "VLANAwareBridgeCapabilityPort", serviceName = "VLANAwareBridgeCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
public interface IVLANAwareBridgeCapabilityService {
	
	/**
	 * Add a new VLAN Configuration to the VLAN database
	 * @param resourceId
	 * @param vlanConfiguration
	 * @throws CapabilityException
	 */
	public void createVLANConfiguration(String resourceId, VLANConfiguration vlanConriguration) throws CapabilityException;
	
	/**
	 * Delete a VLAN Configuration from the VLAN database
	 * @param resourceId
	 * @param vlanId the id of the VLAN
	 * @throws CapabilityException
	 */
	public void deleteVLANConfiguration(String resourceId, int vlanId) throws CapabilityException;
	
	/**
	 * @param resourceId
	 * @param entry
	 * @throws CapabilityException
	 */
	public void addStaticVLANRegistrationEntryToFilteringDatabase(String resourceId, StaticVLANRegistrationEntry entry) throws CapabilityException;
	
	/**
	 * @param resourceId
	 * @param vlanID
	 * @throws CapabilityException
	 */
	public void deleteStaticVLANRegistrationEntryFromFilteringDatabase(String resourceId, int vlanID) throws CapabilityException;
}
