package org.opennaas.gui.dolfin.controllers;

import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.api.CircuitCollection;
import org.opennaas.extensions.genericnetwork.model.topology.Topology;
import org.opennaas.gui.dolfin.bos.DolfinBO;
import org.opennaas.gui.dolfin.entities.OfertieTopology;
import org.opennaas.gui.dolfin.entities.Switch;
import org.opennaas.gui.dolfin.entities.settings.Settings;
import org.opennaas.gui.dolfin.services.rest.RestServiceException;
import static org.opennaas.gui.dolfin.utils.Constants.OFERTIE_GUI_URL;
import org.opennaas.gui.dolfin.utils.model.DolfinBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
@Controller
@RequestMapping("/secure/dolfin")
@SessionAttributes("settings")
public class DolfinController {

    private static final Logger LOGGER = Logger.getLogger(DolfinController.class);
    @Autowired
    protected DolfinBO dolfinBO;
    @Autowired
    protected ReloadableResourceBundleMessageSource messageSource;
    private OfertieTopology dolfinTopology;
    private CircuitCollection allocatedCircuits;

    /**
     * Go to Circuits View. Request list of allocated circuits. In json var puts
     * the CollectionCircuits in xml format. Javascript changes to json.
     *
     * @param model
     * @param locale
     * @param session
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/circuits")
    public String circuits(ModelMap model, Locale locale, HttpSession session) {
        LOGGER.error("Get Circuits");
        Settings settings = null;
        if ((Settings) session.getAttribute("settings") != null) {
            model.put("settings", (Settings) session.getAttribute("settings"));
            settings = (Settings) session.getAttribute("settings");
        } else {
            model.addAttribute("errorMsg", "Session time out. Return to <a href='"+OFERTIE_GUI_URL+"/secure/dolfin/home'>Home</a>");
        }
        if (settings == null) {
            settings = new Settings();
        }
        model.addAttribute("settings", settings);

        if ((String) session.getAttribute("topologyName") != null) {
            model.put("topologyName", (String) session.getAttribute("topologyName"));
        }
        if (dolfinTopology == null) {
            try {
                dolfinTopology = DolfinBeanUtils.getTopology(dolfinBO.getTopology());
                LOGGER.error("OfertieTopo");
                allocatedCircuits = dolfinBO.getAllocatedCircuits();
                model.addAttribute("xml", allocatedCircuits.toString());
        
                LOGGER.error("GEtting allocated Circuits");
            } catch (RestServiceException ex) {
//                java.util.logging.Logger.getLogger(OfertieController.class.getName()).log(Level.SEVERE, null, ex);
                model.addAttribute("errorMsg", "The topology can not be read or some errors reading circuits.");
            }catch (NullPointerException ex) {
//                java.util.logging.Logger.getLogger(OfertieController.class.getName()).log(Level.SEVERE, null, ex);
                model.addAttribute("errorMsg", "The topology can not be read or some errors reading circuits.");
            }
        }

        try {
            /*            if(allocatedCircuits.getCircuits().size() > 0){
             GuiCircuitCollection guiCirColect = OfertieBeanUtils.mappingSwitchPort(allocatedCircuits, dolfinTopology);
             model.addAttribute("json", guiCirColect);
             }
             */        } catch (NullPointerException e) {//this try-catch is necesary because getCircuits is not initialized, and when is null, the size is null intead of 0
            LOGGER.error("Get Circuits is null");
        }
        return "showCircuits";
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/statistics")
    public String statistics(ModelMap model, Locale locale, HttpSession session) {
        LOGGER.error("Get Statistics view");
        List<Switch> listSwitches;
        Settings settings = null;
        if ((Settings) session.getAttribute("settings") != null) {
            model.put("settings", (Settings) session.getAttribute("settings"));
            settings = (Settings) session.getAttribute("settings");
        } else {
            model.addAttribute("errorMsg", "Session time out. Return to <a href='"+OFERTIE_GUI_URL+"/secure/dolfin/home'>Home</a>");
        }
        if (settings == null) {
            settings = new Settings();
        }
        model.addAttribute("settings", settings);

        if ((String) session.getAttribute("topologyName") != null) {
            model.put("topologyName", (String) session.getAttribute("topologyName"));
        }
        if (dolfinTopology == null) {
            try {
                dolfinTopology = DolfinBeanUtils.getTopology(dolfinBO.getTopology());
                LOGGER.error("OfertieTopo");
                listSwitches = dolfinTopology.getSwitches();
                model.addAttribute("listSwitches", listSwitches);
                LOGGER.error("GEtting allocated Circuits");
            } catch (RestServiceException ex) {
//                java.util.logging.Logger.getLogger(OfertieController.class.getName()).log(Level.SEVERE, null, ex);
                model.addAttribute("errorMsg", "The topology can not be read or some errors reading circuits.");
            } catch (NullPointerException ex){
                model.addAttribute("errorMsg", "The topology can not be read or some errors reading circuits.");
            }
        }
        return "statistics";
    }

}
