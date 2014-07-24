
package org.opennaas.gui.nfvrouting.entities.topology;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class GUITopology {
    
    private List<Switch> switches = new ArrayList<Switch>();

    public List<Switch> getSwitches() {
        return switches;
    }

    public void setSwitches(List<Switch> switches) {
        this.switches = switches;
    }
    
}
