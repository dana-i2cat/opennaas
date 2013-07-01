package org.opennaas.extensions.ofrouting.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author josep
 */
public class Switch {

    private int numberPorts;
    private List<String> listPorts = new ArrayList<String>();
    private int inputPort;
    private int outputPort;
    private String macAddress;

    public Switch(){
        
    }
    public Switch(String port, int inputPort, int outputPort, String macAddress) {
        listPorts.add(port);
        this.inputPort = inputPort;
        this.outputPort = outputPort;
        this.macAddress = macAddress;
    }

    public Switch(int inputPort, String macAddress) {
        this.inputPort = inputPort;
        this.macAddress = macAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
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
        if ((this.macAddress == null) ? (other.macAddress != null) : !this.macAddress.equals(other.macAddress)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.numberPorts;
        hash = 29 * hash + (this.macAddress != null ? this.macAddress.hashCode() : 0);
        return hash;
    }
}
