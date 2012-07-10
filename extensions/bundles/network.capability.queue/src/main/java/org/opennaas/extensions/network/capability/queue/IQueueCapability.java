package org.opennaas.extensions.network.capability.queue;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.queue.QueueResponse;

/**
 * @author Jordi Puig
 */
@Path("/netqueue")
public interface IQueueCapability extends ICapability {

	/**
	 * This action will execute each resource queue
	 * 
	 * @return the queue response
	 * @throws CapabilityException
	 */
	@Path("/execute")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Map<String, QueueResponse> execute() throws CapabilityException;

}