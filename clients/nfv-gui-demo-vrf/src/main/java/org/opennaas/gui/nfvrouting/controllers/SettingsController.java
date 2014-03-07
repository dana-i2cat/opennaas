package org.opennaas.gui.nfvrouting.controllers;

import java.util.Locale;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.opennaas.gui.nfvrouting.bos.NFVRoutingBO;
import org.opennaas.gui.nfvrouting.entities.settings.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 *
 * @author i2cat
 */
@Controller
@SessionAttributes("settings")
public class SettingsController {

    private static final Logger LOGGER = Logger.getLogger(NFVRoutingController.class);
    @Autowired
    protected NFVRoutingBO nfvRoutingBO;

    /**
     * Redirect to Settings view.
     *
     * @param type
     * @param model
     * @param locale
     * @param session
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/secure/noc/nfvRouting/settings")
    public String settings(@ModelAttribute("settings") Settings settings, ModelMap model, Locale locale, HttpSession session) {
        LOGGER.error("Get Settings view -----------------");

        model.addAttribute(settings);
        if ((String) session.getAttribute("topologyName") != null) {
            model.put("topologyName", (String) session.getAttribute("topologyName"));
        }

        String response = nfvRoutingBO.getONRouteMode();
        if (response.equals("OpenNaaS is not started")) {
            model.addAttribute("errorMsg", response);
        }
        LOGGER.info("ON ROUTING MODE: " + response);
        model.addAttribute("onRouteMode", response);

        return "settings";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/secure/noc/nfvRouting/settings")
    public String settings(@ModelAttribute("settings") Settings settings_ses, Settings settings, BindingResult result, ModelMap model, Locale locale, HttpSession session) {
        LOGGER.error("Get Settings view -----------------");

        if ((String) session.getAttribute("topologyName") != null) {
            model.put("topologyName", (String) session.getAttribute("topologyName"));
        }
        
        if (settings_ses == null) {
            settings_ses = new Settings();
        }

        nfvRoutingBO.setONRouteMode(settings.getRoutingType());
        model.addAttribute("settings", settings);

        return "settings";
    }
}
