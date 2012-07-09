package org.opennaas.extensions.capability.macbridge.vlanawarebridge;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.capability.macbridge.model.StaticVLANRegistrationEntry;
import org.opennaas.extensions.capability.macbridge.model.VLANConfiguration;

/**
 * A capability that partially implements the management functions specified in 
 * the standard IEEE 802.1q
 * 
 * @author Eduard Grasa
 */
public interface IVLANAwareBridgeCapability extends ICapability {
	
	/**
	 * Add a new VLAN Configuration to the VLAN database
	 * @param vlanConfiguration
	 * @throws CapabilityException
	 */
	public void createVLANConfiguration(VLANConfiguration vlanConriguration) throws CapabilityException;
	
	/**
	 * Delete a VLAN Configuration from the VLAN database
	 * @param vlanId the id of the VLAN
	 * @throws CapabilityException
	 */
	public void deleteVLANConfiguration(int vlanId) throws CapabilityException;
	
	/**
	 * 
	 * @param entry
	 * @throws CapabilityException
	 */
	public void addStaticVLANRegistrationEntryToFilteringDatabase(StaticVLANRegistrationEntry entry) throws CapabilityException;
	
	/**
	 * @param vlanID
	 * @throws CapabilityException
	 */
	public void deleteStaticVLANRegistrationEntryFromFilteringDatabase(int vlanID) throws CapabilityException;
}
