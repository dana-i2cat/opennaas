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
    private String inputPort;
    private String outputPort;
    private String macAddress;

    public Switch(String port,String inputPort, String outputPort, String macAddress) {
        listPorts.add(port);
        this.inputPort = inputPort;
        this.outputPort = outputPort;
        this.macAddress = macAddress;
    }
    
    public Switch(String inputPort, String macAddress) {
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

    public String getInputPort() {
        return inputPort;
    }

    public void setInputPort(String inputPort) {
        this.inputPort = inputPort;
    }

    public String getOutputPort() {
        return outputPort;
    }

    public void setOutputPort(String outputPort) {
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
        if ((this.inputPort == null) ? (other.inputPort != null) : !this.inputPort.equals(other.inputPort)) {
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
