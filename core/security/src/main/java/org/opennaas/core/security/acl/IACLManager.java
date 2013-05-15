package org.opennaas.core.security.acl;

import org.opennaas.core.resources.Resource;

/**
 * 
 * @author Julio Carlos Barrera
 * 
 */
public interface IACLManager {

	public void secureResource(Resource resource, String user);

	public boolean isResourceAccessible(String resourceId);
}
