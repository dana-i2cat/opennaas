package org.opennaas.extensions.capability.macbridge.vlanawarebridge.ws;

import javax.jws.WebService;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.capability.macbridge.model.StaticVLANRegistrationEntry;
import org.opennaas.extensions.capability.macbridge.model.VLANConfiguration;
import org.opennaas.extensions.capability.macbridge.vlanawarebridge.IVLANAwareBridgeCapability;

@WebService(portName = "VLANAwareBridgeCapabilityPort", serviceName = "VLANAwareBridgeCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
public class VLANAwareBridgeCapabilityService implements IVLANAwareBridgeCapabilityService {

	private IResourceManager resourceManager = null;
	
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
	 * Get the capability from the resource
	 * 
	 * @param resourceId
	 * @return the resource with resourceId = resourceId
	 * @throws ResourceException
	 */
	protected IResource getResource(String resourceId) throws ResourceException {
		return resourceManager.getResourceById(resourceId);
	}

	/**
	 * Get the capability from the resource
	 * 
	 * @param capabilities
	 * @param type
	 * @return the capability
	 * @throws ResourceException
	 */
	protected ICapability getCapability(String resourceId, Class<? extends ICapability> _class) throws CapabilityException {
		try {
			IResource resource = getResource(resourceId);
			return resource.getCapabilityByInterface(_class);
		} catch (ResourceException e) {
			throw new CapabilityException("Capability not found", e);
		}
	}

	/**
	 * @return the resourceManager
	 */
	public IResourceManager getResourceManager() {
		return resourceManager;
	}

	/**
	 * @param resourceManager
	 *            the resourceManager to set
	 */
	public void setResourceManager(IResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

}
