package org.opennaas.extensions.vrf.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;

/**
 *
 * @author Josep Batallé (josep.batalle@i2cat.net)
 */
public class VRFModel implements IModel{
    
    private RoutingTable ipv4;
    private RoutingTable ipv6;
    private Map<String, String> switchController = new HashMap<String, String>();

    @Override
    public List<String> getChildren() {
        return new ArrayList<String>();
    }

    @Override
    public String toXml() throws SerializationException {
        return ObjectSerializer.toXml(this);
    }

    public RoutingTable getTable(int version) {
        if (version == 4)
            return ipv4;
        else if (version == 6)
            return ipv6;
        return null;
    }

    public void setTable(RoutingTable table, int version) {
        if (version == 4)
            this.ipv4 = table;
        else if (version == 6)
            this.ipv6 = table;
    }

   public Map<String, String> getSwitchController() {
        return switchController;
    }

    public void setSwitchController(Map<String, String> switchController) {
        this.switchController = switchController;
    }

    public RoutingTable getIpv4() {
        return ipv4;
    }

    public void setIpv4(RoutingTable ipv4) {
        this.ipv4 = ipv4;
    }

    public RoutingTable getIpv6() {
        return ipv6;
    }

    public void setIpv6(RoutingTable ipv6) {
        this.ipv6 = ipv6;
    }
}
