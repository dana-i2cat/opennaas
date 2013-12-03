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

/**
 *
 * @author Josep Batallé (josep.batalle@i2cat.net)
 *
 */
@Path("/")
public interface IRoutingCapability {

    /**
     * Get Route given Destination IP, DPID of the switch and the input
     * port where the packet entry in the switch
     *
     * @param ipSource Source IP Address in integer format (received from Floodlight)
     * @param ipDest Destination IP Address in integer format (received from Floodlight)
     * @param switchDPID DPID of the switch
     * @param inputPort Input port
     * @param proactive Type of request (reactive/proactive)
     * @return output Port where the switch forward the packet
     */
    @Path("/route/{ipSource}/{ipDest}/{switchDPID}/{inputPort}/{action}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getRoute(@PathParam("ipSource") String ipSource,
            @PathParam("ipDest") String ipDest,
            @PathParam("switchDPID") String switchDPID,
            @PathParam("inputPort") int inputPort,
            @PathParam("action") boolean proactive);

    /**
     * Insert new Route
     *
     * @param ipSource in String format
     * @param ipDest in String format
     * @param switchDPID
     * @param inputPort
     * @param outputPort
     * @return status
     */
    @Path("/route")
    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertRoute(@FormParam("ipSource") String ipSource,
            @FormParam("ipDest") String ipDest,
            @FormParam("switchDPID") String switchDPID,
            @FormParam("inputPort") int inputPort,
            @FormParam("outputPort") int outputPort);

    /**
     * Remove route given id of the route and the IP version
     *
     * @param id Identificator of the route in OpenNaaS
     * @param version IP version of this route
     * @return Status
     */
    @Path("/route")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response removeRoute(@QueryParam("id") int id,
            @QueryParam("version") int version);

    /**
     * Remove route given all parameters of the route
     *
     * @param ipSource in String format
     * @param ipDest in String format
     * @param switchDPID
     * @param inputPort
     * @param outputPort
     * @return status
     */
    @Path("/route")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response removeRoute(@FormParam("ipSource") String ipSource,
            @FormParam("ipDest") String ipDest,
            @FormParam("switchDPID") String switchDPID,
            @FormParam("inputPort") int inputPort,
            @FormParam("outputPort") int outputPort);

    /**
     * Remove all routes
     *
     * @return status
     */
    @Path("/routes")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response removeRoutes();

    /**
     * Get the entire Model
     *
     * @return json with the list of routes
     */
    @Path("/routes")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getRoutes();

    /**
     * Get the Route Table of specific IP version
     *
     * @param version Version of IP (4 or 6)
     * @return json with the list of the required table
     */
    @Path("/routes/{version}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getRoutes(@PathParam("version") int version);

    /**
     * Insert Routes from file
     *
     * @param fileName The name of the file
     * @return Status of the request.
     */
    @Path("/insertRouteFromFile/{fileName}")
    @POST
    @Consumes("application/octet-stream")
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertRouteFile(
            @PathParam("fileName") String fileName/*, 
            InputStream viDescription*/);

    /**
     * Used in demonstrations. Request a log from OpenNaaS in order to see the route requests events.
     * @return 
     */
    @Path("/log")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getLog();
}
