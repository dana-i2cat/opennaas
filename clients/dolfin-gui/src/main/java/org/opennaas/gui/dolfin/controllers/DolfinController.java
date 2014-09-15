package org.opennaas.gui.dolfin.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.api.CircuitCollection;
import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;
import org.opennaas.extensions.genericnetwork.model.driver.DevicePortId;
import org.opennaas.extensions.genericnetwork.model.topology.Topology;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.wrapper.QoSPolicyRequestsWrapper;
import org.opennaas.gui.dolfin.bos.OfertieBO;
import org.opennaas.gui.dolfin.entities.GuiCircuitCollection;
import org.opennaas.gui.dolfin.entities.GuiCircuits;
import org.opennaas.gui.dolfin.entities.GuiSwitch;
import org.opennaas.gui.dolfin.entities.GuiTopology;
import org.opennaas.gui.dolfin.entities.OfertieTopology;
import org.opennaas.gui.dolfin.entities.settings.Settings;
import org.opennaas.gui.dolfin.services.rest.RestServiceException;
import static org.opennaas.gui.dolfin.utils.Constants.OFERTIE_GUI_URL;
import org.opennaas.gui.dolfin.utils.model.DolfinBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.xml.sax.SAXException;

/**
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
@Controller
@SessionAttributes("settings")
public class DolfinController {

    private static final Logger LOGGER = Logger.getLogger(DolfinController.class);
    @Autowired
    protected OfertieBO ofertieBO;
    @Autowired
    protected ReloadableResourceBundleMessageSource messageSource;
    private OfertieTopology ofertieTopology;
    private Topology Topology;
    private CircuitCollection allocatedCircuits;

    /**
     * Go to Circuits View. Request list of allocated circuits. In json var puts
     * the CollectionCircuits in xml format. Javascript changes to json.
     *
     * @param model
     * @param locale
     * @param session
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/secure/noc/ofertie/getCircuits")
    public String getCircuits(ModelMap model, Locale locale, HttpSession session) {
        LOGGER.error("Get Circuits");
        Settings settings = null;
        if ((Settings) session.getAttribute("settings") != null) {
            model.put("settings", (Settings) session.getAttribute("settings"));
            settings = (Settings) session.getAttribute("settings");
        } else {
            model.addAttribute("errorMsg", "Session time out. Return to <a href='"+OFERTIE_GUI_URL+"/secure/ofertie/home'>Home</a>");
        }
        if (settings == null) {
            settings = new Settings();
        }
        model.addAttribute("settings", settings);

        if ((String) session.getAttribute("topologyName") != null) {
            model.put("topologyName", (String) session.getAttribute("topologyName"));
        }
        if (ofertieTopology == null) {
            try {
                ofertieTopology = DolfinBeanUtils.getTopology(ofertieBO.getTopology());
                LOGGER.error("OfertieTopo");
                allocatedCircuits = ofertieBO.getAllocatedCircuits();
                LOGGER.error("GEtting allocated Circuits");
            } catch (RestServiceException ex) {
//                java.util.logging.Logger.getLogger(OfertieController.class.getName()).log(Level.SEVERE, null, ex);
                model.addAttribute("errorMsg", "The topology can not be read or some errors reading circuits.");
            }
        }

        try {
            /*            if(allocatedCircuits.getCircuits().size() > 0){
             GuiCircuitCollection guiCirColect = OfertieBeanUtils.mappingSwitchPort(allocatedCircuits, ofertieTopology);
             model.addAttribute("json", guiCirColect);
             }
             */        } catch (NullPointerException e) {//this try-catch is necesary because getCircuits is not initialized, and when is null, the size is null intead of 0
            LOGGER.error("Get Circuits is null");
        }
        model.addAttribute("xml", allocatedCircuits.toString());
        return "showCircuits";
    }

    /**
     * Request the allocated flows
     *
     * @param switchName
     * @param model
     * @return the information of the switch (IP:port)
     */
    @RequestMapping(method = RequestMethod.GET, value = "/secure/ofertie/getAllocatedFlows/{switchName}")
    public @ResponseBody
    String getAllocatedFlows(@PathVariable("switchName") String switchName, ModelMap model) {
        LOGGER.debug("Get Information about switch ------------------");
        String response = ofertieBO.getAllocatedFlows(switchName);
        return response;
    }

    /**
     * Return a json file that contains the Topology definiton
     *
     * @param model
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/secure/noc/ofertie/getTopology")
    public @ResponseBody
    Topology getTopology(ModelMap model) {
        LOGGER.error("Get ROUTE");
        Topology response = null;
        try {
            //get json file
            response = ofertieBO.getTopology();
            LOGGER.error(response);
            model.addAttribute("json", response);
            model.addAttribute("infoMsg", "Route removed correctly.");
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
        }
        return response;
    }

    /**
     * Obtain information of circuits due ajax.
     *
     * @param model
     * @return the Collection of Circuits
     */
    @RequestMapping(method = RequestMethod.GET, value = "/secure/noc/ofertie/getCircuits/ajax")
    public @ResponseBody CircuitCollection getAllocatedCircuits(ModelMap model) {
        if (ofertieTopology == null) {
            try {
                ofertieTopology = DolfinBeanUtils.getTopology(ofertieBO.getTopology());
            } catch (RestServiceException ex) {
                java.util.logging.Logger.getLogger(DolfinController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            allocatedCircuits = ofertieBO.getAllocatedCircuits();
        } catch (RestServiceException ex) {
            java.util.logging.Logger.getLogger(DolfinController.class.getName()).log(Level.SEVERE, null, ex);
        }
//        GuiCircuitCollection guiCirColect = OfertieBeanUtils.mappingSwitchPort(allocatedCircuits, ofertieTopology);
        return allocatedCircuits;
    }

    /**
     * Return the Ofertie Circuit in XML format
     *
     * @param circuitId
     * @param model
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/secure/noc/ofertie/getCircuit/{circuitId}")
    public @ResponseBody
    Circuit getAllocatedCircuit(@PathVariable("circuitId") String circuitId, ModelMap model) {
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
     * @param model
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/secure/noc/ofertie/getCircuitSwitches/{circuitId}")
    public @ResponseBody
    String getAllocatedCircuitSwitches(@PathVariable("circuitId") String circuitId, ModelMap model) {
        GuiCircuits guiCircuits = null;
        String response;
LOGGER.error("CIRCUIT ID: "+circuitId);
LOGGER.error("CIRCUIT ID: "+ofertieTopology.getSwitches().get(0).getDpid());
        for (Circuit circuit : allocatedCircuits.getCircuits()) {
            if (circuit.getCircuitId().equals(circuitId)) {
                guiCircuits = DolfinBeanUtils.mappingSwitchPort(circuit, ofertieTopology);
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
     * @param model
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/secure/ofertie/getSwitchByPort/{port}")
    public @ResponseBody
    String getSwitchByPort(@PathVariable("port") String port, ModelMap model) {
        return DolfinBeanUtils.getSwitchOfPort(port, ofertieTopology);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/secure/ofertie/topologyToGUI")
    public @ResponseBody
    String topologyToGuiTopology(ModelMap model) {
        GuiTopology guiTop = null;
        Map<String, String> possibleHosts = new HashMap<String, String>();
        try {
            guiTop = DolfinBeanUtils.convertONTopologyToGuiTopology(ofertieBO.getTopology());
            possibleHosts = DolfinBeanUtils.findUnusedPorts(ofertieBO.getTopology(), guiTop);
            guiTop.setPosibleHosts(possibleHosts);
        } catch (RestServiceException ex) {
            java.util.logging.Logger.getLogger(DolfinController.class.getName()).log(Level.SEVERE, null, ex);
        }
        String response = DolfinBeanUtils.mapperObjectsToJSON(guiTop);
        return response;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/secure/ofertie/insertDemoPath")
    public @ResponseBody
    String createFlows(ModelMap model) {
        String response = "";
        List<String> flows = new ArrayList<String>();

        /*
        String flow1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><qos_policy_request><source><address>192.168.10.10</address><port>0</port></source><destination><address>192.168.10.11</address><port>25000</port></destination><label>0</label><qos_policy><throughput><min timeout=\"0\">1000</min><max timeout=\"0\">1000</max></throughput></qos_policy></qos_policy_request>";
        flows.add(flow1);
        flow1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><qos_policy_request><source><address>192.168.10.11</address><port>0</port></source><destination><address>192.168.10.10</address><port>25000</port></destination><label>0</label><qos_policy><throughput><min timeout=\"0\">1000</min><max timeout=\"0\">1000</max></throughput></qos_policy></qos_policy_request>";
        flows.add(flow1);
        flow1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><qos_policy_request><source><address>192.168.10.10</address><port>0</port></source><destination><address>192.168.10.11</address><port>25000</port></destination><label>4</label><qos_policy><throughput><min timeout=\"0\">1000</min><max timeout=\"0\">1000</max></throughput></qos_policy></qos_policy_request>";
        flows.add(flow1);
        flow1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><qos_policy_request><source><address>192.168.10.11</address><port>0</port></source><destination><address>192.168.10.10</address><port>25000</port></destination><label>4</label><qos_policy><throughput><min timeout=\"0\">1000</min><max timeout=\"0\">1000</max></throughput></qos_policy></qos_policy_request>";
        flows.add(flow1);
        for(String flow : flows){
            response = ofertieBO.allocateFlow(flow);
        }
        */
        insertFlows("demoPath");
        LOGGER.error(response);
        if(response.equals("400") || response.equals("500")){
            model.addAttribute("errorMsg", "500");
        }else{
            model.addAttribute("infoMsg", "Inserted circuits correctly.");
        }
        return response;
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/secure/ofertie/insertIperfReq")
    public @ResponseBody String ipReq(ModelMap model) {
        String response = "";
        List<String> flows = new ArrayList<String>();
/*
        String flow1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><qos_policy_request><source><address>192.168.10.20</address><port>0</port></source><destination><address>192.168.10.21</address><port>25000</port></destination><label>0</label><qos_policy><throughput><min timeout=\"0\">1000</min><max timeout=\"0\">1000</max></throughput></qos_policy></qos_policy_request>";
        flows.add(flow1);
        flow1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><qos_policy_request><source><address>192.168.10.21</address><port>0</port></source><destination><address>192.168.10.20</address><port>25000</port></destination><label>0</label><qos_policy><throughput><min timeout=\"0\">1000</min><max timeout=\"0\">1000</max></throughput></qos_policy></qos_policy_request>";
        flows.add(flow1);
        for(String flow : flows){
            response = ofertieBO.allocateFlow(flow);
        }
        */
        insertFlows("iperf");
        model.addAttribute("infoMsg", "Inserted circuits correctly.");
        return response;
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/secure/ofertie/delete")
    public @ResponseBody String delete(ModelMap model) {
        String response = "";
        List<String> ls = new ArrayList<String>();
        //read keys
        QoSPolicyRequestsWrapper qos = ofertieBO.getAllocatedFlow();
        for ( String key : qos.getQoSPolicyRequests().keySet() ) {
            LOGGER.error("Deallocate flow with key: "+key);
            response = ofertieBO.deallocatesFlow(key);
        }
        
        model.addAttribute("infoMsg", "Inserted circuits correctly.");
        return response;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/secure/ofertie/test")
    public @ResponseBody String test(ModelMap model) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            GuiTopology guiTop = DolfinBeanUtils.convertONTopologyToGuiTopology(ofertieBO.getTopology());
            map = DolfinBeanUtils.findUnusedPorts(ofertieBO.getTopology(), guiTop);
        } catch (RestServiceException ex) {
            java.util.logging.Logger.getLogger(DolfinController.class.getName()).log(Level.SEVERE, null, ex);
        }
        String response = DolfinBeanUtils.mapperObjectsToJSON(map);
        return response;
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/secure/ofertie/readScriptFile")
    private @ResponseBody String readScriptFile() throws ParserConfigurationException, SAXException, IOException{
        String response = "";
        File newFile = new File("/home/ofertie/TNCDemo/IperfRequest.txt");///home/ofertie/TNCDemo/
   
//        response = readFile(newFile).get(1);
//        LOGGER.error(response);
        response = insertFlows("demoPath");
        LOGGER.error(response);
        return response;
    }
    
    private String insertFlows(String name){
        String response = "";
        List<String> lines;
        File newFile = null;
        if(name.equals("demoPath")){
            newFile = new File("/home/ofertie/TNCDemo/DemoPath.txt");///home/ofertie/TNCDemo/
        }else if(name.equals("iperf")){
            newFile = new File("/home/ofertie/TNCDemo/IperfRequest.txt");///home/ofertie/TNCDemo/
        }
        try {
            lines = readFile(newFile);
            for(int i=0; i<lines.size(); i++){
                if(lines.get(i).equals("<?xml version=\"1.0\" encoding=\"utf-8\"?>")){
                    LOGGER.error("Insert: "+lines.get(i+1));
                    response = ofertieBO.allocateFlow(lines.get(i+1));
//                    i++;
                }
            }
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(DolfinController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return response;
    }
    
    public List<String> readFile(File newFile) throws IOException{
        List<String> lines = FileUtils.readLines(newFile);
        for (String line : lines) {
            LOGGER.error(line);  
        }
        return lines;
    }
    @RequestMapping(method = RequestMethod.GET, value = "/secure/noc/ofertie/portIdsMap")
    public @ResponseBody Map<String, DevicePortId> portIdsMap(ModelMap mode) throws RestServiceException{
        Topology top = ofertieBO.getTopology();
        Map<String, DevicePortId> possibleHosts = new HashMap<String, DevicePortId>();
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
