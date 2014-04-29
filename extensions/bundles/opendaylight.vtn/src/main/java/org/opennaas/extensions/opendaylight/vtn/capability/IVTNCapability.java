package org.opennaas.extensions.opendaylight.vtn.capability;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.opennaas.extensions.opendaylight.vtn.model.Boundary;
import org.opennaas.extensions.opendaylight.vtn.model.OpenDaylightvBridge;
import org.opennaas.extensions.opendaylight.vtn.model.PortMap;
import org.opennaas.extensions.opendaylight.vtn.model.vLink;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers.BoundaryWrapper;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers.LogicalPortsOFFlowsWrapper;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers.vBridgeInterfacesWrapper;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers.vBridgesWrapper;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers.vLinksWrapper;

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
     * @param vtnName
     * @return will be one of the HTML Code values defined in ODL
     */
    @Path("/vtns/{vtn-name}")
    @GET
    public Response createVTN(@PathParam("vtn-name") String vtnName);

    /**
     * Remove a VTN
     *
     * @param vtnName
     * @return will be one of the HTML Code values defined in ODL
     */
    @Path("/vtns/{vtn-name}")
    @DELETE
    public Response removeVTN(@PathParam("vtn-name") String vtnName);

    /**
     * Create controller
     *
     * @param name
     * @param ipaddr
     * @param type
     * @param description
     * @return
     */
    @Path("/controller/{name}/{ipaddr}/{type}/{description}")
    @GET
    public Response createController(@PathParam("name") String name, @PathParam("ipaddr") String ipaddr, @PathParam("type") String type, @PathParam("description") String description);

    /**
     * Create vBridge
     *
     * @param vtnName
     * @param vBridge
     * @param ctrl
     * @param domain
     * @return
     */
    @Path("/vtns/{vtn-name}/vbridges/{vbr}/{ctrl}/{domain}")
    @GET
    public Response createvBridge(@PathParam("vtn-name") String vtnName, @PathParam("vbr") String vBridge,
            @PathParam("ctrl") String ctrl, @PathParam("domain") String domain);

    @Path("/vtns/{vtn-name}/vbridges/{vbr}/interfaces/{iface}")
    @GET
    public Response createInterfaces(@PathParam("vtn-name") String vtnName, @PathParam("vbr") String vBridge, @PathParam("iface") String iface);

    @Path("/boundaries/{id}/{ctrl1}/{domain1}/{port1}/{ctrl1}/{domain1}/{port1}")
    @GET
    public Response createBoundary(@PathParam("id") String id, @PathParam("ctrl1") String ctrl1,
            @PathParam("domain1") String domain1, @PathParam("port1") String port1, @PathParam("ctrl2") String ctrl2,
            @PathParam("domain2") String domain2, @PathParam("port2") String port2);

    @Path("/vtns/{vtn-name}/vlink/{vlink-name}/{vnode1}/{if1}/{vnode2}/{if2}/{bound-id}/{vlan-id}")
    @GET
    public Response createvLink(@PathParam("vtn-name") String vtnName, @PathParam("vlink-name") String vlinkName,
            @PathParam("vnode1") String vnode1, @PathParam("if1") String if1, @PathParam("vnode2") String vnode2,
            @PathParam("if2") String if2, @PathParam("bound-id") String boundId, @PathParam("vlan-id") String vlanId);

    @Path("/mapport/{vtn-name}/{vbr}/{iface}/{logical-port}")
    @GET
    public Response mapPort(@PathParam("vtn-name") String vtnName, @PathParam("vbr") String vBridge,
            @PathParam("iface") String iface, @PathParam("logical-port") String port);

    @Path("/mapport/{vtn-name}/{vbr}/{iface}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public PortMap mapPort(@PathParam("vtn-name") String vtnName, @PathParam("vbr") String vBridge, @PathParam("iface") String iface);

    @Path("/logicports/{ctrl}/{domain}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public LogicalPortsOFFlowsWrapper getLogicalPorts(@PathParam("ctrl") String ctrl, @PathParam("domain") String domain);

    @Path("/checkportmap")
    @GET
    public void checkPortMap();

    @Path("/vbridge/{vtn-name}/{vbr}")
    @GET
    public OpenDaylightvBridge getvBridge(@PathParam("vtn-name") String vtnName, @PathParam("vbr") String vBridge);

    @Path("/vbridges/{vtn-name}")
    @GET
    public vBridgesWrapper getvBridges(@PathParam("vtn-name") String vtnName);

    @Path("/interfaces/{vtn-name}/{vbr}")
    @GET
    public vBridgeInterfacesWrapper getInterfaces(@PathParam("vtn-name") String vtnName, @PathParam("vbr") String vBridge);

    @Path("/boundary/{bound}")
    @GET
    public Boundary getBoundary(@PathParam("bound") String bound);

    @Path("/boundaries")
    @GET
    public BoundaryWrapper getBoundaries();

    @Path("/vlink/{vtn-name}/{vlink}")
    @GET
    public vLink getvLink(@PathParam("vtn-name") String vtnName, @PathParam("vlink") String bound);

    @Path("/vlinks/{vtn-name}")
    @GET
    public vLinksWrapper getvLinks(@PathParam("vtn-name") String vtnName);

    /**
     * Set coordinator address. IP address and port
     *
     * @param address
     * @param port
     * @return
     */
    @Path("/coordinator/{address}/{port}")
    @GET
    public Response coordinatorAddress(@PathParam("address") String address, @PathParam("port") String port);

    @Path("/ipreq/{DPID}/{Port}")
    @GET
    public Response ipreq(@PathParam("DPID") String DPID, @PathParam("Port") String Port);

    @Path("/updateInterfaces")
    @GET
    public void updateInterfaces();
    
    @Path("/update")
    @GET
    public void update();
    
    @Path("/cleanvtn")
    @GET
    public void cleanVTN();
}
