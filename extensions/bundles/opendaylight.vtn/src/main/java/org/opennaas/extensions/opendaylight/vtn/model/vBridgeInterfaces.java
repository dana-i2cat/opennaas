package org.opennaas.extensions.opendaylight.vtn.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class vBridgeInterfaces {

    private String if_name;
    private String description;
    private String adminstatus;
    private List<PortMap> portMaps = new ArrayList<PortMap>();

    public vBridgeInterfaces(String if_name) {
        this.if_name = if_name;
    }

    public vBridgeInterfaces() {
    }

    public String getIf_name() {
        return if_name;
    }

    public void setIf_name(String if_name) {
        this.if_name = if_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdminstatus() {
        return adminstatus;
    }

    public void setAdminstatus(String adminstatus) {
        this.adminstatus = adminstatus;
    }

    public List<PortMap> getPortMaps() {
        return portMaps;
    }

    public void setPortMaps(List<PortMap> portMaps) {
        this.portMaps = portMaps;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final vBridgeInterfaces other = (vBridgeInterfaces) obj;
        if ((this.if_name == null) ? (other.if_name != null) : !this.if_name.equals(other.if_name)) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if ((this.adminstatus == null) ? (other.adminstatus != null) : !this.adminstatus.equals(other.adminstatus)) {
            return false;
        }
        return true;
    }
}
