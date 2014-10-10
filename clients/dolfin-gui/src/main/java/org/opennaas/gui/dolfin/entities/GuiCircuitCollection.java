package org.opennaas.gui.dolfin.entities;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class GuiCircuitCollection {
    
    private List<GuiCircuits> circuits = new ArrayList<GuiCircuits>();

    public List<GuiCircuits> getCircuits() {
        return circuits;
    }

    public void setCircuits(List<GuiCircuits> circuits) {
        this.circuits = circuits;
    }
    
     
}
