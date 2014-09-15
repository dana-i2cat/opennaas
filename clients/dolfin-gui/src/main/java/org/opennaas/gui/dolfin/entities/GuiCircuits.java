package org.opennaas.gui.dolfin.entities;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class GuiCircuits {

    private List<GuiSwitch> guiSwitches = new ArrayList<GuiSwitch>();

    public List<GuiSwitch> getGuiSwitches() {
        return guiSwitches;
    }

    public void setGuiSwitches(List<GuiSwitch> listSwitches) {
        this.guiSwitches = listSwitches;
    }

}
