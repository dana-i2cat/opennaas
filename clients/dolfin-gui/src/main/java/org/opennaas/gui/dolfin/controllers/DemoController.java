package org.opennaas.gui.dolfin.controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.wrapper.QoSPolicyRequestsWrapper;
import org.opennaas.gui.dolfin.bos.DolfinBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
@Controller
public class DemoController {
    //this controller only configures the demo (iperf, insert specific flows...)
    private static final Logger LOGGER = Logger.getLogger(DolfinController.class);
    @Autowired
    protected DolfinBO dolfinBO;
    
    @RequestMapping(method = RequestMethod.GET, value = "/secure/dolfin/demo/insertDemoPath")
    public @ResponseBody  String createFlows(ModelMap model) {
        String response = insertFlows("demoPath");
        if(response.equals("400") || response.equals("500")){
            model.addAttribute("errorMsg", "Error inserting circuits.");
        }else{
            model.addAttribute("infoMsg", "Inserted circuits correctly.");
        }
        return response;
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/secure/dolfin/demo/insertIperfReq")
    public @ResponseBody String ipReq(ModelMap model) {
        String response = insertFlows("iperf");
        if(response.equals("400") || response.equals("500")){
            model.addAttribute("errorMsg", "Error inserting circuits.");
        }else{
            model.addAttribute("infoMsg", "Inserted circuits correctly.");
        }
        return response;
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/secure/dolfin/demo/deleteFlows")
    public @ResponseBody String deleteFlows(ModelMap model) {
        String response = "";
        //read keys
        QoSPolicyRequestsWrapper qos = dolfinBO.getAllocatedFlow();
        for ( String key : qos.getQoSPolicyRequests().keySet() ) {
            LOGGER.error("Deallocate flow with key: "+key);
            response = dolfinBO.deallocatesFlow(key);
        }
        
        model.addAttribute("infoMsg", "Inserted circuits correctly.");
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
                    response = dolfinBO.allocateFlow(lines.get(i+1));
                }
            }
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(DolfinController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return response;
    }
    
    public List<String> readFile(File newFile) throws IOException{
        List<String> lines = FileUtils.readLines(newFile);
        return lines;
    }
}
