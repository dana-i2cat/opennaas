package org.opennaas.extensions.ofrouting.model;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author josep
 */
public class Table {

    private List<Route> routeIPv4 = new ArrayList<Route>();
    private List<Route> routeIPv6 = new ArrayList<Route>();
    private List<RouteSubnet> routeSubnet = new ArrayList<RouteSubnet>();
    private static final long serialVersionUID = -4002472167559948067L;
    Log log = LogFactory.getLog(Table.class);

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

    public String addRoute(Route route, int version) {
        log.info("Adding route with version "+version);
        if (!RouteExists(route, version)) {
            if (version == 4) {
                if(this.routeIPv4.isEmpty())
                    route.setId(1);
                else
                    route.setId(this.routeIPv4.get(this.routeIPv4.size()-1).getId()+1);
                this.routeIPv4.add(route);
                return "Added";
            } else if (version == 6) {
                if(this.routeIPv6.isEmpty())
                    route.setId(1);
                else
                    route.setId(this.routeIPv6.get(this.routeIPv6.size()-1).getId()+1);
                this.routeIPv6.add(route);
                return "Added";
            }
        }
        return "Already exist";
    }

    public String addRouteSub(RouteSubnet route) {
        if (!RouteExistsSubnet(route)) {
            if(this.routeSubnet.isEmpty())
                    route.setId(1);
                else
                route.setId(this.routeSubnet.get(this.routeSubnet.size()-1).getId()+1);
            this.routeSubnet.add(route);
            return "Added";
        }
        return "Already exist";
    }
    
    public String delRoute(Route route, int version) {
        log.info("Deleting route with version "+version);
        if (RouteExists(route, version)) {
            if (version == 4) {
                this.routeIPv4.remove(route);
                return "Removed";
            } else if (version == 6) {
                this.routeIPv6.remove(route);
                return "Added";
            }
        }
        return "Already exist";
    }
    
    public String delRouteSub(RouteSubnet route) {
        if (RouteExistsSubnet(route)) {
            this.routeSubnet.remove(route);
            return "Added";
        }
        return "Already exist";
    }
    
    

    public Boolean RouteExists(Route route, int version) {
        if (version == 4) {
            for (Route r : this.routeIPv4) {
                if (r.equals(route)) {
                    log.info("The route already exist!");
                    return true;
                }
            }
        } else if (version == 6) {
            for (Route r : this.routeIPv6) {
                if (r.equals(route)) {
                    log.info("The route already exist!");
                    return true;
                }
            }
        }
        log.error("This route no exists.");
        //log.info("Adding route...");
        return false;
    }
    
    public List<RouteSubnet> otherRouteExists(RouteSubnet route, Switch srcInf, Switch dstInf) {
        List<RouteSubnet> subnetList = new ArrayList<RouteSubnet>();
        RouteSubnet route2 = new RouteSubnet();
        route2.setSourceSubnet(route.getDestSubnet());
        route2.setDestSubnet(route.getSourceSubnet());
            for (RouteSubnet r : this.routeSubnet) {

                if (r.equalsOtherSubRoute(route) && !r.getSwitchInfo().getMacAddress().equals(srcInf.getMacAddress()) && !r.getSwitchInfo().getMacAddress().equals(dstInf.getMacAddress())) {
                    log.error("Match other RouteSubnet");
                    subnetList.add(r);
//                    return true;
                }else if(r.equalsOtherSubRoute(route2) && !r.getSwitchInfo().getMacAddress().equals(srcInf.getMacAddress()) && !r.getSwitchInfo().getMacAddress().equals(dstInf.getMacAddress())){
                     log.error("Match other RouteSubnet");
                    subnetList.add(r);
                }
            }
        
        log.error("Returning List of Subnets");
        //log.info("Adding route...");
        return subnetList;
    }

    public Boolean RouteExistsSubnet(RouteSubnet route) {
        for (RouteSubnet r : this.routeSubnet) {
            if (r.equals(route)) {
                log.info("The route already exist!");
                return true;
            }
        }
        log.error("This subnetwork does not exist!");
        //log.info("Adding route...");
        return false;
    }

    public int getOutputPort(Route route, int version) {
        log.info("Return output port from route");
        if (version == 4) {        
            for (Route r : this.routeIPv4) {
                if (r.equals(route)) {
                    log.info("OutputPort ipv4 = " + r.getSwitchInfo().getOutputPort());
                    return r.getSwitchInfo().getOutputPort();
                }
            }
        } else  if (version == 6) {   
            for (Route r : this.routeIPv6) {
                if (r.equals(route)) {
                    log.info("OutputPort ipv6 = " + r.getSwitchInfo().getOutputPort());
                    return r.getSwitchInfo().getOutputPort();
                }
            }
        }
        return 0;
    }

    public int getOutputPort(RouteSubnet route) {
        log.info("Return output port from subnetwork");
        for (RouteSubnet r : this.routeSubnet) {
            if (r.equals(route)) {
                log.info("OutputPort = " + r.getSwitchInfo().getOutputPort());
                return r.getSwitchInfo().getOutputPort();
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Table other = (Table) obj;
        if (this.routeIPv4 != other.routeIPv4 && (this.routeIPv4 == null || !this.routeIPv4.equals(other.routeIPv4))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.routeIPv4 != null ? this.routeIPv4.hashCode() : 0);
        return hash;
    }

    public Switch getDestinationSwitch(String srcIp, String destIp, String originSwitch, int version) {
        if(version == 4){
            for (Route r : this.routeIPv4) {
                //Using subnet, this lines is comment. The route table contain less number of entries
    //            if(!originSwitch.equals(r.getSwitchInfo().getMacAddress())){
                if (InetAddresses.toAddrString(r.getSourceAddress()).equals(srcIp) && InetAddresses.toAddrString(r.getDestinationAddress()).equals(destIp)) {
                    log.info("GetDestSwitch with src "+srcIp +", dst "+destIp +", dins "+r.getSwitchInfo().getMacAddress());
                    return r.getSwitchInfo();
                }
    //            }
            }
        }else if(version == 6){
            for (Route r : this.routeIPv6) {
            //Using subnet, this lines is comment. The route table contain less number of entries
//            if(!originSwitch.equals(r.getSwitchInfo().getMacAddress())){
            if (InetAddresses.toAddrString(r.getSourceAddress()).equals(srcIp) && InetAddresses.toAddrString(r.getDestinationAddress()).equals(destIp)) {
                return r.getSwitchInfo();
            }
//            }
        }
        }
        return null;
    }

    public Switch getDestinationSwitchfromSub(String srcIp, String destIp, String originSwitch) {
        for (RouteSubnet r : this.routeSubnet) {
            if (!originSwitch.equals(r.getSwitchInfo().getMacAddress())) {
                if (r.getSourceSubnet().getIpAddressString().equals(srcIp) && r.getDestSubnet().getIpAddressString().equals(destIp)) {
                    return r.getSwitchInfo();
                }
            }
        }
        return null;
    }
    
    public Boolean removeRoute(int id, int version) {
        if (version == 4) {
            for (Route r : this.routeIPv4) {
                if (r.getId() == id) {
                    this.routeIPv4.remove(r);
                    return true;
                }
            }
        }else if (version == 6) {
            for (Route r : this.routeIPv6) {
                if (r.getId() == id) {
                    this.routeIPv6.remove(r);
                    return true;
                }
            }
        }
        log.error("This route no exists.");
        //log.info("Adding route...");
        return false;
    }

    public Route getRouteId(int id) {
        for (Route r : this.routeIPv4) {
                if (r.getId() == id) {
                    return r;
                }
            }
        return null;
    }
}
