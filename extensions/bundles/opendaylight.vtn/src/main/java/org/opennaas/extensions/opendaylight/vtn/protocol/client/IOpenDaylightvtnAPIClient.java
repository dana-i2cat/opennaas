package org.opennaas.extensions.opendaylight.vtn.protocol.client;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.opennaas.extensions.opendaylight.vtn.model.Boundary;
import org.opennaas.extensions.opendaylight.vtn.model.OpenDaylightController;
import org.opennaas.extensions.opendaylight.vtn.model.OpenDaylightvBridge;
import org.opennaas.extensions.opendaylight.vtn.model.PortMap;
import org.opennaas.extensions.opendaylight.vtn.model.VTN;
import org.opennaas.extensions.opendaylight.vtn.model.vBridgeInterfaces;
import org.opennaas.extensions.opendaylight.vtn.model.vLink;

@Path("/vtn-webapi")
public interface IOpenDaylightvtnAPIClient {

    /**
     * Create a VTN
     * @param vtn
     * @return 
     */
    @Path("/vtns.json")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createVTN(VTN vtn);
    
    /**
     * Delete a VTN
     * @param name
     * @return 
     */
    @Path("/vtns/{name}.json")
    @DELETE
    public Response deleteVTN(@PathParam("name") String name);
    
    /**
     * Create controller
     * @param controller
     * @return 
     */
    @Path("/controllers.json")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createController(OpenDaylightController controller);
    
    /**
     * Create vBRidge
     * @param vtn
     * @param vBridge
     * @return 
     */
    @Path("/vtns/{vtn}/vbridges.json")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createvBridge(@PathParam("vtn") String vtn, OpenDaylightvBridge vBridge);
    
    @Path("/vtns/{vtn}/vbridges/{vBridge}/interfaces.json")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createInterfaces(@PathParam("vtn") String vtn_name, @PathParam("vBridge") String vbr_name, vBridgeInterfaces if_name);

    @Path("/boundaries.json")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createBoundary(Boundary bound);

    @Path("/vtns/{vtn}/vlinks.json")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createvLink(@PathParam("vtn") String vtn_name, vLink vlink);

    @Path("/vtns/{vtn}/vbridges/{vBridge}/interfaces/{iface}/portmap.json")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response configPortMap(@PathParam("vtn") String vtn_name, @PathParam("vBridge") String vbr_name, @PathParam("iface") String if_name, PortMap portMap);
    
}
