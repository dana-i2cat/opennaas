package org.opennaas.gui.dolfin.controllers;

import java.io.File;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.opennaas.gui.dolfin.bos.DolfinBO;
import org.opennaas.gui.dolfin.entities.settings.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * @author Josep
 */
@Controller
@SessionAttributes("settings")
public class HomeController {

    private static final Logger LOGGER = Logger.getLogger(HomeController.class);
    @Autowired
    protected DolfinBO ofertieBO;
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
    @RequestMapping(method = RequestMethod.GET, value = "/secure/ofertie/home")
    public String home(ModelMap model, Locale locale, HttpSession session) {
        LOGGER.debug("home");
        if ((String) session.getAttribute("topologyName") != null) {
            model.put("topologyName", (String) session.getAttribute("topologyName"));
        }

        Settings settings = (Settings) session.getAttribute("settings");
        if(settings == null){
            settings = new Settings();
        }
	model.addAttribute("settings", settings);
        return "home";
    }

    /**
     * Request the Flow Table of switch.
     *
     * @param dpid
     * @param model
     * @param locale
     * @param session
     * @return Flow table in xml representation
     */
    @RequestMapping(method = RequestMethod.GET, value = "/secure/ofertie/switchInfo/{dpid}")
    public @ResponseBody String getAllocatedFlows(@PathVariable("dpid") String dpid, Model model, Locale locale, HttpSession session) {
        LOGGER.debug("Request switch information of switch with the following DPID: " + dpid);
        String response = "";
        try {
            response = ofertieBO.getAllocatedFlows(dpid);
        } catch (Exception e) {
            return response;
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/secure/ofertie/home/{topoName}")
    public String home(@PathVariable("topoName") String topoName, ModelMap model, Locale locale, HttpServletRequest request, HttpSession session) {
        LOGGER.debug("home");
        if ((String) session.getAttribute("topologyName") != null) {
            model.put("topologyName", (String) session.getAttribute("topologyName"));
        }

        File newFile = new File(request.getRealPath("") + "/resources/js/topology/topo.json");
        session.setAttribute("topologyName", newFile.getPath());
        return "home";
    }
}
