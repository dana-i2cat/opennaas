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
import org.opennaas.extensions.genericnetwork.model.topology.Host;
import org.opennaas.extensions.genericnetwork.model.topology.Link;
import org.opennaas.extensions.genericnetwork.model.topology.NetworkElement;
import org.opennaas.extensions.genericnetwork.model.topology.Port;
import org.opennaas.extensions.genericnetwork.model.topology.Switch;
import org.opennaas.extensions.genericnetwork.model.topology.Topology;
import org.opennaas.gui.nfvrouting.entities.topology.GuiSwitch;
import org.opennaas.gui.nfvrouting.entities.topology.GuiLink;
import org.opennaas.gui.nfvrouting.entities.topology.GuiTopology;
import org.opennaas.gui.nfvrouting.entities.topology.Node;
import org.opennaas.gui.nfvrouting.entities.topology.SwitchCollection;

/**
 * This class provides the methods to convert OpenNaaS beans to Routing GUI
 * beans
 *
 * @author Josep Batallé (@i2cat)
 */
public class OpennaasBeanUtils {

    private static final Logger log = Logger.getLogger(OpennaasBeanUtils.class);

    public static SwitchCollection getTopology(Topology topology) {
        SwitchCollection ofTopo = new SwitchCollection();
        List<GuiSwitch> sws = new ArrayList<GuiSwitch>();
        Set<NetworkElement> nEs = topology.getNetworkElements();
        GuiSwitch sw;
        for (NetworkElement nE : nEs) {
            log.error(nE.getId());
            sw = new GuiSwitch();
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

    public static String getSwitchOfPort(String port, SwitchCollection ofTopo) {
        log.error("GET Switch of Prot");
        log.error("GET Switch of Prot" + ofTopo.getSwitches().size());
        for (GuiSwitch sw : ofTopo.getSwitches()) {
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
        List<Node> nodes = new ArrayList<Node>();
        List<GuiLink> links = new ArrayList<GuiLink>();
        SwitchCollection of = getTopology(topology);
        Node node;
        int id=0;
        for (NetworkElement nE : topology.getNetworkElements()) {
            node = new Node();
            node.setId(id);
            if (nE instanceof Host) {
                node.setName(nE.getId().split("host:")[1]);
                node.setIp(((Host) nE).getIp());
                node.setType("host");
            } else if (nE instanceof Switch) {
                node.setName(nE.getId().split("openflowswitch:")[1]);
                node.setDpid(((Switch) nE).getDpid());
                node.setType("switch");
            }
            id++;
            nodes.add(node);
        }
        for (Link link : topology.getLinks()) {
            String source = getSwitchOfPort(link.getSrcPort().getId(), of);
            String target = getSwitchOfPort(link.getDstPort().getId(), of);
            int idenSrc = getIdOfNode(nodes, source);
            int idenDst = getIdOfNode(nodes, target);
            log.error("Id: "+idenSrc);
            log.error("Id: "+idenDst);
            String srcPort = "0";
            String dstPort = "0";
            if (getNodeType(nodes, idenSrc).equals("switch")) {
                srcPort = getPhysicalPortId(topology, link.getSrcPort().getId());
            }else{
                log.error(nodes.get(idenSrc).getId());
                nodes.get(idenSrc).setSW(nodes.get(idenDst).getName());
                nodes.get(idenSrc).setPort(getPhysicalPortId(topology, link.getDstPort().getId()));
            }
            if (getNodeType(nodes, idenDst).equals("switch")) {
                dstPort = getPhysicalPortId(topology, link.getDstPort().getId());
            }else{
                nodes.get(idenDst).setSW(nodes.get(idenSrc).getName());
                nodes.get(idenDst).setPort(getPhysicalPortId(topology, link.getSrcPort().getId()));
            }
            links.add(new GuiLink("path"+idenSrc+idenDst, idenSrc, idenDst, Integer.parseInt(srcPort), Integer.parseInt(dstPort)));
        }
        guiTop.setNodes(nodes);
        guiTop.setLinks(links);

        return guiTop;
    }

    private static int getIdOfNode(List<Node> nodes, String name){
        log.error("Name: "+name);
        for (Node n : nodes) {
            if(name.split("host:").length > 1){
                if(n.getName().equals(name.split("host:")[1]))
                    return n.getId();
            }else if(name.split("openflowswitch:").length > 1){
                if(n.getName().equals(name.split("openflowswitch:")[1]))
                    return n.getId();
            }
        }
        log.error("No return");
        return 0;
    }
    
    private static String getNodeType(List<Node> nodes, int id){
        log.error("GetNode id : "+id);
        for (Node n : nodes) {
            if(n.getId() == id)
                return n.getType();
        }
        return null;
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
        SwitchCollection ofTopo = getTopology(topology);
        log.error(ofTopo.getSwitches().size());
        for (GuiSwitch sw : ofTopo.getSwitches()) {
            for (String port : sw.getPorts()) {
                used = false;
                for (GuiLink link : guiTop.getLinks()) {
                    if (String.valueOf(link.getSrcPort()).equals(port) || String.valueOf(link.getDstPort()).equals(port)) {
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

    private static String getPhysicalPortId(Topology topology, String portId){
        log.error("Port Id: "+portId);
        return topology.getNetworkDevicePortIdsMap().get(portId).getDevicePortId();
    }
}
