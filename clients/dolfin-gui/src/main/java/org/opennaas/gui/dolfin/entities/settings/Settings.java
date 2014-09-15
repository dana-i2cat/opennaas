package org.opennaas.gui.dolfin.entities.settings;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class Settings {
    
    private String addShellMode = "window"; //window/tab
    private String colorDynamicRoutes = "#ccffff";//"#81DAF5"
    private String updateTime = "5";//seconds

    public Settings() {
        this.addShellMode = "window";
    }

    public String getAddShellMode() {
        return addShellMode;
    }

    public void setAddShellMode(String addShellMode) {
        this.addShellMode = addShellMode;
    }

    public String getColorDynamicRoutes() {
        return colorDynamicRoutes;
    }

    public void setColorDynamicRoutes(String colorDynamicRoutes) {
        this.colorDynamicRoutes = colorDynamicRoutes;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
    
}

