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
    public String settings(ModelMap model, Locale locale, HttpSession session) {
        LOGGER.error("Get Settings view -----------------");

        Settings settings = new Settings();
	if ((Settings) session.getAttribute("settings") != null) {
            model.put("settings", (Settings) session.getAttribute("settings"));
            settings = (Settings) session.getAttribute("settings");
        }else{
		model.addAttribute("errorMsg", "Session time out. Return to <a href='http://nfv.opennaas.i2cat.net/secure/nfvRouting/home'>Home</a>");
//		return "home";
	}
        
        if ((String) session.getAttribute("topologyName") != null) {
            model.put("topologyName", (String) session.getAttribute("topologyName"));
        }

        try{
            String response = nfvRoutingBO.getONRouteMode();
            if (response.equals("OpenNaaS is not started")) {
                model.addAttribute("errorMsg", response);
            }
            LOGGER.info("ON ROUTING MODE: " + response);
            model.addAttribute("onRouteMode", response);
            settings.setRoutingType(response);
        }catch(Exception e){
            model.addAttribute("errorMsg", "Session time out.");
        }
        model.addAttribute(settings);
        return "settings";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/secure/noc/nfvRouting/settings")
    public String settings(Settings settings, BindingResult result, ModelMap model, Locale locale, HttpSession session) {
        LOGGER.error("Get Settings view -----------------");

        Settings settings_ses = null;
	if ((Settings) session.getAttribute("settings") != null) {
            model.put("settings", (Settings) session.getAttribute("settings"));
		settings_ses =  (Settings) session.getAttribute("settings");
        }else{
		model.addAttribute("errorMsg", "Session time out. Return to <a href='http://nfv.opennaas.i2cat.net/secure/nfvRouting/home'>Home</a>");
	}
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
