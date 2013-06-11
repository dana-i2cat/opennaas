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

    private List<Route> route = new ArrayList<Route>();
    private static final long serialVersionUID = -4002472167559948067L;
    Log log = LogFactory.getLog(Table.class);
    private StringBuilder register;

    public List<Route> getRoute() {
        return route;
    }

    public void setRoute(List<Route> route) {
        this.route = route;
    }
    
    public String addRoute(Route route){
        if(!RouteExists(route)){
            this.route.add(route);
            return "Added";
        }
        return "Already exist";
    }
    
    public Boolean RouteExists(Route route){
        for (Route r : this.route){
            if(r.equals(route)){
                log.info("The route already exist!");
                return true;
            }
        }
        //log.info("Adding route...");
        return false;
    }
    
    public String getOutputPort(Route route){
        log.error("Return output port");
        for (Route r : this.route){
            if(r.equals(route)){
                log.error("OutputPort = "+ r.getSwitchInfo().getOutputPort());
                return r.getSwitchInfo().getOutputPort();
            }
        }
        return "null";
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
        if (this.route != other.route && (this.route == null || !this.route.equals(other.route))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.route != null ? this.route.hashCode() : 0);
        return hash;
    }

    public StringBuilder getRegister() {
        return register;
    }

    public void setRegister(StringBuilder register) {
        this.register = register;
    }
    
    public void addRegister(String string){
        if(this.register == null){
            this.register = new StringBuilder("Test ");
        }
        this.register.append("\n"+string);
    }

    public Switch getDestinationSwitch(String srcIp, String destIp, String originSwitch){
        for (Route r : this.route){
            if(!originSwitch.equals(r.getSwitchInfo().getMacAddress())){
                if(r.getSourceAddress().equals(srcIp) && r.getDestinationAddress().equals(destIp)){
                    return r.getSwitchInfo();
                }
            }
        }
        return null;
    }
}
