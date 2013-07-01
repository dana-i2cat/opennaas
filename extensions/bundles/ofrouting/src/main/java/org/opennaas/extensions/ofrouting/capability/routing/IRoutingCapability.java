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
    public int getPath(@PathParam("ipSource") String ipSource,
            @PathParam("ipDest") String ipDest,
            @PathParam("switchip") String switchip,
            @PathParam("inputPort") int inputPort
            ) throws CapabilityException;

    /**
     * Get Path, including the management of Subnetworks
     * 
     * @throws CapabilityException
     */
    @Path("/getSubPath/{ipSource}/{ipDest}/{switchip}/{inputPort}/{action}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getSubPath(@PathParam("ipSource") String ipSource,
            @PathParam("ipDest") String ipDest,
            @PathParam("switchip") String switchip,
            @PathParam("inputPort") int inputPort,
            @PathParam("proactive") boolean proactive
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
            @FormParam("inputPort") int inputPort,
            @FormParam("inputPort") int outputPort) throws CapabilityException;

    /**
     * Insert new route
     * 
     * return ok or fail
     * @throws CapabilityException
     */
    @Path("/putSubRoute")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String putSubRoute(@FormParam("ipSource") String ipSource,
            @FormParam("ipDest") String ipDest,
            @FormParam("switchMac") String switchMac,
            @FormParam("inputPort") int inputPort,
            @FormParam("inputPort") int outputPort) throws CapabilityException;

    /**
     * Update route
     *
     */
    
    /**
     * Put Information about controllers. IP and port <--> switch
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
    
    /**
     * Insert Routes from file
     * 
     * return json with the list of routes
     * @throws CapabilityException
     */
    @Path("/insertRouteFromFile/{fileName}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String insertRouteFile(@PathParam("fileName") String ipSource) throws CapabilityException;
    
}
