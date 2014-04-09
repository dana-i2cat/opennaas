package org.opennaas.extensions.opendaylight.vtn.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class OpenDaylightvBridge {

    private String vbr_name;
    private String controller_id;
    private String description;
    private String domain_id;
    private List<vBridgeInterfaces> iface = new ArrayList<vBridgeInterfaces>();

    public String getVbr_name() {
        return vbr_name;
    }

    public void setVbr_name(String vbr_name) {
        this.vbr_name = vbr_name;
    }

    public String getController_id() {
        return controller_id;
    }

    public void setController_id(String controller_id) {
        this.controller_id = controller_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDomain_id() {
        return domain_id;
    }

    public void setDomain_id(String domain_id) {
        this.domain_id = domain_id;
    }

    public List<vBridgeInterfaces> getIface() {
        return iface;
    }

    public void setIface(List<vBridgeInterfaces> iface) {
        this.iface = iface;
    }

    @Override
    public int hashCode() {
        int hash = 3;
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
        final OpenDaylightvBridge other = (OpenDaylightvBridge) obj;
        if ((this.vbr_name == null) ? (other.vbr_name != null) : !this.vbr_name.equals(other.vbr_name)) {
            return false;
        }
        if ((this.controller_id == null) ? (other.controller_id != null) : !this.controller_id.equals(other.controller_id)) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if ((this.domain_id == null) ? (other.domain_id != null) : !this.domain_id.equals(other.domain_id)) {
            return false;
        }
        if (this.iface != other.iface && (this.iface == null || !this.iface.equals(other.iface))) {
            return false;
        }
        return true;
    }

}
