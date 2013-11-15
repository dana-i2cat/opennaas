package org.opennaas.extensions.vrf.capability.routing;

import java.io.InputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
     * Get Route given Destination IP, DPID of the switch and the input
     * port where the packet entry in the switch
     *
     * @param ipSource Source IP Address
     * @param ipDest Destination IP Address
     * @param switchdpid DPID of the switch
     * @param inputPort Input port
     * @param proactive Type of request (reactive/proactive)
     * @return output Port where the switch forward the packet
     * @throws CapabilityException
     */
    @Path("/route/{ipSource}/{ipDest}/{switchDPID}/{inputPort}/{action}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getRoute(@PathParam("ipSource") String ipSource,
            @PathParam("ipDest") String ipDest,
            @PathParam("switchDPID") String switchDPID,
            @PathParam("inputPort") int inputPort,
            @PathParam("action") boolean proactive) throws CapabilityException;

    /**
     * Insert new Route
     *
     * @param ipSource
     * @param ipDest
     * @param switchDPID
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
            @FormParam("switchDPID") String switchDPID,
            @FormParam("inputPort") int inputPort,
            @FormParam("outputPort") int outputPort) throws CapabilityException;

    /**
     * Remove route given id of the route and the IP version
     *
     * @param id Identificator of the route in OpenNaaS
     * @param version IP version of this route
     * @return Status
     * @throws CapabilityException
     */
    @Path("/route")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response removeRoute(@QueryParam("id") int id,
            @QueryParam("version") int version) throws CapabilityException;

    /**
     * Remove route given all parameters of the route
     *
     * @param ipSource
     * @param ipDest
     * @param switchDPID
     * @param inputPort
     * @param outputPort
     * @return status
     * @throws CapabilityException
     */
    @Path("/route")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response removeRoute(@FormParam("ipSource") String ipSource,
            @FormParam("ipDest") String ipDest,
            @FormParam("switchDPID") String switchDPID,
            @FormParam("inputPort") int inputPort,
            @FormParam("outputPort") int outputPort) throws CapabilityException;

    /**
     * Remove all routes
     *
     * @return status
     * @throws CapabilityException
     */
    @Path("/routes")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response removeRoutes() throws CapabilityException;

    /**
     * Get the entire Model
     *
     * @return json with the list of routes
     * @throws CapabilityException
     */
    @Path("/routes")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getRoutes() throws CapabilityException;

    /**
     * Get the Route Table of specific IP version
     *
     * @param version Version of IP (4 or 6)
     * @return json with the list of the required table
     * @throws CapabilityException
     */
    @Path("/routes/{version}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getRoutes(@PathParam("version") int version) throws CapabilityException;

    /**
     * Insert Routes from file
     *
     * @param fileName The name of the file
     * @return Status of the request.
     * @throws CapabilityException
     */
    @Path("/insertRouteFromFile/{fileName}")
    @POST
    @Consumes("application/octet-stream")
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertRouteFile(@PathParam("fileName") String ipSource, InputStream viDescription) throws CapabilityException;

    /**
     * Put Information about controllers. IP and port <--> switch
     *
     * @param ipController IP of the controller
     * @param portController The port of the controller
     * @param switchDPID DPID of the switch that is connected with the controller
     * @return ok or fail
     * @throws CapabilityException
     */
    @Path("/putSwitchController")
    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public Response putSwitchController(@FormParam("ipController") String ipController,
            @FormParam("portController") String portController,
            @FormParam("switchDPID") String switchDPID) throws CapabilityException;

    /**
     * Request to the controller page. If is offline, return the response. Used
     * only by the GUI.
     *
     * @return
     * @throws CapabilityException
     */
    @Path("/getSwitchControllers")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getControllersInfo() throws CapabilityException;

    /**
     * Get Controller Status
     *
     * @param IP IP address and port of the controller. (ip:port)
     * @return The status of the controller (online/offline)
     * @throws CapabilityException
     */
    @Path("/getControllerStatus/{ip-port}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getControllerStatus(@PathParam("ip-port") String ip) throws CapabilityException;

    /**
     * Used in demonstrations. Request a log from OpenNaaS in order to see the route requests events.
     * @return @throws CapabilityException
     */
    @Path("/log")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getLog() throws CapabilityException;
}
