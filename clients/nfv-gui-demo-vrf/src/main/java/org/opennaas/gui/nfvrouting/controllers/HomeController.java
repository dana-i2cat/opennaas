package org.opennaas.gui.nfvrouting.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.opennaas.gui.nfvrouting.beans.UploadedFile;
import org.opennaas.gui.nfvrouting.bos.NFVRoutingBO;
import org.opennaas.gui.nfvrouting.validator.FileValidator;
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
    @RequestMapping(method = RequestMethod.GET, value = "/secure/nfvRouting/home")
    public String home(ModelMap model, Locale locale, HttpSession session) {
        LOGGER.debug("home");
        model.addAttribute(new UploadedFile());
        if ((String) session.getAttribute("topologyName") != null) {
            model.put("topologyName", (String) session.getAttribute("topologyName"));
        }

        try {
            String response = nfvRoutingBO.getRouteTable(4);
            if (response.equals("OpenNaaS is not started")) {
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
     *
     * @param dpid
     * @param model
     * @param locale
     * @param session
     * @return Flow table in xml representation
     */
    @RequestMapping(method = RequestMethod.GET, value = "/secure/nfvRouting/switchInfo/{dpid}")
    public @ResponseBody
    String getSwInfo(@PathVariable("dpid") String dpid, Model model, Locale locale, HttpSession session) {
        LOGGER.debug("Request switch information of switch with the following DPID: " + dpid);
        String response = "";
        try {
            response = nfvRoutingBO.getSwInfo(dpid);
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
    @RequestMapping(method = RequestMethod.POST, value = "/secure/nfvRouting/home")
    public String create(@ModelAttribute("uploadedFile") UploadedFile uploadedFile, BindingResult result, ModelMap model, HttpServletRequest request,
            HttpSession session) {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        MultipartFile file = uploadedFile.getFile();
        fileValidator.validate(uploadedFile, result);

        String fileName = file.getOriginalFilename();

        if (result.hasErrors()) {
            model.addAttribute("errorMsg", "Some error with the uploaded file.");
            return "home";
        }
File newFile = null;
        try {
            inputStream = file.getInputStream();
            LOGGER.error("Path"+request.getRealPath(""));
            File dir = new File(request.getRealPath("") + "/resources/files/");
            dir.mkdir();

            // inputStream = file.getInputStream();
            if (file.getSize() > 100000) {
                model.addAttribute("errorMsg", "The file is more bigger than 10 MB.");
                return "uploadVI";
            }

//            newFile = new File(request.getRealPath("") + "/resources/files/" + fileName);
            newFile = new File(request.getRealPath("") + "/resources/js/topology/topo.json");
            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            outputStream = new FileOutputStream(newFile);
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        model.addAttribute("infoMsg", "Topology uploaded.");
        
//        nfvRoutingBO.uploadTopology(newFile.getAbsolutePath());
        session.setAttribute("topologyName", newFile.getPath());
        return "home";
    }
}
