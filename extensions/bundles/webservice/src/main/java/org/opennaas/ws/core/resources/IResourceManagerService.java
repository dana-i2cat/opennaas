package org.opennaas.ws.core.resources;

import javax.jws.WebService;

import org.opennaas.core.resources.descriptor.ResourceDescriptor;

/**
 * @author Jordi Puig
 */
@WebService
public interface IResourceManagerService {

	/**
	 * @param resourceDescriptor
	 * @return
	 */
	public String createResource(ResourceDescriptor resourceDescriptor);
}