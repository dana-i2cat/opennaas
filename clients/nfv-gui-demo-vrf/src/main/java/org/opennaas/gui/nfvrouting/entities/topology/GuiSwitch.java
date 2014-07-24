package org.opennaas.gui.nfvrouting.entities.topology;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class GuiSwitch {

    private String name;

    public GuiSwitch(String name){
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
