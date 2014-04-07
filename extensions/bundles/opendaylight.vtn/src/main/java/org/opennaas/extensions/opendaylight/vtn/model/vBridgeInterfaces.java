package org.opennaas.extensions.opendaylight.vtn.model;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class vBridgeInterfaces {

    private String if_name;
    private String description;
    private String adminstatus;

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

}
