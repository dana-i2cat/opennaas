package org.opennaas.gui.nfvrouting.utils.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.opennaas.extensions.genericnetwork.model.topology.Link;
import org.opennaas.extensions.genericnetwork.model.topology.NetworkElement;
import org.opennaas.extensions.genericnetwork.model.topology.Port;
import org.opennaas.extensions.genericnetwork.model.topology.Topology;
import org.opennaas.gui.nfvrouting.entities.topology.GuiLink;
import org.opennaas.gui.nfvrouting.entities.topology.GuiTopology;
import org.opennaas.gui.nfvrouting.entities.topology.GUITopology;
import org.opennaas.gui.nfvrouting.entities.topology.Switch;

/**
 * This class provides the methods to convert OpenNaaS beans to Routing GUI
 * beans
 *
 * @author Josep Batallé (@i2cat)
 */
public class OpennaasBeanUtils {

    private static final Logger log = Logger.getLogger(OpennaasBeanUtils.class);

    public static GUITopology getTopology(Topology topology) {
        GUITopology ofTopo = new GUITopology();
        List<Switch> sws = new ArrayList<Switch>();
        Set<NetworkElement> nEs = topology.getNetworkElements();
        Switch sw;
        for (NetworkElement nE : nEs) {
            log.error(nE.getId());
            sw = new Switch();
            sw.setDpid(nE.getId());

            for (Port port : nE.getPorts()) {
                log.error("ports " + port.getId());
                sw.getPorts().add(port.getId());
            }
            sws.add(sw);
        }
        ofTopo.setSwitches(sws);
        log.error(ofTopo.getSwitches().size());
        return ofTopo;
    }

    public static String getSwitchOfPort(String port, GUITopology ofTopo) {
        log.error("GET Switch of Prot");
        log.error("GET Switch of Prot" + ofTopo.getSwitches().size());
        for (Switch sw : ofTopo.getSwitches()) {
            log.error("Switch " + sw.getDpid());
            for (String p : sw.getPorts()) {
                log.error("GEtWI " + p);
                log.error(port);
                if (p.equals(port)) {
                    return sw.getDpid();
                }
            }
        }
        return "no exist";
    }

    public static GuiTopology convertONTopologyToGuiTopology(Topology topology) {
        GuiTopology guiTop = new GuiTopology();
        List<String> nodes = new ArrayList<String>();
        List<GuiLink> links = new ArrayList<GuiLink>();
        GUITopology of = getTopology(topology);
        for (NetworkElement nE : topology.getNetworkElements()) {
            nodes.add(nE.getId());
        }
        for (Link link : topology.getLinks()) {
            String source = getSwitchOfPort(link.getSrcPort().getId(), of);
            String target = getSwitchOfPort(link.getDstPort().getId(), of);
            links.add(new GuiLink(source, target, link.getSrcPort().getId(), link.getDstPort().getId()));
        }
        guiTop.setNodes(nodes);
        guiTop.setLinks(links);

        return guiTop;
    }

    /**
     * In the case that a port is not connected to any other switch, its
     * possible taht the endpoint should be a host
     *
     * @param topology
     * @param guiTop
     * @return
     */
    public static Map<String, String> findUnusedPorts(Topology topology, GuiTopology guiTop) {
        Boolean used;
        Map<String, String> map = new HashMap<String, String>();
        GUITopology ofTopo = getTopology(topology);
        log.error(ofTopo.getSwitches().size());
        for (Switch sw : ofTopo.getSwitches()) {
            for (String port : sw.getPorts()) {
                used = false;
                for (GuiLink link : guiTop.getLinks()) {
                    if (link.getSrcPort().equals(port) || link.getDstPort().equals(port)) {
                        used = true;
                        break;
                    }
                }
                if (!used) {
                    map.put(sw.getDpid(), port);
                }
            }
        }
        return map;
    }

    public static String mapperObjectsToJSON(Object obj) {
        String response = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            response = mapper.writeValueAsString(obj);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(OpennaasBeanUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

}
