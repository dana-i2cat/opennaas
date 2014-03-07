/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opennaas.gui.nfvrouting.entities.settings;

/**
 *
 * @author josep
 */
public class Settings {
    
    private String addShellMode = "tab"; //window
    private String routingType = "static";

    public Settings() {
        this.addShellMode = "tab";
    }

    public String getAddShellMode() {
        return addShellMode;
    }

    public void setAddShellMode(String addShellMode) {
        this.addShellMode = addShellMode;
    }

    public String getRoutingType() {
        return routingType;
    }

    public void setRoutingType(String routingType) {
        this.routingType = routingType;
    }
    
}

