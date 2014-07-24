package org.opennaas.gui.nfvrouting.entities.topology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class GuiTopology {

    private List<String> nodes = new ArrayList<String>();
    private List<GuiLink> links = new ArrayList<GuiLink>();
    private Map<String, String> possibleHosts = new HashMap<String, String>();

    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
    }

    public List<GuiLink> getLinks() {
        return links;
    }

    public void setLinks(List<GuiLink> links) {
        this.links = links;
    }

    public Map<String, String> getPosibleHosts() {
        return possibleHosts;
    }

    public void setPosibleHosts(Map<String, String> possibleHosts) {
        this.possibleHosts = possibleHosts;
    }

}
