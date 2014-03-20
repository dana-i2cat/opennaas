package org.opennaas.extensions.vrf.dijkstraroute.capability;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Josep Batallé (josep.batalle@i2cat.net)
 *
 */
@Path("/")
public interface IDijkstraRoutingCapability {
    
    /**
     * Dynamic routing. Return the path.
     * Get Route given Destination IP, DPID of the switch and the input
     * port where the packet entry in the switch
     * @param source Source IP Address in integer format (received from Floodlight)
     * @param target Destination IP Address in integer format (received from Floodlight)
     * @return output Port where the switch forward the packet
     */
    @Path("/route/{source}/{target}")
    @GET
    @Produces({MediaType.TEXT_XML, MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON}) 
    public Response getDynamicRoute(
            @PathParam("source") String source, 
            @PathParam("target") String target);
    
    /**
     * Return the topology filename used and the directory
     * @return 
     */
    @Path("/topologyName")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getTopologyFilename();
    
    /**
     * Set the directory and name of the filename
     * @param fileName
     * @return 
     */
    @Path("/topologyName/{fileName}")
    @GET
    public Response setTopologyFilename(
            @PathParam("fileName") String fileName);
    
    /* ------------- DEMO ------------- */
    /**
     * Used in demonstrations. Request a log from OpenNaaS in order to see the route requests events.
     * @return 
     */
    @Path("/log")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getLog();
    
    /**
     * Send the path
     * @return 
     */
    @Path("/stream")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getStream();
   
    /* ------------- DEMO ------------- */
    
}
