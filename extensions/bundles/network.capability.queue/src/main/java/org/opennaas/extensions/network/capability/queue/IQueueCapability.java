package org.opennaas.extensions.network.capability.queue;

import java.util.Map;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.queue.QueueResponse;

/**
 * @author Jordi Puig
 */
public interface IQueueCapability extends ICapability {

	/**
	 * This action will execute each resource queue
	 * 
	 * @return the queue response
	 * @throws CapabilityException
	 */
	public Map<String, QueueResponse> execute() throws CapabilityException;

}