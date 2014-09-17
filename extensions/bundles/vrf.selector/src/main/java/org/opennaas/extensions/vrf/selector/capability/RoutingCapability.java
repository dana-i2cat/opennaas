package org.opennaas.extensions.vrf.selector.capability;

/*
 * #%L
 * OpenNaaS :: Virtual Routing Function :: Selector
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.List;
import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.extensions.vrf.dijkstraroute.capability.DijkstraRoutingCapability;
import org.opennaas.extensions.vrf.staticroute.capability.routing.IStaticRoutingCapability;

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
        log.error("SELECTOR: GET ROUTE: "+ipSource + " " + ipDest + " " + switchDPID + " " + inputPort);
        Response response = null;
        if (mode.equals("static")) {
            response = service.getRoute(ipSource, ipDest, switchDPID, inputPort, proactive);
        } else if (mode.equals("dijkstra")) {
            DijkstraRoutingCapability dynamicRoute = new DijkstraRoutingCapability();
            response = dynamicRoute.getDynamicRoute(ipSource, ipDest, switchDPID, inputPort);
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

}