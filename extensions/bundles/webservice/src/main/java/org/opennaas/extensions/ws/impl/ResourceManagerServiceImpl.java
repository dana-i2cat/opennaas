package org.opennaas.extensions.ws.impl;

import javax.jws.WebService;

import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.extensions.ws.services.IResourceManagerService;

/**
 * @author Jordi Puig
 */
@WebService
public class ResourceManagerServiceImpl implements IResourceManagerService {

	private IResourceManager	resourceManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IResourceManagerService#createResource(org.opennaas.core.resources.descriptor.ResourceDescriptor)
	 */
	@Override
	public String createResource(ResourceDescriptor resourceDescriptor) {
		// TODO Auto-generated method stub
		return null;
	}

}
