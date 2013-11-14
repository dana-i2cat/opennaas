package org.opennaas.gui.nfvrouting.controllers;

import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.opennaas.gui.nfvrouting.bos.NFVRoutingBO;
import org.opennaas.gui.nfvrouting.services.rest.RestServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.opennaas.gui.nfvrouting.utils.Constants;

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
    protected String resourceName = Constants.RESOURCE_VRF_NAME;

    /**
     * Redirect to home
     * 
     * @param model
     * @param locale
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/secure/nfvRouting/home")
    public String home(Model model, Locale locale, HttpSession session) {
        LOGGER.debug("home");
        try {
            String response = nfvRoutingBO.getInfoControllers(resourceName);
            model.addAttribute("json", response);
        } catch (Exception e) {
            return "home";
        }
        return "controllerInfo";
    }

    /**
     * Request the status of the controllers
     * @param ip
     * @param model
     * @param locale
     * @param session
     * @return 
     */
    @RequestMapping(method = RequestMethod.GET, value = "/secure/nfvRouting/controllerStatus/{ip}")
    public @ResponseBody String ctrlStatus(@PathVariable("ip") String ip, Model model, Locale locale, HttpSession session) {
        LOGGER.debug("Controller Status "+ip);
        String response = "Offline";
        try {
            response = nfvRoutingBO.getControllerStatus(resourceName, ip);
        } catch (Exception e) {
            return response;
        }
        return response;
    }
}
