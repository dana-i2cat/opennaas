package org.opennaas.network.capability.queue;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.command.Response;

/**
 * @author Jordi Puig
 */
public interface IQueueService {

	/**
	 * This action will execute each resource queue
	 * 
	 * @return the queue response
	 * @throws CapabilityException
	 */
	public Response execute() throws CapabilityException;

}
