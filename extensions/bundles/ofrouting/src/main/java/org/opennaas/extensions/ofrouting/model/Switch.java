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
    private String ipAddress;

    public Switch(String port, String ipAddress) {
        listPorts.add(port);
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Switch other = (Switch) obj;
        if (this.numberPorts != other.numberPorts) {
            return false;
        }
        if (this.listPorts != other.listPorts && (this.listPorts == null || !this.listPorts.equals(other.listPorts))) {
            return false;
        }
        if ((this.ipAddress == null) ? (other.ipAddress != null) : !this.ipAddress.equals(other.ipAddress)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.numberPorts;
        hash = 29 * hash + (this.listPorts != null ? this.listPorts.hashCode() : 0);
        hash = 29 * hash + (this.ipAddress != null ? this.ipAddress.hashCode() : 0);
        return hash;
    }
}
