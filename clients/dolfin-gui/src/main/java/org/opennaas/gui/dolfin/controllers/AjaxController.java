package org.opennaas.gui.dolfin.controllers;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.api.CircuitCollection;
import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;
import org.opennaas.extensions.genericnetwork.model.driver.DevicePortId;
import org.opennaas.extensions.genericnetwork.model.topology.Topology;
import org.opennaas.gui.dolfin.bos.DolfinBO;
import org.opennaas.gui.dolfin.entities.GuiCircuits;
import org.opennaas.gui.dolfin.entities.GuiSwitch;
import org.opennaas.gui.dolfin.entities.GuiTopology;
import org.opennaas.gui.dolfin.entities.OfertieTopology;
import org.opennaas.gui.dolfin.services.rest.RestServiceException;
import org.opennaas.gui.dolfin.utils.model.DolfinBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
@Controller
@RequestMapping("/secure/dolfin/ajax")
public class AjaxController {
    private static final Logger LOGGER = Logger.getLogger(DolfinController.class);
    @Autowired
    protected DolfinBO dolfinBO;
    private OfertieTopology dolfinTopology;
    private CircuitCollection allocatedCircuits;
    
    /**
     * Request the Flow Table of switch.
     *
     * @param dpid
     * @param model
     * @param locale
     * @param session
     * @return Flow table in xml representation
     */
    @RequestMapping(method = RequestMethod.GET, value = "/switchInfo/{dpid}")
    public @ResponseBody String getAllocatedFlows(@PathVariable("dpid") String dpid, Model model, Locale locale, HttpSession session) {
        LOGGER.debug("Request switch information of switch with the following DPID: " + dpid);
        String response = "";
        try {
            response = dolfinBO.getAllocatedFlows(dpid);
        } catch (Exception e) {
            return response;
        }
        return response;
    }
    
    /**
     * Request the allocated flows
     *
     * @param switchName
     * @return the information of the switch (IP:port)
     */
    @RequestMapping(method = RequestMethod.GET, value = "/getAllocatedFlows/{switchName}")
    public @ResponseBody String getAllocatedFlows(@PathVariable("switchName") String switchName) {
        LOGGER.debug("Get Information about switch ------------------");
        String response = dolfinBO.getAllocatedFlows(switchName);
        return response;
    }

    /**
     * Return a json file that contains the Topology definiton
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/getTopology")
    public @ResponseBody Topology getTopology() {
        LOGGER.error("Get ROUTE");
        Topology response = null;
        try {
            response = dolfinBO.getTopology();
        } catch (RestServiceException ex) {
            java.util.logging.Logger.getLogger(AjaxController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    /**
     * Obtain information of circuits due ajax.
     *
     * @return the Collection of Circuits
     */
    @RequestMapping(method = RequestMethod.GET, value = "/getCircuits")
    public @ResponseBody CircuitCollection getAllocatedCircuits() {
        if (dolfinTopology == null) {
            try {
                dolfinTopology = DolfinBeanUtils.getTopology(dolfinBO.getTopology());
            } catch (RestServiceException ex) {
                java.util.logging.Logger.getLogger(DolfinController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            allocatedCircuits = dolfinBO.getAllocatedCircuits();
        } catch (RestServiceException ex) {
            java.util.logging.Logger.getLogger(DolfinController.class.getName()).log(Level.SEVERE, null, ex);
        }
//        GuiCircuitCollection guiCirColect = OfertieBeanUtils.mappingSwitchPort(allocatedCircuits, dolfinTopology);
        return allocatedCircuits;
    }

    /**
     * Return the Ofertie Circuit in XML format
     *
     * @param circuitId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/getCircuit/{circuitId}")
    public @ResponseBody Circuit getAllocatedCircuit(@PathVariable("circuitId") String circuitId) {
        for (Circuit circuit : allocatedCircuits.getCircuits()) {
            if (circuit.getCircuitId().equals(circuitId)) {
                return circuit;
            }
        }
        return null;
    }

    /**
     * Return the Circuit used by the GUI in json format
     *
     * @param circuitId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/getCircuitSwitches/{circuitId}")
    public @ResponseBody String getAllocatedCircuitSwitches(@PathVariable("circuitId") String circuitId) {
        GuiCircuits guiCircuits = null;
        String response;
LOGGER.error("CIRCUIT ID: "+circuitId);
LOGGER.error("CIRCUIT ID: "+dolfinTopology.getSwitches().get(0).getDpid());
        for (Circuit circuit : allocatedCircuits.getCircuits()) {
            if (circuit.getCircuitId().equals(circuitId)) {
                guiCircuits = DolfinBeanUtils.mappingSwitchPort(circuit, dolfinTopology);
            }
        }
        LOGGER.error("ListSwitches: ");
        LOGGER.error("Size: "+guiCircuits.getGuiSwitches().size());
        for(GuiSwitch c : guiCircuits.getGuiSwitches()){
            LOGGER.error(c.getName());
        }
        response = DolfinBeanUtils.mapperObjectsToJSON(guiCircuits);
        return response;
    }

    /**
     * Return the switch id given a port number
     *
     * @param port
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/getSwitchByPort/{port}")
    public @ResponseBody String getSwitchByPort(@PathVariable("port") String port) {
        return DolfinBeanUtils.getSwitchOfPort(port, dolfinTopology);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/topologyToGUI")
    public @ResponseBody  String topologyToGuiTopology(ModelMap model) {
        GuiTopology guiTop = null;
        Map<String, String> possibleHosts;
        try {
            guiTop = DolfinBeanUtils.convertONTopologyToGuiTopology(dolfinBO.getTopology());
            possibleHosts = DolfinBeanUtils.findUnusedPorts(dolfinBO.getTopology(), guiTop);
            guiTop.setPosibleHosts(possibleHosts);
        } catch (RestServiceException ex) {
            java.util.logging.Logger.getLogger(DolfinController.class.getName()).log(Level.SEVERE, null, ex);
        }
        String response = DolfinBeanUtils.mapperObjectsToJSON(guiTop);
        return response;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/test")
    public @ResponseBody String test(ModelMap model) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            GuiTopology guiTop = DolfinBeanUtils.convertONTopologyToGuiTopology(dolfinBO.getTopology());
            map = DolfinBeanUtils.findUnusedPorts(dolfinBO.getTopology(), guiTop);
        } catch (RestServiceException ex) {
            java.util.logging.Logger.getLogger(DolfinController.class.getName()).log(Level.SEVERE, null, ex);
        }
       return DolfinBeanUtils.mapperObjectsToJSON(map);
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/portIdsMap")
    public @ResponseBody Map<String, DevicePortId> portIdsMap(ModelMap mode) throws RestServiceException{
        Topology top = dolfinBO.getTopology();
        Map<String, DevicePortId> possibleHosts;
        possibleHosts = top.getNetworkDevicePortIdsMap();
        return possibleHosts;
/*        Iterator<String> keySetIterator = possibleHosts.keySet().iterator();
        while(keySetIterator.hasNext()){
            String key = keySetIterator.next();
            DevicePortId dvP = possibleHosts.get(key);
            if()
                return dvP.getDevicePortId();
        }
        
        return null;*/
    }
}
