
package org.opennaas.gui.nfvrouting.entities.settings;

/**
 *
 * @author josep
 */
public class Settings {
    
    private String addShellMode = "window"; //window/tab
    private String routingType = "static"; //static/dijkstra
    private String colorDynamicRoutes = "#ccffff";//"#81DAF5"

    public Settings() {
        this.addShellMode = "window";
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

    public String getColorDynamicRoutes() {
        return colorDynamicRoutes;
    }

    public void setColorDynamicRoutes(String colorDynamicRoutes) {
        this.colorDynamicRoutes = colorDynamicRoutes;
    }
    
}

