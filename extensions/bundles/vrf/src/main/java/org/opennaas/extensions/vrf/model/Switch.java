package org.opennaas.extensions.vrf.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Josep Batallé (josep.batalle@i2cat.net)
 */
public class Switch {

    private int numberPorts;//not used
    private List<String> listPorts = new ArrayList<String>();//not used
    private int inputPort;
    private int outputPort;
    private String dpid;

    public Switch(){
        
    }
    public Switch(String port, int inputPort, int outputPort, String macAddress) {
        listPorts.add(port);
        this.inputPort = inputPort;
        this.outputPort = outputPort;
        this.dpid = macAddress;
    }

    public Switch(int inputPort, String macAddress) {
        this.inputPort = inputPort;
        this.dpid = macAddress;
    }

    public String getMacAddress() {
        return dpid;
    }

    public void setMacAddress(String macAddress) {
        this.dpid = macAddress;
    }

    public List<String> getListPorts() {
        return listPorts;
    }

    public void setListPorts(List<String> listPorts) {
        this.listPorts = listPorts;
    }

    public int getNumberPorts() {
        return numberPorts;
    }

    public void setNumberPorts(int numberPorts) {
        this.numberPorts = numberPorts;
    }

    public int getInputPort() {
        return inputPort;
    }

    public void setInputPort(int inputPort) {
        this.inputPort = inputPort;
    }

    public int getOutputPort() {
        return outputPort;
    }

    public void setOutputPort(int outputPort) {
        this.outputPort = outputPort;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Switch other = (Switch) obj;
        if (this.inputPort != other.inputPort) {
            return false;
        }
        if (this.outputPort != other.outputPort) {
            return false;
        }
        if ((this.dpid == null) ? (other.dpid != null) : !this.dpid.equals(other.dpid)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.numberPorts;
        hash = 29 * hash + (this.dpid != null ? this.dpid.hashCode() : 0);
        return hash;
    }
}
