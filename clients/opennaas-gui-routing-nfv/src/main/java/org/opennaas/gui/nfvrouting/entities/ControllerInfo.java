package org.opennaas.gui.nfvrouting.entities;

/**
 *
 * @author Josep Batallé (josep.batalle@i2cat.net)
 */
public class ControllerInfo {
    
    private String controllerIp;
    private String controllerPort;
    private String dpid;

    public String getControllerIp() {
        return controllerIp;
    }

    public void setControllerIp(String controllerIp) {
        this.controllerIp = controllerIp;
    }

    public String getControllerPort() {
        return controllerPort;
    }

    public void setControllerPort(String controllerPort) {
        this.controllerPort = controllerPort;
    }

    public String getMacAddress() {
        return dpid;
    }

    public void setMacAddress(String dpid) {
        this.dpid = dpid;
    }
    
}
