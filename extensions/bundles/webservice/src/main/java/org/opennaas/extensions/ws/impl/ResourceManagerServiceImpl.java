package org.opennaas.extensions.ws.impl;

import javax.inject.Inject;
import javax.jws.WebService;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.extensions.ws.services.IResourceManagerService;

/**
 * @author Jordi Puig
 */
@WebService
public class ResourceManagerServiceImpl implements IResourceManagerService {

	@Inject
	private IResourceManager	resourceManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.ws.core.resources.IResourceManagerService#createResource(org.opennaas.core.resources.descriptor.ResourceDescriptor)
	 */
	@Override
	public String createResource(ResourceDescriptor resourceDescriptor) {
		String resourceId = "";
		IResource resource = null;
		try {
			resource = resourceManager.createResource(resourceDescriptor);
			resourceId = resource != null ? resource.getResourceIdentifier().getId() : "-1";
		} catch (ResourceException e) {
			resourceId = "-1";
		}
		return resourceId;
	}
}
