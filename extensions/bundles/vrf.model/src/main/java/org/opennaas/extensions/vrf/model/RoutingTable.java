package org.opennaas.extensions.vrf.model;

/*
 * #%L
 * OpenNaaS :: Virtual Routing Function :: Model
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

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Josep Batallé (josep.batalle@i2cat.net)
 */
public class RoutingTable {

    private static final long serialVersionUID = -4002472167559948067L;
    Log log = LogFactory.getLog(RoutingTable.class);
    private List<VRFRoute> routeTable = new ArrayList<VRFRoute>();
    private int version;

    public RoutingTable(int version) {
        this.version = version;
        routeTable = new ArrayList<VRFRoute>();
    }

    public List<VRFRoute> getRouteTable() {
        return routeTable;
    }

    public void setRouteTable(List<VRFRoute> routeTable) {
        this.routeTable = routeTable;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public VRFRoute getRouteId(int id) {
        for (VRFRoute r : this.routeTable) {
            if (r.getId() == id) {
                return r;
            }
        }
        return null;
    }

    public String addRoute(VRFRoute route) {
        if (RouteExists(route) == 0) {
            if (this.routeTable.isEmpty()) {
                route.setId(1);
            } else {
                route.setId(routeTable.get(routeTable.size() - 1).getId() + 1);
            }
            routeTable.add(route);
            return "Added";
        }
        return "Already exist";
    }

    public Boolean removeRoute(int id) {
        for (VRFRoute r : this.routeTable) {
            if (r.getId() == id) {
                this.routeTable.remove(r);
                return true;
            }
        }
        log.info("This route no exists.");
        return false;
    }

    public void removeRoute(VRFRoute route) {
        routeTable.remove(route);
    }

    public void removeRoutes() {
        routeTable.clear();
    }

    public int RouteExists(VRFRoute route) {
        for (VRFRoute r : this.routeTable) {
            if (r.equals(route)) {
                log.error("The route "+r.getSourceAddress()+" "+r.getDestinationAddress()+" "+r.getSwitchInfo().getDPID()+" exist!");
                return r.getId();
            }
        }
        log.error("This route no exists.");
        return 0;
    }

    public int getOutputPort(int id) {
        for (VRFRoute r : this.routeTable) {
            if (r.getId() == id) {
                log.info("OutputPort = " + r.getSwitchInfo().getOutputPort());
                return r.getSwitchInfo().getOutputPort();
            }
        }
        return 0;
    }

    public int getOutputPort(VRFRoute route) {
        for (VRFRoute r : this.routeTable) {
            if (r.equals(route)) {
                log.info("OutputPort = " + r.getSwitchInfo().getOutputPort());
                return r.getSwitchInfo().getOutputPort();
            }
        }
        return 0;
    }

    public List<VRFRoute> getListRoutes(VRFRoute route, L2Forward srcSwInfo, L2Forward destSwInfo) {
        List<VRFRoute> subnetList = new ArrayList<VRFRoute>();
        VRFRoute route2 = new VRFRoute();
        route2.setSourceAddress(route.getDestinationAddress());
        route2.setDestinationAddress(route.getSourceAddress());
        for (VRFRoute r : this.getRouteTable()) {
            if (!r.getSwitchInfo().getDPID().equals(srcSwInfo.getDPID())) {
                if (r.equalsOtherRoutes(route)) {
                    subnetList.add(r);
                } else if (r.equalsOtherRoutes(route2)) {
                    subnetList.add(r);
                }
            }
        }
        log.info("Returning all Routes.");
        return subnetList;
    }
    
    public List<VRFRoute> getListRoutes(VRFRoute route) {
        List<VRFRoute> subnetList = new ArrayList<VRFRoute>();
        VRFRoute route2 = new VRFRoute();
        route2.setSourceAddress(route.getDestinationAddress());
        route2.setDestinationAddress(route.getSourceAddress());
        for (VRFRoute r : this.getRouteTable()) {
                if (r.equalsOtherRoutes(route)) {
                    subnetList.add(r);
                } else if (r.equalsOtherRoutes(route2)) {
                    subnetList.add(r);
                }
        }
        log.info("Returning all Routes.");
        return subnetList;
    }
}
