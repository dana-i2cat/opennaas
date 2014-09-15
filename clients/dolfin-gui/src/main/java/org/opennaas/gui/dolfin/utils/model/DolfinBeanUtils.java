package org.opennaas.gui.dolfin.utils.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.api.CircuitCollection;
import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;
import org.opennaas.extensions.genericnetwork.model.circuit.NetworkConnection;
import org.opennaas.extensions.genericnetwork.model.topology.Link;
import org.opennaas.extensions.genericnetwork.model.topology.NetworkElement;
import org.opennaas.extensions.genericnetwork.model.topology.Port;
import org.opennaas.extensions.genericnetwork.model.topology.Topology;
import org.opennaas.gui.dolfin.entities.GuiCircuits;
import org.opennaas.gui.dolfin.entities.GuiCircuitCollection;
import org.opennaas.gui.dolfin.entities.GuiLink;
import org.opennaas.gui.dolfin.entities.GuiSwitch;
import org.opennaas.gui.dolfin.entities.GuiTopology;
import org.opennaas.gui.dolfin.entities.OfertieTopology;
import org.opennaas.gui.dolfin.entities.Switch;

/**
 * This class provides the methods to convert OpenNaaS beans to Ofertie GUI
 * beans
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class DolfinBeanUtils {

    private static final Logger log = Logger.getLogger(DolfinBeanUtils.class);

    public static OfertieTopology getTopology(Topology topology) {
        OfertieTopology ofTopo = new OfertieTopology();
        List<Switch> sws = new ArrayList<Switch>();
        Set<NetworkElement> nEs = topology.getNetworkElements();
        Switch sw;
        for (NetworkElement nE : nEs) {
            log.error(nE.getId());
            sw = new Switch();
            sw.setDpid(nE.getId());

            for (Port port : nE.getPorts()) {
                log.error("ports "+port.getId());
                sw.getPorts().add(port.getId());
            }
            sws.add(sw);
        }
        ofTopo.setSwitches(sws);
        log.error(ofTopo.getSwitches().size());
        return ofTopo;
    }

    public static GuiCircuitCollection mappingSwitchPort(CircuitCollection circuitCollection, OfertieTopology ofTopo) {
        GuiCircuitCollection guiCirCollection = new GuiCircuitCollection();
        List<GuiCircuits> listGuiCircuits = new ArrayList<GuiCircuits>();
        List<GuiSwitch> listSwitches = new ArrayList<GuiSwitch>();

        Collection<Circuit> circuits = circuitCollection.getCircuits();
        if (circuits.size() > 0) {
            for (Circuit circuit : circuits) {
                List<NetworkConnection> nCs = circuit.getRoute().getNetworkConnections();
                GuiCircuits guiCircuit = new GuiCircuits();
                for (NetworkConnection nC : nCs) {
                    String name = getSwitchOfPort(nC.getSource().getId(), ofTopo);
                    listSwitches.add(new GuiSwitch(name));
                    String dpid2 = getSwitchOfPort(nC.getDestination().getId(), ofTopo);
                    if (!name.equals(dpid2)) {
                        listSwitches.add(new GuiSwitch(dpid2));
                    }
                }
//                guiCircuit.setId(circuit.getCircuitId());
                guiCircuit.setGuiSwitches(listSwitches);
                listGuiCircuits.add(guiCircuit);
            }
        }
        guiCirCollection.setCircuits(listGuiCircuits);

        return guiCirCollection;
    }

    public static GuiCircuits mappingSwitchPort(Circuit circuit, OfertieTopology ofTopo) {
        List<GuiSwitch> listSwitches = new ArrayList<GuiSwitch>();

        List<NetworkConnection> nCs = circuit.getRoute().getNetworkConnections();
        GuiCircuits guiCircuit = new GuiCircuits();
        for (NetworkConnection nC : nCs) {
            log.error("NC: "+nC.getSource().getId());
            String dpid = getSwitchOfPort(nC.getSource().getId(), ofTopo);
            listSwitches.add(new GuiSwitch(dpid));
            log.error("Adding... "+dpid);
            String dpid2 = getSwitchOfPort(nC.getDestination().getId(), ofTopo);
            if (!dpid.equals(dpid2)) {
                log.error("Adding2... "+dpid2);
                listSwitches.add(new GuiSwitch(dpid2));
            }
        }
        guiCircuit.setGuiSwitches(listSwitches);

        return guiCircuit;
    }

    public static String getSwitchOfPort(String port, OfertieTopology ofTopo) {
        log.error("GET Switch of Prot");
        log.error("GET Switch of Prot"+ofTopo.getSwitches().size());
        for (Switch sw : ofTopo.getSwitches()) {
            log.error("Switch "+sw.getDpid());
            for (String p : sw.getPorts()) {
                log.error("GEtWI "+p);
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
        OfertieTopology of  = getTopology(topology);
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
    
    /** In the case that a port is not connected to any other switch, its possible taht the endpoint should be a host
     * 
     * @param ofTopo
     * @param guiTop 
     * @return  
     */
    public static Map<String, String> findUnusedPorts(Topology topology, GuiTopology guiTop){
        Boolean used;
        Map<String, String> map = new HashMap<String, String>();
        OfertieTopology ofTopo  = getTopology(topology);
        log.error(ofTopo.getSwitches().size());
        for (Switch sw : ofTopo.getSwitches()) {
            for(String port : sw.getPorts()){
                used = false;
                for (GuiLink link : guiTop.getLinks()) {
                    if( link.getSrcPort().equals(port) || link.getDstPort().equals(port)) {
                        used = true;
                        break;
                    }
                }
                if(!used){
                    map.put(sw.getDpid(), port);
                }
            }
        }
        return map;
    }
    
    public static String mapperObjectsToJSON(Object obj){
        String response = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            response = mapper.writeValueAsString(obj);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(DolfinBeanUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }
}
