package org.opennaas.extensions.network.capability.queue;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;

/**
 * @author Jordi Puig
 */
@Path("/")
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
	public Response execute() throws CapabilityException;

}