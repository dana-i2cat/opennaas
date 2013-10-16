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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Josep
 */
@Controller
public class NFVRoutingController {

    private static final Logger LOGGER = Logger.getLogger(NFVRoutingController.class);
    @Autowired
    protected NFVRoutingBO nfvRoutingBO;
    @Autowired
    protected ReloadableResourceBundleMessageSource messageSource;

    /**
     * Redirect to the physical view
     * 
     * @param templateType
     * @param model
     * @param locale
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/secure/noc/nfvRouting/getRouteTable")
    public String getRouteTable(@RequestParam("type") String type, Model model, Locale locale) {

        LOGGER.debug("------------------");
        try {
            String response = nfvRoutingBO.getRouteTable("VM-Routing1", type);
            LOGGER.info("received json: "+response);
            model.addAttribute("json", response);
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
        }
//                model.addAttribute("physicalInfrastructure", vcpeNetworkBO.getPhysicalInfrastructure(templateType));

        return "table";
    }

    /**
     * Redirect to the physical view
     * 
     * @param templateType
     * @param model
     * @param locale
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/secure/noc/nfvRouting/insertRoute")
    public String insertRoute(Model model) {
        model.addAttribute(new insertRoutes());

        return "insert";
    }

    /**
     * Redirect to the physical view
     * 
     * @param templateType
     * @param model
     * @param locale
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/secure/noc/nfvRouting/insertRoute")
    public String insertRoutePost(insertRoutes route, BindingResult result, ModelMap model) {
        LOGGER.error("Insert route------------------"+route.getListRoutes());
        
//        LOGGER.error(route.getSourceAddress());

        try {
            for(Route r : route.getListRoutes()){
                String response = nfvRoutingBO.insertRoute(r);
                model.addAttribute("json", response);
            }
            model.addAttribute("infoMsg", "Route addded correctly.");
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
        }
//                model.addAttribute("physicalInfrastructure", vcpeNetworkBO.getPhysicalInfrastructure(templateType));

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
     * Redirect to the physical view
     * 
     * @param templateType
     * @param model
     * @param locale
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/secure/noc/nfvRouting/insertCtrlInfo")
    public String insertCtrlInfo(insertCtrlInfo ctrlInfo, BindingResult result, ModelMap model) {
        LOGGER.error("Insert Controller information------------------"+ctrlInfo.getListCtrl());
        try {
            for(ControllerInfo r : ctrlInfo.getListCtrl()){
                if (r.getControllerIp().equals("192.168.0.6"))
                        r.setControllerIp("controllersVM");
                else if (r.getControllerIp().equals("192.168.0.10"))
                        r.setControllerIp("controllersVM2");
                else
                    model.addAttribute("errorMsg", "This IP does not correspond to any controller in this demo");
                String response = nfvRoutingBO.insertCtrlInfo(r);
                model.addAttribute("json", response);
            }
            model.addAttribute("infoMsg", "Route addded correctly.");
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
        }
//                model.addAttribute("physicalInfrastructure", vcpeNetworkBO.getPhysicalInfrastructure(templateType));

        return "insertCtrlInfo";
    }
    
}
