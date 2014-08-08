
package org.opennaas.gui.nfvrouting.entities.topology;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class SwitchCollection {
    
    private List<GuiSwitch> switches = new ArrayList<GuiSwitch>();

    public List<GuiSwitch> getSwitches() {
        return switches;
    }

    public void setSwitches(List<GuiSwitch> switches) {
        this.switches = switches;
    }
    
}
