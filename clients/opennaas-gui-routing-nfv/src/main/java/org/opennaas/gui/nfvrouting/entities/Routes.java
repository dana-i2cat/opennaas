package org.opennaas.gui.nfvrouting.entities;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Josep Batallé (josep.batalle@i2cat.net)
 */
public class Routes {
    
    private static final long serialVersionUID = -4002472167559948067L;
    private List<Route> routeIPv4 = new ArrayList<Route>();
    private List<Route> routeIPv6 = new ArrayList<Route>();
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
}
