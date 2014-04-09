package org.opennaas.extensions.opendaylight.vtn.protocol.client;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.opennaas.extensions.opendaylight.vtn.model.Boundary;
import org.opennaas.extensions.opendaylight.vtn.model.LogicalPort;
import org.opennaas.extensions.opendaylight.vtn.model.OpenDaylightController;
import org.opennaas.extensions.opendaylight.vtn.model.OpenDaylightvBridge;
import org.opennaas.extensions.opendaylight.vtn.model.PortMap;
import org.opennaas.extensions.opendaylight.vtn.model.VTN;
import org.opennaas.extensions.opendaylight.vtn.model.vBridgeInterfaces;
import org.opennaas.extensions.opendaylight.vtn.model.vLink;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers.BoundaryWrapper;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers.LogicalPortsOFFlowsWrapper;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers.vBridgeInterfacesWrapper;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers.vBridgesWrapper;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers.vLinksWrapper;

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
    
    @Path("/vtns/{vtn}/vbridges/{vbr}/interfaces.json")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createInterfaces(@PathParam("vtn") String vtn_name, @PathParam("vbr") String vbr_name, vBridgeInterfaces iface);

    @Path("/boundaries.json")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createBoundary(Boundary bound);

    @Path("/vtns/{vtn}/vlinks.json")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createvLink(@PathParam("vtn") String vtn_name, vLink vlink);

    @Path("/vtns/{vtn}/vbridges/{vbr}/interfaces/{iface}/portmap.json")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response configPortMap(@PathParam("vtn") String vtn_name, @PathParam("vbr") String vbr_name, @PathParam("iface") String if_name, PortMap portMap);

    @Path("/vtns/{vtn}/vbridges/{vbr}/interfaces/{iface}/portmap.json")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public PortMap configPortMap(@PathParam("vtn") String vtn_name, @PathParam("vbr") String vbr_name, @PathParam("iface") String iface);
    
    @Path("/controllers/{ctrl}/domains/{domain}/logical_ports/detail.json")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public LogicalPortsOFFlowsWrapper getLogicalPorts(@PathParam("ctrl") String ctrl, @PathParam("domain") String domain);
    
    @Path("/vtns/{vtn}/vbridges/{vbr}.json")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public OpenDaylightvBridge getvBridge(@PathParam("vtn") String vtn, @PathParam("vbr") String vbr);
    
    @Path("/vtns/{vtn}/vbridges.json")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public vBridgesWrapper getvBridges(@PathParam("vtn") String vtn);
    
    @Path("/vtns/{vtn}/vbridges/{vbr}/interfaces.json")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public vBridgeInterfacesWrapper getInterfaces(@PathParam("vtn") String vtn, @PathParam("vbr") String vbr_name);
    
    
    @Path("/boundaries/{boundary}.json")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Boundary getBoundary(@PathParam("boundary") String bound);
    
    @Path("/boundaries.json")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public BoundaryWrapper getBoundaries();
    
    @Path("/vtns/{vtn}/vlinks/{vlink}.json")
    @GET
    public vLink getvLink(@PathParam("vtn") String vtn, @PathParam("vlink") String vlink);
    
    @Path("/vtns/{vtn}/vlinks.json")
    @GET
    public vLinksWrapper getvLinks(@PathParam("vtn") String vtnName);
    
}
