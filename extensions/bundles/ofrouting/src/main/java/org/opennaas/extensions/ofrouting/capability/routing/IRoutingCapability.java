package org.opennaas.extensions.ofrouting.capability.routing;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import javax.ws.rs.core.Response;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;

/**
 * 
 * @author josep
 * 
 */
@Path("/")
public interface IRoutingCapability extends ICapability {

	
        /**
	 * Get Path
	 * 
	 * @throws CapabilityException
	 */
	@Path("/getPath/{ipSource}/{ipDest}/{switchip}/{inputPort}")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getPath(@PathParam("ipSource") String ipSource,
            @PathParam("ipDest") String ipDest,
            @PathParam("switchip") String switchip,
            @PathParam("inputPort") String inputPort
            //@PathParam("switch") Switch sw
                ) throws CapabilityException;
        
        /**
	 * Get Table of routes
	 * 
         * return json with the list of routes
	 * @throws CapabilityException
	 */
	@Path("/getRouteTable")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getRouteTable() throws CapabilityException;
        
        /**
	 * Insert new route
	 * 
         * return ok or fail
	 * @throws CapabilityException
	 */
	@Path("/putRoute")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public String putRoute(@FormParam("ipSource") String ipSource,
        @FormParam("ipDest") String ipDest,
        @FormParam("switchMac") String switchMac,
        @FormParam("inputPort") String inputPort,
        @FormParam("inputPort") String outputPort) throws CapabilityException;

/**
* Update route
*
*/
        
        
        /**
	 * Get register
	 * 
         * return ok or fail
	 * @throws CapabilityException
	 */
	@Path("/getRegister")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getRegister() throws CapabilityException;
        
        /**
	 * Put Controller Info
	 * 
         * return ok or fail
	 * @throws CapabilityException
	 */
	@Path("/putSwitchController")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response putSwitchController(@FormParam("ipController") String ipController,
            @FormParam("portController") String portController,
            @FormParam("switchMac") String switchMac) throws CapabilityException;
        
        /**
	 * Get Info Controllers
	 * 
         * return json with the list of routes
	 * @throws CapabilityException
	 */
	@Path("/getSwitchControllers")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getControllersInfo() throws CapabilityException;
}
