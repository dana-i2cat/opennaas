package org.opennaas.extensions.vrf.selector.capability;

import java.util.List;
import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
        Response response = null;
        if( mode.equals("static") ){
            response = service.getRoute(ipSource, ipDest, switchDPID, inputPort, proactive);
        } else if(mode.equals("dijkstra")){
            DijkstraRoutingCapability dynamicRoute = new DijkstraRoutingCapability();
            response = dynamicRoute.getDynamicRoute(ipSource, ipDest);
        }
        return response;
    }

    @Override
    public Response setSelectorMode(String mode) {
        if( mode.equals("static") ){
            this.mode = "static";
        } else if( mode.equals("dijkstra") ){
            this.mode = "dijkstra";
        }
        else{
            return Response.serverError().entity("Incorrect mode. Possible modes: static, dijkstra. Remain active mode: "+mode).build();
        }
        return Response.ok().build();
    }
}
