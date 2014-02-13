package org.opennaas.extensions.vrf.selector.capability;

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
    @Path("/route/{ipSource}/{ipDest}/{switchDPID}/{inputPort}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getRoute(@PathParam("ipSource") String ipSource,
            @PathParam("ipDest") String ipDest,
            @PathParam("switchDPID") String switchDPID,
            @PathParam("inputPort") int inputPort);
    
    /**
     * Set the working mode of the selector (static or dijkstra)
     *
     * @param mode 
     * @return output Port where the switch forward the packet
     */
    @Path("/routeMode/{mode}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response setSelectorMode(@PathParam("mode") String mode);
}
