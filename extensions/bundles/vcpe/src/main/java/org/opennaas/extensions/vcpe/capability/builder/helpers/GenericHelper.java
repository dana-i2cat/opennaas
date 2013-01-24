/**
 * 
 */
package org.opennaas.extensions.vcpe.capability.builder.helpers;

import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.extensions.vcpe.Activator;

/**
 * @author Jordi
 * 
 */
public class GenericHelper {

	/**
	 * @return
	 * @throws ResourceException
	 */
	public static IResourceManager getResourceManager() throws ResourceException {
		try {
			return Activator.getResourceManagerService();
		} catch (ActivatorException e) {
			throw new ResourceException("Could not find ResourceManager", e);
		}
	}

	/**
	 * @return
	 * @throws ResourceException
	 */
	public static IProtocolManager getProtocolManager() throws ResourceException {
		try {
			return Activator.getProtocolManagerService();
		} catch (ActivatorException e) {
			throw new ResourceException("Could not find ProtocolManager", e);
		}
	}
}
