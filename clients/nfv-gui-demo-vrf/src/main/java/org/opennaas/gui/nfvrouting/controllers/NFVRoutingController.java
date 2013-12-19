package org.opennaas.gui.nfvrouting.controllers;

import java.util.Locale;
import org.apache.log4j.Logger;
import org.opennaas.gui.nfvrouting.beans.insertRoutes;
import org.opennaas.gui.nfvrouting.bos.NFVRoutingBO;
import org.opennaas.gui.nfvrouting.entities.Route;
import org.opennaas.gui.nfvrouting.services.rest.RestServiceException;
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

    /**
     * Redirect to Configure view. Get the Route table of the given IP type.
     *
     * @param type
     * @param model
     * @param locale
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/secure/noc/nfvRouting/getRouteTable")
    public String getRouteTable(@RequestParam("type") String type, Model model, Locale locale) {
        LOGGER.debug("Get Route Table ------------------> IPv" + type);
        int typ;
        if (type.equals("IPv4")) {
            typ = 4;
        } else if (type.equals("IPv6")) {
            typ = 6;
        } else {
            model.addAttribute("errorMsg", "This type of table does not exist.");
            return "configRoute";
        }
        try {
            String response = nfvRoutingBO.getRouteTable(typ);
            LOGGER.info("received json: " + response);
            model.addAttribute("json", response);
        } catch (RestServiceException e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "home";
        }
        return "configRoute";
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
        return "insertRoute";
    }

    /**
     * Redirect to insert view and insert the values received by POST
     *
     * @param route
     * @param result
     * @param model
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/secure/noc/nfvRouting/insertRoute")
    public String insertRoutePost(insertRoutes route, BindingResult result, ModelMap model) {
        LOGGER.info("Insert route ------------------> " + route.getListRoutes());
        try {
            for (Route r : route.getListRoutes()) {
                String response = nfvRoutingBO.insertRoute(r);
                model.addAttribute("json", response);
            }
            model.addAttribute("infoMsg", "Route addded correctly.");
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
        }

        return "insertRoute";
    }

    /**
     * Redirect to insert view and insert the values received by POST
     *
     * @param route
     * @param result
     * @param model
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/secure/noc/nfvRouting/animation")
    public String animation(insertRoutes route, BindingResult result, ModelMap model) {
        LOGGER.info("Animation ------------------> " + route.getListRoutes());

        return "animation";
    }

    /**
     * Remove the Route without redirect
     *
     * @param type
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/secure/noc/nfvRouting/deleteRoute/{id}")
    public @ResponseBody
    String deleteRoute(@RequestParam("type") String type, @PathVariable("id") int id, ModelMap model) {
        LOGGER.debug("Remove Route ------------------> " + id);
        String response = "";
        int version;
        if (type.equals("IPv4")) {
            version = 4;
        } else if (type.equals("IPv6")) {
            version = 6;
        } else {
            model.addAttribute("errorMsg", "This type of table does not exist.");
            return "configRoute";
        }
        try {
            response = nfvRoutingBO.deleteRoute(id, version);
            model.addAttribute("json", response);
            model.addAttribute("infoMsg", "Route addded correctly.");
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
        }
        return response;
    }

    /**
     * Used in the Demo in order to show the Log
     *
     * @param model
     * @return the log of OpenNaaS
     */
    @RequestMapping(method = RequestMethod.GET, value = "/secure/noc/nfvRouting/getLog")
    public @ResponseBody
    String getLog(ModelMap model) {
        LOGGER.debug("Get log ------------------");
        String response = nfvRoutingBO.getLog();

        return response;
    }

    /**
     * Obtain information of a switch. In which controller is connected and the
     * Flow table.
     *
     * @param dpid
     * @param model
     * @return the information of the switch (IP:port)
     */
    @RequestMapping(method = RequestMethod.GET, value = "/secure/nfvRouting/getInfoSw/{dpid}")
    public @ResponseBody
    String getInfoSw(@PathVariable("dpid") String dpid, ModelMap model) {
        LOGGER.debug("Get Information about switch ------------------");
        String response = nfvRoutingBO.getSwInfo(dpid);
        return response;
    }
}
