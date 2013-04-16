/**
 * 
 */
package org.opennaas.extensions.vcpe.capability.builder.builders.helpers;

import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
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

	/**
	 * Execute the queue of the resource
	 * 
	 * @param vcpe
	 * @throws ResourceException
	 * @throws ProtocolException
	 */
	public static void executeQueue(IResource vcpe) throws ResourceException, ProtocolException {
		IQueueManagerCapability qCapability = (IQueueManagerCapability) vcpe.getCapabilityByInterface(IQueueManagerCapability.class);
		QueueResponse response = qCapability.execute();
		if (!response.isOk()) {
			String commitMsg = response.getConfirmResponse().getInformation();
			throw new ResourceException(
					"Failed to execute queue for resource " + vcpe.getResourceDescriptor().getInformation().getName() + ": " + commitMsg);
		}
	}
}
