package org.opennaas.extensions.powernet.capability.example;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;

/**
 * 
 * @author Elisabeth Rigol
 * 
 */
@Path("/")
public interface IExampleCapability extends ICapability {

	/**
	 * Say Hello
	 * 
	 * @throws CapabilityException
	 */
	@Path("/sayHello")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public String sayHello(@QueryParam("userName") String userName) throws CapabilityException;

}
