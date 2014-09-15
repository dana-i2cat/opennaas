package org.opennaas.gui.dolfin.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.opennaas.gui.dolfin.beans.UploadedFile;
import org.opennaas.gui.dolfin.bos.OfertieBO;
import org.opennaas.gui.dolfin.entities.settings.Settings;
import org.opennaas.gui.dolfin.validator.FileValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * @author Josep
 */
@Controller
@SessionAttributes("settings")
public class HomeController {

    private static final Logger LOGGER = Logger.getLogger(HomeController.class);
    @Autowired
    protected OfertieBO ofertieBO;
    @Autowired
    protected ReloadableResourceBundleMessageSource messageSource;
    @Autowired
    FileValidator fileValidator;

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
        model.addAttribute(new UploadedFile());
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

    /**
     * Create a form to upload new VI. Redirect to management view
     *
     * @param uploadedFile
     * @param model
     * @param result
     * @param request
     * @param session
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/secure/ofertie/home")
    public String create(@ModelAttribute("uploadedFile") UploadedFile uploadedFile, BindingResult result, ModelMap model, HttpServletRequest request,
            HttpSession session) {

        MultipartFile file = uploadedFile.getFile();
        fileValidator.validate(uploadedFile, result);

        if (result.hasErrors()) {
            model.addAttribute("errorMsg", "Some error with the uploaded file.");
            return "home";
        }
        File newFile;
        try {
            InputStream inputStream = file.getInputStream();
            LOGGER.error("Path" + request.getRealPath(""));
//            File dir = new File(request.getRealPath("") + "/resources/files/");
//            dir.mkdir();

            // inputStream = file.getInputStream();
            if (file.getSize() > 100000) {
                model.addAttribute("errorMsg", "The file is more bigger than 10 MB.");
                return "home";
            }

//            newFile = new File(request.getRealPath("") + "/resources/files/" + fileName);
            newFile = new File(request.getRealPath("") + "/resources/js/topology/topo.json");
//            if (!newFile.exists()) {
            newFile.createNewFile();
//            }
            OutputStream outputStream = new FileOutputStream(newFile);
            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            session.setAttribute("topologyName", newFile.getPath());
        } catch (IOException e) {
        }
        model.addAttribute("infoMsg", "Topology uploaded.");

        return "home";
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/secure/ofertie/home/{topoName}")
    public String home(@PathVariable("topoName") String topoName, ModelMap model, Locale locale, HttpServletRequest request, HttpSession session) {
        LOGGER.debug("home");
        model.addAttribute(new UploadedFile());
        if ((String) session.getAttribute("topologyName") != null) {
            model.put("topologyName", (String) session.getAttribute("topologyName"));
        }

        File newFile = new File(request.getRealPath("") + "/resources/js/topology/topo.json");
        session.setAttribute("topologyName", newFile.getPath());
        return "home";
    }
}
