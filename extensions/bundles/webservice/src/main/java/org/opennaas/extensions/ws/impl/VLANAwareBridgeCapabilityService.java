package org.opennaas.extensions.ws.impl;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.capability.macbridge.model.MACBridge;
import org.opennaas.extensions.capability.macbridge.model.StaticVLANRegistrationEntry;
import org.opennaas.extensions.capability.macbridge.model.VLANConfiguration;
import org.opennaas.extensions.capability.macbridge.vlanawarebridge.IVLANAwareBridgeCapability;
import org.opennaas.extensions.ws.services.IVLANAwareBridgeCapabilityService;

@WebService(portName = "VLANAwareBridgeCapabilityPort", serviceName = "VLANAwareBridgeCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
public class VLANAwareBridgeCapabilityService extends GenericCapabilityService implements IVLANAwareBridgeCapabilityService {

	@Override
	public void createVLANConfiguration(String resourceId, VLANConfiguration vlanConriguration) throws CapabilityException {
		IVLANAwareBridgeCapability capability = (IVLANAwareBridgeCapability) getCapability(resourceId, IVLANAwareBridgeCapability.class);
		capability.createVLANConfiguration(vlanConriguration);
	}

	@Override
	public void deleteVLANConfiguration(String resourceId, int vlanId) throws CapabilityException {
		IVLANAwareBridgeCapability capability = (IVLANAwareBridgeCapability) getCapability(resourceId, IVLANAwareBridgeCapability.class);
		capability.deleteVLANConfiguration(vlanId);
	}

	@Override
	public List<VLANConfiguration> readVLANConfigurationsInVLANDatabase(String resourceId) throws CapabilityException {
		try {
			MACBridge macBridge = (MACBridge) getResource(resourceId).getModel();
			return new ArrayList<VLANConfiguration>(macBridge.getVLANDatabase().values());
		} catch (Exception ex) {
			throw new CapabilityException(ex);
		}
	}

	@Override
	public void addStaticVLANRegistrationEntryToFilteringDatabase(String resourceId, StaticVLANRegistrationEntry entry) throws CapabilityException {
		IVLANAwareBridgeCapability capability = (IVLANAwareBridgeCapability) getCapability(resourceId, IVLANAwareBridgeCapability.class);
		capability.addStaticVLANRegistrationEntryToFilteringDatabase(entry);
	}

	@Override
	public void deleteStaticVLANRegistrationEntryFromFilteringDatabase(String resourceId, int vlanID) throws CapabilityException {
		IVLANAwareBridgeCapability capability = (IVLANAwareBridgeCapability) getCapability(resourceId, IVLANAwareBridgeCapability.class);
		capability.deleteStaticVLANRegistrationEntryFromFilteringDatabase(vlanID);
	}

	/**
	 * Show the static VLAN configuration entries in the filtering database
	 * 
	 * @param resourceId
	 * @return
	 * @throws CapabilityException
	 */
	public List<StaticVLANRegistrationEntry> readStaticVLANRegistrationEntriesInFilteringDatabase(String resourceId) throws CapabilityException {
		try {
			MACBridge macBridge = (MACBridge) getResource(resourceId).getModel();
			return new ArrayList<StaticVLANRegistrationEntry>(macBridge.getFilteringDatabase().getStaticVLANRegistrations().values());
		} catch (Exception ex) {
			throw new CapabilityException(ex);
		}
	}
}
