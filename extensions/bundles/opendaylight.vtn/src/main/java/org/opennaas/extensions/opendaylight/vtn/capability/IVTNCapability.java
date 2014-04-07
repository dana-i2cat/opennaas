package org.opennaas.extensions.opendaylight.vtn.capability;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author Josep Batallé (josep.batalle@i2cat.net)
 *
 */
@Path("/")
public interface IVTNCapability {
    
    /**
     * ODL Code values:
     * (https://wiki.opendaylight.org/view/OpenDaylight_Virtual_Tenant_Network_(VTN):VTN_Coordinator:RestApi)
     */
    /**
     * Create a new VTN
     *
     * @param name
     * @return will be one of the HTML Code values defined in ODL
     */
    @Path("/vtns/{vtn-name}")
    @GET
    public Response createVTN(@PathParam("vtn-name") String vtnName);
    
    /**
     * Remove a VTN
     * 
     * @param name
     * @return will be one of the HTML Code values defined in ODL
     */
    @Path("/vtns/{vtn-name}")
    @DELETE
    public Response removeVTN(@PathParam("vtn-name") String vtnName);
    
    /**
     * Create controller
     * @param name
     * @param ipaddr
     * @param type
     * @return 
     */
    @Path("/controller/{name}/{ipaddr}/{type}")
    @GET
    public Response createController(@PathParam("name") String name, @PathParam("ipaddr") String ipaddr, @PathParam("type") String type);
    
    /**
     * Create vBridge
     * @param vtnName
     * @param vBridge
     * @param ctrl
     * @param domain
     * @return 
     */
    @Path("/vtns/{vtn-name}/vbridges/{vBridge}/{ctrl}/{domain}")
    @GET
    public Response createvBridge(@PathParam("vtn-name") String vtnName, @PathParam("vBridge") String vBridge, 
            @PathParam("ctrl") String ctrl, @PathParam("domain") String domain);
    
    @Path("/vtns/{vtn-name}/vbridges/{vBridge}/interfaces/{iface}")
    @GET
    public Response createInterfaces(@PathParam("vtn-name") String vtnName, @PathParam("vBridge") String vBridge, @PathParam("iface") String iface);
    
    @Path("/boundaries/{id}/{ctrl1}/{domain1}/{port1}/{ctrl1}/{domain1}/{port1}")
    @GET
    public Response createBoundary(@PathParam("id") String id, @PathParam("ctrl1") String ctrl1, 
            @PathParam("domain1") String domain1, @PathParam("port1") String port1, @PathParam("ctrl2") String ctrl2, 
            @PathParam("domain2") String domain2, @PathParam("port2") String port2);
    
    @Path("/boundaries/{vtn-name}/{vBridge}/{iface}/{logical-port}")
    @GET
    public Response mapPort(@PathParam("vtn-name") String vtnName, @PathParam("vBridge") String vBridge, 
            @PathParam("iface") String iface, @PathParam("logical-port") String port);
    
    @Path("/test")
    @GET
    public Response test();
    
    /**
     * Set coordinator address. IP address and port
     * @param address
     * @param port
     * @return 
     */
    @Path("/coordinator/{address}/{port}")
    @GET
    public Response coordinatorAddress(@PathParam("address") String address, @PathParam("port") String port);

}
