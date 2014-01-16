package org.opennaas.gui.nfvrouting.controllers;

import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.opennaas.gui.nfvrouting.bos.NFVRoutingBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Josep
 */
@Controller
public class HomeController {

    private static final Logger LOGGER = Logger.getLogger(HomeController.class);
    @Autowired
    protected NFVRoutingBO nfvRoutingBO;
    @Autowired
    protected ReloadableResourceBundleMessageSource messageSource;

    /**
     * Redirect to home
     *
     * @param model
     * @param locale
     * @param session
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/secure/nfvRouting/home")
    public String home(Model model, Locale locale, HttpSession session) {
        LOGGER.debug("home");
        try {
            String response = nfvRoutingBO.getRouteTable(4);
            if (response.equals("OpenNaaS is not started")){
                model.addAttribute("errorMsg", response);
            }
//            String response = nfvRoutingBO.getInfoControllers();
//            model.addAttribute("json", response);
        } catch (Exception e) {
            return "home";
        }
        return "home";
    }

    /**
     * Request the status of the controllers
     *
     * @param ip
     * @param model
     * @param locale
     * @param session
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/secure/nfvRouting/controllerStatus/{ip}")
    public @ResponseBody
    String ctrlStatus(@PathVariable("ip") String ip, Model model, Locale locale, HttpSession session) {
        LOGGER.debug("Controller Status " + ip);
        String response = "Offline";
        try {
//            response = nfvRoutingBO.getControllerStatus(ip);
        } catch (Exception e) {
            return response;
        }
        return response;
    }
    
    /**
     * Request the Flow Table of switch.
     * @param dpid
     * @param model
     * @param locale
     * @param session
     * @return Flow table in xml representation
     */
    @RequestMapping(method = RequestMethod.GET, value = "/secure/nfvRouting/switchInfo/{dpid}")
    public @ResponseBody String getSwInfo(@PathVariable("dpid") String dpid, Model model, Locale locale, HttpSession session) {
        LOGGER.debug("Request switch information of switch with the following DPID: " + dpid);
        String response = "";
        try {
            response = nfvRoutingBO.getSwInfo(dpid);
        } catch (Exception e) {
            return response;
        }
        return response;
    }
}
