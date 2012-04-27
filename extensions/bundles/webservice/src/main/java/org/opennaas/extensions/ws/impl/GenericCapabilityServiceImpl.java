/**
 * 
 */
package org.opennaas.extensions.ws.impl;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.ICapability;

/**
 * @author Jordi
 */
public class GenericCapabilityServiceImpl {

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
	protected ICapability getCapability(String resourceId, String type) throws ResourceException {
		IResource resource = getResource(resourceId);
		ICapability iCapability = null;
		for (ICapability capability : resource.getCapabilities()) {
			String _type = capability.getCapabilityInformation().getType();
			if (_type.equals(type)) {
				iCapability = capability;
			}
		}
		return iCapability;
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
