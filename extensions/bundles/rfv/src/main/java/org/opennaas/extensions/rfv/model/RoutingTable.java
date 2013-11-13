package org.opennaas.extensions.rfv.model;

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
    private List<Route> routeTable = new ArrayList<Route>();
    private int version;

    public RoutingTable(int version) {
        this.version = version;
        routeTable = new ArrayList<Route>();
    }

    public List<Route> getRouteTable() {
        return routeTable;
    }

    public void setRouteTable(List<Route> routeTable) {
        this.routeTable = routeTable;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String addRoute(Route route) {
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

    public void removeRoute(Route route) {
        routeTable.remove(route);
    }
    
    public void removeRoutes() {
        routeTable.clear();
    }
    
    public int RouteExists(Route route) {
        for (Route r : this.routeTable) {
            if (r.equals(route)) {
                log.info("The route exist!");
                return r.getId();
            }
        }
        log.error("This route no exists.");
        return 0;
    }

    public int getOutputPort(int id) {
        for (Route r : this.routeTable) {
            if (r.getId() == id) {
                log.info("OutputPort = " + r.getSwitchInfo().getOutputPort());
                return r.getSwitchInfo().getOutputPort();
            }
        }
        return 0;
    }

    public int getOutputPort(Route route) {
        for (Route r : this.routeTable) {
            if (r.equals(route)) {
                log.info("OutputPort = " + r.getSwitchInfo().getOutputPort());
                return r.getSwitchInfo().getOutputPort();
            }
        }
        return 0;
    }

    public Switch getDestinationSwitch(String srcIp, String destIp, Switch Switch) {
        for (Route r : this.routeTable) {
            if (r.getDestinationAddress().equals(destIp)) {
                log.info("GetDestSwitch with src " + srcIp + ", dst " + destIp + ", dins " + r.getSwitchInfo().getMacAddress());
                return r.getSwitchInfo();
            }
        }
        return null;
    }

    public Route getRouteId(int id) {
        for (Route r : this.routeTable) {
            if (r.getId() == id) {
                return r;
            }
        }
        return null;
    }

    public Boolean removeRoute(int id) {
        for (Route r : this.routeTable) {
            if (r.getId() == id) {
                this.routeTable.remove(r);
                return true;
            }
        }
        log.error("This route no exists.");
        return false;
    }

    public List<Route> otherRoutesExists(Route route, Switch srcSw, Switch dstSw) {
        List<Route> subnetList = new ArrayList<Route>();
        Route route2 = new Route();
        route2.setSourceAddress(route.getDestinationAddress());
        route2.setDestinationAddress(route.getSourceAddress());
        for (Route r : this.getRouteTable()) {
            if (r.equalsOtherSubRoute(route) && !r.getSwitchInfo().getMacAddress().equals(srcSw.getMacAddress()) && !r.getSwitchInfo().getMacAddress().equals(dstSw.getMacAddress())) {
                log.error("Match other RouteSubnet");
                subnetList.add(r);
            } else if (r.equalsOtherSubRoute(route2) && !r.getSwitchInfo().getMacAddress().equals(srcSw.getMacAddress()) && !r.getSwitchInfo().getMacAddress().equals(dstSw.getMacAddress())) {
                log.error("Match other RouteSubnet");
                subnetList.add(r);
            }
        }
        log.error("Returning List of Subnets");
        return subnetList;
    }
}
