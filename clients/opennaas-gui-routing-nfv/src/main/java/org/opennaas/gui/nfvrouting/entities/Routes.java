/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opennaas.gui.nfvrouting.entities;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.extensions.ofrouting.model.RouteSubnet;

/**
 *
 * @author Josep Batalle
 */
public class Routes {
    

    private List<Route> routeIPv4 = new ArrayList<Route>();
    private List<Route> routeIPv6 = new ArrayList<Route>();
    private List<RouteSubnet> routeSubnet = new ArrayList<RouteSubnet>();
    private static final long serialVersionUID = -4002472167559948067L;
    Log log = LogFactory.getLog(Routes.class);

    public List<Route> getRoute(int version) {
        if (version == 4) {
            return routeIPv4;
        } else if (version == 6) {
            return routeIPv6;
        }
        return new ArrayList<Route>();
    }

    public List<Route> getRouteIPv4() {
        return routeIPv4;
    }

    public void setRouteIPv4(List<Route> routeIPv4) {
        this.routeIPv4 = routeIPv4;
    }

    public List<Route> getRouteIPv6() {
        return routeIPv6;
    }

    public void setRouteIPv6(List<Route> routeIPv6) {
        this.routeIPv6 = routeIPv6;
    }

    public List<RouteSubnet> getRouteSubnet() {
        return routeSubnet;
    }

    public void setRouteSubnet(List<RouteSubnet> routeSubnet) {
        this.routeSubnet = routeSubnet;
    }

    
}
