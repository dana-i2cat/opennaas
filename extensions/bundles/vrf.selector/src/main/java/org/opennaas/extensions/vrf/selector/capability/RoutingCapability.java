package org.opennaas.extensions.vrf.selector.capability;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.sdnnetwork.capability.ofprovision.IOFProvisioningNetworkCapability;
import org.opennaas.extensions.vrf.dijkstraroute.capability.DijkstraRoutingCapability;
import org.opennaas.extensions.vrf.staticroute.capability.IStaticRoutingCapability;

/**
 *
 * @author Josep Batallé (josep.batalle@i2cat.net)
 *
 */
public class RoutingCapability implements IRoutingCapability {

    Log log = LogFactory.getLog(RoutingCapability.class);
    private final IStaticRoutingCapability service;
    private String mode = "static";
    private Boolean proactive = true;

    public RoutingCapability(List<IStaticRoutingCapability> listStaticRoutingCapability) {

        super();
//        this.service = staticRoutingCapability;
//        log.error("List: "+listStaticRoutingCapability.size());
        this.service = listStaticRoutingCapability.get(0);
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Boolean isProactive() {
        return proactive;
    }

    public void setProactive(Boolean proactive) {
        this.proactive = proactive;
    }

    @Override
    public Response getRoute(String ipSource, String ipDest, String switchDPID, int inputPort) {
        log.error("SELECTOR: GET ROUTE");
        log.error(ipSource + " " + ipDest + " " + switchDPID + " " + inputPort);
        Response response = null;
        if (mode.equals("static")) {
            response = service.getRoute(ipSource, ipDest, switchDPID, inputPort, proactive);
        } else if (mode.equals("dijkstra")) {
            DijkstraRoutingCapability dynamicRoute = new DijkstraRoutingCapability();
            response = dynamicRoute.getDynamicRoute(ipSource, ipDest);
        }
        return response;
    }

    @Override
    public Response setSelectorMode(String mode) {
        if (mode.equals("static")) {
            this.mode = "static";
        } else if (mode.equals("dijkstra")) {
            this.mode = "dijkstra";
        } else {
            return Response.serverError().entity("Incorrect mode. Possible modes: static, dijkstra. Remain active mode: " + mode).build();
        }
        return Response.ok().build();
    }

    @Override
    public Response getSelectorMode() {
        return Response.ok(mode).build();
    }

    @Override
    public Response switchMapping() {
        IResourceManager resourceManager;
        try {
            resourceManager = org.opennaas.extensions.sdnnetwork.Activator.getResourceManagerService();
            try {
                log.info("ResourceManager " + resourceManager.getIdentifierFromResourceName("sdnnetwork", "sdn1").getId());
            } catch (ResourceException ex) {
                Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
            }
            IResource sdnNetResource = resourceManager.listResourcesByType("sdnnetwork").get(0);
            try {
                IOFProvisioningNetworkCapability sdnCapab = (IOFProvisioningNetworkCapability) sdnNetResource.getCapabilityByInterface(IOFProvisioningNetworkCapability.class);
                /*            String resourceSdnNetworkId = sdnCapab.getMapDeviceResource(resourceName);
                 if (resourceSdnNetworkId == null) {
                 log.error("This Switch ID is not mapped to any ofswitch resource.");
                 return null;
                 }
                 */
                List<IResource> listResources = resourceManager.listResourcesByType("openflowswitch");
                for (IResource r : listResources) {
                    String name = r.getResourceDescriptor().getInformation().getName();
                    //PSNC
                    String DPID;
                    if(name.equals("s1")){
                        DPID = "00:00:64:87:88:58:f6:57";
                    }else if(name.equals("s2")){
                        DPID = "00:00:64:87:88:58:f8:57";
                    }else{
                        DPID = "00:00:00:00:00:00:00:0" + name.substring(name.length() - 1);
                    }
                    log.error(DPID);
                    String resourceId = r.getResourceIdentifier().getId();
                    log.error(resourceId);
                    sdnCapab.mapDeviceResource(DPID, resourceId);
                    /*            if (r.getResourceDescriptor().getId().equals(resourceSdnNetworkId)) {
                     resourceName = r.getResourceDescriptor().getInformation().getName();
                     log.debug("Switch name is: " + resourceName);
                     }
                     */                }
            } catch (ResourceException ex) {
                Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ActivatorException ex) {
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.ok().build();
    }
}
