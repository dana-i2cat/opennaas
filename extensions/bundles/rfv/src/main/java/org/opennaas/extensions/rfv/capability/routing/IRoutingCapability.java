package org.opennaas.extensions.rfv.capability.routing;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;

/**
 * 
 * @author Josep Batallé (josep.batalle@i2cat.net)
 * 
 */
@Path("/")
public interface IRoutingCapability extends ICapability {

    /**
     * Get Route given Destination IP, MAC address of the switch and the input port where the packet entry in the switch
     * @param ipSource
     * @param ipDest
     * @param switchip
     * @param inputPort
     * @param proactive
     * @return output Port
     * @throws CapabilityException 
     */
    @Path("/route/{ipSource}/{ipDest}/{switchip}/{inputPort}/{action}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getRoute(@PathParam("ipSource") String ipSource,
            @PathParam("ipDest") String ipDest,
            @PathParam("switchip") String switchip,
            @PathParam("inputPort") int inputPort,
            @PathParam("action") boolean proactive) throws CapabilityException;

    /**
     * Insert new Route
     * @param ipSource
     * @param ipDest
     * @param switchMac
     * @param inputPort
     * @param outputPort
     * @return status
     * @throws CapabilityException 
     */
    @Path("/route")
    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertRoute(@FormParam("ipSource") String ipSource,
            @FormParam("ipDest") String ipDest,
            @FormParam("switchMac") String switchMac,
            @FormParam("inputPort") int inputPort,
            @FormParam("outputPort") int outputPort) throws CapabilityException;

    /**
     * Remove route given id of the route and the IP version
     * @param id
     * @param version
     * @return
     * @throws CapabilityException 
     */
    @Path("/route")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response removeRoute(@FormParam("id") int id,
            @FormParam("version") int version) throws CapabilityException;

    /**
     * Remove route given all parameters of the route
     * @param ipSource
     * @param ipDest
     * @param switchMac
     * @param inputPort
     * @param outputPort
     * @return
     * @throws CapabilityException 
     */
    @Path("/route")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response removeRoute(@FormParam("ipSource") String ipSource,
            @FormParam("ipDest") String ipDest,
            @FormParam("switchMac") String switchMac,
            @FormParam("inputPort") int inputPort,
            @FormParam("outputPort") int outputPort) throws CapabilityException;

    /**
     * Remove all routes
     * @return
     * @throws CapabilityException 
     */
    @Path("/routes")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response removeRoutes() throws CapabilityException;

    /**
     * Get the entire Model
     * @return json with the list of routes
     * @throws CapabilityException 
     */
    @Path("/routes")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getRoutes() throws CapabilityException;

    /**
     * Get the Route Table of specific IP version
     * @param type
     * @return json with the list of the required table
     * @throws CapabilityException 
     */
    @Path("/routes/{type}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getRoutes(@PathParam("type") int type) throws CapabilityException;

    /**
     * Insert Routes from file
     * @param ipSource
     * @return
     * @throws CapabilityException 
     */
    @Path("/insertRouteFromFile/{fileName}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertRouteFile(@PathParam("fileName") String ipSource) throws CapabilityException;

    /**
     * Put Information about controllers. IP and port <--> switch
     * @param ipController
     * @param portController
     * @param switchMac
     * @return ok or fail
     * @throws CapabilityException 
     */
    @Path("/putSwitchController")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response putSwitchController(@FormParam("ipController") String ipController,
            @FormParam("portController") String portController,
            @FormParam("switchMac") String switchMac) throws CapabilityException;

    /**
     * Request to the controller page. If is offline, return the response.
     * Used only by the GUI.
     * @return
     * @throws CapabilityException 
     */
    @Path("/getSwitchControllers")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getControllersInfo() throws CapabilityException;

    /**
     * Get Controller Status
     * @param ip
     * @return
     * @throws CapabilityException 
     */
    @Path("/getControllerStatus/{ip-port}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getControllerStatus(@PathParam("ip-port") String ip) throws CapabilityException;
}
