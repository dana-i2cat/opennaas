/**
 * 
 */
package org.opennaas.extensions.ws.impl;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;

/**
 * @author Jordi
 */
public class GenericCapabilityService {

	private IResourceManager	resourceManager;

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
