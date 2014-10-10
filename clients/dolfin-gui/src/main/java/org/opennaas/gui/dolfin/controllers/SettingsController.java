package org.opennaas.gui.dolfin.controllers;

import java.util.Locale;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.opennaas.gui.dolfin.bos.DolfinBO;
import org.opennaas.gui.dolfin.entities.settings.Settings;
import org.opennaas.gui.dolfin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
@Controller
@RequestMapping("/secure/dolfin")
@SessionAttributes("settings")
public class SettingsController {

    private static final Logger LOGGER = Logger.getLogger(SettingsController.class);
    @Autowired
    protected DolfinBO dolfinBO;

    /**
     * Redirect to Settings view.
     *
     * @param model
     * @param locale
     * @param session
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/settings")
    public String settings(ModelMap model, Locale locale, HttpSession session) {
        LOGGER.error("Get Settings view -----------------");

        Settings settings = new Settings();
        if ((Settings) session.getAttribute("settings") != null) {
            model.put("settings", (Settings) session.getAttribute("settings"));
            settings = (Settings) session.getAttribute("settings");
        } else {
            model.addAttribute("errorMsg", "Session time out. Return to <a href='"+Constants.HOME_URL+"'>Home</a>");
        }

        if ((String) session.getAttribute("topologyName") != null) {
            model.put("topologyName", (String) session.getAttribute("topologyName"));
        }

        model.addAttribute(settings);
        return "settings";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/settings")
    public String settings(Settings settings, BindingResult result, ModelMap model, Locale locale, HttpSession session) {
        LOGGER.error("Get Settings view -----------------");

        Settings settings_ses = null;
        if ((Settings) session.getAttribute("settings") != null) {
            model.put("settings", (Settings) session.getAttribute("settings"));
            settings_ses = (Settings) session.getAttribute("settings");
        } else {
            model.addAttribute("errorMsg", "Session time out. Return to <a href='"+Constants.HOME_URL+"'>Home</a>");
        }
        if ((String) session.getAttribute("topologyName") != null) {
            model.put("topologyName", (String) session.getAttribute("topologyName"));
        }
        model.addAttribute("settings", settings);
        Constants.GENERICNETWORK_RESOURCE = settings.getGenNetResName();
//        dolfinBO.setGenNetResource(settings.getGenNetResName());
        return "settings";
    }
}
