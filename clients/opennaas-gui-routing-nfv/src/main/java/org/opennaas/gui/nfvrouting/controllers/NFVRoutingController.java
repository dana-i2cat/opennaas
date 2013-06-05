package org.opennaas.gui.nfvrouting.controllers;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.opennaas.gui.nfvrouting.bos.NFVRoutingBO;
import org.opennaas.gui.nfvrouting.entities.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
    public String getRouteTable(Model model, Locale locale) {

        LOGGER.debug("------------------");
        try {
            String response = nfvRoutingBO.getRouteTable("test");
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
    public String insertRoute(Model model, Locale locale) {
        model.addAttribute(new Route());

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
    public String insertRoutePost(Route route, BindingResult result, Model model, Locale locale) {
        LOGGER.debug("Insert route------------------");
        try {
            String response = nfvRoutingBO.insertRoute(route);
            model.addAttribute("json", response);
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
        }
//                model.addAttribute("physicalInfrastructure", vcpeNetworkBO.getPhysicalInfrastructure(templateType));

        return "insert";
    }

    
}
