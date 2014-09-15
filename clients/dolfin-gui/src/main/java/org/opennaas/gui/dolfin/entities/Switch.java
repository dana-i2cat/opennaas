package org.opennaas.gui.dolfin.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Josep Batallé (josep.batalle@i2cat.net)
 */
public class Switch {

    private String dpid;
    private List<String> ports =  new ArrayList<String>();

    public Switch(String dpid, List<String> ports) {
        this.dpid = dpid;
        this.ports = ports;       
    }

    public Switch() {
        
    }

    public String getDpid() {
        return dpid;
    }

    public void setDpid(String dpid) {
        this.dpid = dpid;
    }

    public List<String> getPorts() {
        return ports;
    }

    public void setPorts(List<String> ports) {
        this.ports = ports;
    }
}
