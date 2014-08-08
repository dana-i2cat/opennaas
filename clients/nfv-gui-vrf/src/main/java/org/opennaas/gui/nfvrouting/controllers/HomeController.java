package org.opennaas.gui.nfvrouting.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.opennaas.gui.nfvrouting.beans.UploadedFile;
import org.opennaas.gui.nfvrouting.bos.NFVRoutingBO;
import org.opennaas.gui.nfvrouting.entities.settings.Settings;
import org.opennaas.gui.nfvrouting.entities.topology.GuiTopology;
import org.opennaas.gui.nfvrouting.services.rest.RestServiceException;
import org.opennaas.gui.nfvrouting.utils.model.OpennaasBeanUtils;
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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Josep
 */
@Controller
@SessionAttributes("settings")
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
        Settings settings = (Settings) session.getAttribute("settings");
        if(settings == null){
            settings = new Settings();
            settings.setRoutingType(nfvRoutingBO.getONRouteMode());
        }
	model.addAttribute("settings", settings);
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
    String getFlowTable(@PathVariable("dpid") String dpid, Model model, Locale locale, HttpSession session) {
        LOGGER.debug("Request switch information of switch with the following DPID: " + dpid);
        String response = "";
        try {
            response = nfvRoutingBO.getFlowTable(dpid);
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
        InputStream inputStream;
        OutputStream outputStream;

        MultipartFile file = uploadedFile.getFile();
        fileValidator.validate(uploadedFile, result);

        if (result.hasErrors()) {
            model.addAttribute("errorMsg", "Some error with the uploaded file.");
            return "home";
        }
        File newFile = null;
        try {
            inputStream = file.getInputStream();
            LOGGER.error("Path" + request.getRealPath(""));
//            File dir = new File(request.getRealPath("") + "/resources/files/");
//            dir.mkdir();

            // inputStream = file.getInputStream();
            if (file.getSize() > 100000) {
                model.addAttribute("errorMsg", "The file is more bigger than 10 MB.");
                return "home";
            }

//            newFile = new File(request.getRealPath("") + "/resources/files/" + fileName);
            newFile = new File(request.getRealPath("") + "/resources/js/topology/topology.json");
//            if (!newFile.exists()) {
            newFile.createNewFile();
//            }
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
    
    @RequestMapping(method = RequestMethod.GET, value = "/secure/nfvRouting/home/{topoName}")
    public String home(@PathVariable("topoName") String topoName, ModelMap model, Locale locale, HttpServletRequest request, HttpSession session) {
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
        } catch (RestServiceException e) {
            model.addAttribute("errorMsg", "OpenNaaS is not started");
            return "home";
        }
        File newFile = new File(request.getRealPath("") + "/resources/js/topology/topology.json");
        session.setAttribute("topologyName", newFile.getPath());
        return "home";
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/secure/nfvRouting/home/opennaasTopology")
    public String loadOpenNaaSTopology(ModelMap model, Locale locale, HttpServletRequest request, HttpSession session) {
//        InputStream inputStream;
        String topologyJSON = topologyToGuiTopology();
        if(topologyJSON.equals("")){
            model.addAttribute("errorMsg", "Generic Network not initiated or not contain a topoology. ");
             return "home";
        }
        try {
//            inputStream = new ByteArrayInputStream(topologyJSON.getBytes(StandardCharsets.UTF_8));
            LOGGER.error("Path" + request.getRealPath(""));
           File newFile = new File(request.getRealPath("") + "/resources/js/topology/topology.json");
            newFile.createNewFile();
            FileUtils.writeStringToFile(newFile, topologyJSON);
            session.setAttribute("topologyName", newFile.getPath());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        model.addAttribute("infoMsg", "Topology uploaded.");

//        nfvRoutingBO.uploadTopology(newFile.getAbsolutePath());
        return "home";
    }
    
    
    @RequestMapping(method = RequestMethod.GET, value = "/secure/nfvRouting/topologyToGUI")
    public @ResponseBody String topologyToGuiTopology(ModelMap model) {
        LOGGER.debug("Load OpenNaaS Topology");
        String topo = "";
        try {
            String response = nfvRoutingBO.getRouteTable(4);
            if (response.equals("OpenNaaS is not started")) {
                model.addAttribute("errorMsg", response);
            }
            topo += topologyToGuiTopology();
//            String response = nfvRoutingBO.getInfoControllers();
//            model.addAttribute("json", response);
        } catch (RestServiceException e) {
            model.addAttribute("errorMsg", "OpenNaaS is not started");
            //return "errror";
        }
        
        return topo;
    }
    
    public String topologyToGuiTopology() {
        String response = "";
        GuiTopology guiTop = null;
        Map<String, String> possibleHosts;
        try {
            guiTop = OpennaasBeanUtils.convertONTopologyToGuiTopology(nfvRoutingBO.getTopology());
//            possibleHosts = OpennaasBeanUtils.findUnusedPorts(nfvRoutingBO.getTopology(), guiTop);
//            guiTop.setPosibleHosts(possibleHosts);
        } catch (RestServiceException ex) {
            java.util.logging.Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
        response = OpennaasBeanUtils.mapperObjectsToJSON(guiTop);
        return response;
    }
}
