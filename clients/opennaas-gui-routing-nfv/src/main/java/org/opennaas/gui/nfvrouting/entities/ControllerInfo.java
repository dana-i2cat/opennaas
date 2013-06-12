package org.opennaas.gui.nfvrouting.entities;

/**
 *
 * @author josep
 */
public class ControllerInfo {
    
    private String controllerIp;
    private String controllerPort;
    private String macAddress;

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
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    
}
