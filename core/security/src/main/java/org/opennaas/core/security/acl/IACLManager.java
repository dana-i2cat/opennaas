package org.opennaas.core.security.acl;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * 
 * @author Julio Carlos Barrera
 * 
 */
@Path("/")
public interface IACLManager {

	@Path("/secureResource")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void secureResource(String resourceId, String user);

	@Path("/isResourceAccessible/{resourceId}")
	@GET
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Boolean isResourceAccessible(@PathParam("resourceId") String resourceId);
}
