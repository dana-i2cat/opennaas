package org.opennaas.gui.nfvrouting.controllers;

import java.util.Locale;
import org.apache.log4j.Logger;
import org.opennaas.gui.nfvrouting.beans.insertCtrlInfo;
import org.opennaas.gui.nfvrouting.beans.insertRoutes;
import org.opennaas.gui.nfvrouting.bos.NFVRoutingBO;
import org.opennaas.gui.nfvrouting.entities.ControllerInfo;
import org.opennaas.gui.nfvrouting.entities.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.opennaas.gui.nfvrouting.utils.Constants;

/**
 * @author Josep Batallé (josep.batalle@i2cat.net)
 */
@Controller
public class NFVRoutingController {

    private static final Logger LOGGER = Logger.getLogger(NFVRoutingController.class);
    @Autowired
    protected NFVRoutingBO nfvRoutingBO;
    @Autowired
    protected ReloadableResourceBundleMessageSource messageSource;
    protected String resourceName = Constants.RESOURCE_VRF_NAME;

    /**
     * Redirect to table view
     * 
     * @param templateType
     * @param model
     * @param locale
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/secure/noc/nfvRouting/getRouteTable")
    public String getRouteTable(@RequestParam("type") String type, Model model, Locale locale) {
        LOGGER.debug("Get Route Table ------------------> "+type);
        int typ;
        if (type.equals("IPv4")){
                typ = 4;
        }else if (type.equals("IPv6")){
               typ = 6;
        }else{
            model.addAttribute("errorMsg", "This type of table does not exist.");
            return "table";
        }
        try {
            String response = nfvRoutingBO.getRouteTable(resourceName, typ);
            LOGGER.info("received json: "+response);
            model.addAttribute("json", response);
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
        }
        return "table";
    }

    /**
     * **
     * Redirect to insert view
     * 
     * @param model
     * @return 
     */
    @RequestMapping(method = RequestMethod.GET, value = "/secure/noc/nfvRouting/insertRoute")
    public String insertRoute(Model model) {
        model.addAttribute(new insertRoutes());
        return "insert";
    }

    /**
     * Redirect to insert view and insert the values received by POST
     * 
     * @param templateType
     * @param model
     * @param locale
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/secure/noc/nfvRouting/insertRoute")
    public String insertRoutePost(insertRoutes route, BindingResult result, ModelMap model) {
        LOGGER.error("Insert route ------------------> "+route.getListRoutes());
       
        try {
            for(Route r : route.getListRoutes()){
                String response = nfvRoutingBO.insertRoute(resourceName, r);
                model.addAttribute("json", response);
            }
            model.addAttribute("infoMsg", "Route addded correctly.");
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
        }

        return "insert";
    }

    /**
     * Redirect to insert controller info view
     * 
     * @param templateType
     * @param model
     * @param locale
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/secure/noc/nfvRouting/insertCtrlInfo")
    public String insertCtrlInfo(Model model) {
        model.addAttribute(new insertCtrlInfo());

        return "insertCtrlInfo";
    }

    /**
     * Redirect to insert Controller Info view
     * @param ctrlInfo
     * @param result
     * @param model
     * @return 
     */
    @RequestMapping(method = RequestMethod.POST, value = "/secure/noc/nfvRouting/insertCtrlInfo")
    public String insertCtrlInfo(insertCtrlInfo ctrlInfo, BindingResult result, ModelMap model) {
        LOGGER.debug("Insert Controller information ------------------> "+ctrlInfo.getListCtrl());
        try {
            for(ControllerInfo r : ctrlInfo.getListCtrl()){
                if (r.getControllerIp().equals("192.168.0.6")){
                        r.setControllerIp("controllersVM");
                }else if (r.getControllerIp().equals("192.168.0.10")){
                        r.setControllerIp("controllersVM2");
                }else{
                    model.addAttribute("errorMsg", "This IP does not correspond to any controller in this demo");
                }
                String response = nfvRoutingBO.insertCtrlInfo(resourceName, r);
                model.addAttribute("json", response);
            }
            model.addAttribute("infoMsg", "Route addded correctly.");
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
        }

        return "insertCtrlInfo";
    }
    
    /**
     * Remove the Route without redirect
     * @param id
     * @param model
     * @return 
     */
    @RequestMapping(method = RequestMethod.POST, value = "/secure/noc/nfvRouting/deleteRoute/{id}")
    public @ResponseBody String deleteRoute(@RequestParam("type") String type, @PathVariable("id") int id, ModelMap model) {
        LOGGER.debug("Remove Route ------------------> "+id);
        String response = "";
        int version;
        if (type.equals("IPv4")){
                version = 4;
        }else if (type.equals("IPv6")){
               version = 6;
        }else{
            model.addAttribute("errorMsg", "This type of table does not exist.");
            return "table";
        }
        try {
            response = nfvRoutingBO.deleteRoute(resourceName, id, version);
            model.addAttribute("json", response);
            model.addAttribute("infoMsg", "Route addded correctly.");
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
        }

        return response;
    }
    /**
     * Used in the Demo in order to show the Log
     * @param model
     * @return the log of OpenNaaS
     */
    @RequestMapping(method = RequestMethod.GET, value = "/secure/noc/nfvRouting/getLog")
    public @ResponseBody String getLog(ModelMap model) {
        LOGGER.debug("Get log ------------------");
        String response = nfvRoutingBO.getLog(resourceName);
            
        return response;
    }
}
