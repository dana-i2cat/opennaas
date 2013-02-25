package org.opennaas.gui.vcpe.controllers;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.opennaas.gui.vcpe.bos.VCPENetworkBO;
import org.opennaas.gui.vcpe.entities.LogicalInfrastructure;
import org.opennaas.gui.vcpe.entities.PhysicalInfrastructure;
import org.opennaas.gui.vcpe.services.rest.RestServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Jordi
 */
@Controller
public abstract class VCPENetworkController {

	private static final Logger						LOGGER	= Logger.getLogger(VCPENetworkController.class);

	@Autowired
	protected VCPENetworkBO							vcpeNetworkBO;

	@Autowired
	protected ReloadableResourceBundleMessageSource	messageSource;

	/**
	 * Redirect to home
	 * 
	 * @param model
	 * @param locale
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/secure/vcpeNetwork/home")
	public String home(Model model, Locale locale) {
		LOGGER.debug("home");
		try {
			model.addAttribute("vcpeNetworkList", vcpeNetworkBO.getAllVCPENetworks());
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.list.message.error", null, locale));
		}
		return "home";
	}

	/**
	 * Redirect to the physical view
	 * 
	 * @param templateType
	 * @param model
	 * @param locale
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/secure/noc/vcpeNetwork/physical")
	public String getPhysicalForm(@RequestParam("templateType") String templateType, Model model, Locale locale) {
		LOGGER.debug("home");
		try {
			model.addAttribute("vcpeNetworkList", vcpeNetworkBO.getAllVCPENetworks());
			model.addAttribute("physicalInfrastructure", vcpeNetworkBO.getPhysicalInfrastructure(templateType));
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.list.message.error", null, locale));
		}
		return "physicalForm";
	}

	/**
	 * Check if the VLAN is free in the environment
	 * 
	 * @param vcpeId
	 * @param router
	 * @param vlan
	 * @param ifaceName
	 * @param model
	 * @param locale
	 * @return true if is free
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/secure/vcpeNetwork/isVLANFree")
	public @ResponseBody
	String isVLANFree(String vcpeId, String router, String vlan, String ifaceName, Model model, Locale locale) {
		LOGGER.debug("Check if the VLAN: " + vlan + " is free in the ifaceName: " + ifaceName + ". The vcpeID: " + vcpeId);
		Boolean isFree = false;
		try {
			isFree = vcpeNetworkBO.isVLANFree(vcpeId, router, vlan, ifaceName);
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.check.ip.message.error", null, locale));
		}
		return isFree.toString();
	}

	/**
	 * Check if the IP is free in the environment
	 * 
	 * @param vcpeId
	 * @param router
	 * @param ip
	 * @param model
	 * @param locale
	 * @return true if is free
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/secure/vcpeNetwork/isIPFree")
	public @ResponseBody
	String isIPFree(String vcpeId, String router, String ip, Model model, Locale locale) {
		LOGGER.debug("Check if the IP: " + ip + " is free. The vcpeID: " + vcpeId);
		Boolean isFree = false;
		try {
			isFree = vcpeNetworkBO.isIPFree(vcpeId, router, ip);
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.check.ip.message.error", null, locale));
		}
		return isFree.toString();
	}

	/**
	 * Check if the Interface is free in the environment
	 * 
	 * @param vcpeId
	 * @param router
	 * @param iface
	 * @param port
	 * @param model
	 * @param locale
	 * @return true if is free
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/secure/vcpeNetwork/isInterfaceFree")
	public @ResponseBody
	String isInterfaceFree(String vcpeId, String router, String iface, String port, Model model, Locale locale) {
		LOGGER.debug("Check if the Interface: " + iface + "." + port + " is free. The vcpeID: " + vcpeId);
		Boolean isFree = false;
		try {
			isFree = vcpeNetworkBO.isInterfaceFree(vcpeId, router, iface, port);
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.check.interface.message.error", null, locale));
		}
		return isFree.toString();
	}

	/**
	 * Redirect to the form to create a VCPENetwork
	 * 
	 * @param physical
	 * @param model
	 * @param locale
	 * @return
	 */
	public String getLogicalForm(PhysicalInfrastructure physical, Model model, Locale locale) {
		LOGGER.debug("form to create a VCPENetwork");
		try {
			model.addAttribute("logicalInfrastructure", vcpeNetworkBO.getSuggestVCPENetwork(physical));
			model.addAttribute("vcpeNetworkList", vcpeNetworkBO.getAllVCPENetworks());
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.create.message.error", null, locale) + ": " + e.getMessage());
		} finally {
			model.addAttribute("action", new String("create"));
		}
		return "logicalForm";
	}

	/**
	 * Handle the Exception and subclasses
	 * 
	 * @param ex
	 * @param request
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	public String exception(Exception ex, HttpServletRequest request) {
		request.setAttribute("exception", ex.getMessage());
		return "exception";
	}

	/**
	 * Create a VCPE Network
	 * 
	 * @param logicalInfrastructure
	 * @param result
	 * @param model
	 * @param locale
	 * @return
	 */
	protected String create(@Valid LogicalInfrastructure logicalInfrastructure, BindingResult result, Model model, Locale locale) {
		LOGGER.debug("add entity: " + logicalInfrastructure);
		try {
			if (!result.hasErrors()) {
				String vcpeNetworkId = vcpeNetworkBO.create(logicalInfrastructure);
				model.addAttribute("logicalInfrastructure", vcpeNetworkBO.getById(vcpeNetworkId));
				model.addAttribute("infoMsg", messageSource
						.getMessage("vcpenetwork.create.message.info", null, locale));
				model.addAttribute("action", new String("update"));
			} else {
				model.addAttribute("errorMsg", messageSource
						.getMessage("vcpenetwork.create.message.error.fields", null, locale));
				model.addAttribute("action", new String("create"));
			}
			model.addAttribute("vcpeNetworkList", vcpeNetworkBO.getAllVCPENetworks());
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.create.message.error", null, locale) + ": " + e.getMessage());
			model.addAttribute("action", new String("create"));
		}
		return "logicalForm";
	}

	/**
	 * Edit a VCPE Network
	 * 
	 * @param vcpeNetworkId
	 * @param model
	 * @param locale
	 * @return
	 */
	protected String edit(String vcpeNetworkId, Model model, Locale locale) {
		LOGGER.debug("edit entity with id: " + vcpeNetworkId);
		try {
			model.addAttribute("vcpeNetworkList", vcpeNetworkBO.getAllVCPENetworks());
			model.addAttribute("logicalInfrastructure", vcpeNetworkBO.getById(vcpeNetworkId));
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.edit.message.error", null, locale));
		} finally {
			model.addAttribute("action", new String("update"));
		}
		return "logicalForm";
	}

	/**
	 * Update a VCPE Network
	 * 
	 * @param logicalInfrastructure
	 * @param result
	 * @param model
	 * @param locale
	 * @return
	 */
	protected String update(@Valid LogicalInfrastructure logicalInfrastructure, BindingResult result, Model model, Locale locale) {
		LOGGER.debug("update entity: " + logicalInfrastructure);
		try {
			if (!result.hasErrors()) {
				LOGGER.debug("removing the old environment");
				vcpeNetworkBO.delete(logicalInfrastructure.getId());
				LOGGER.debug("create the new environment");
				String vcpeNetworkId = vcpeNetworkBO.create(logicalInfrastructure);
				model.addAttribute("logicalInfrastructure", vcpeNetworkBO.getById(vcpeNetworkId));
				model.addAttribute("infoMsg", messageSource.getMessage("vcpenetwork.update.message.info", null, locale));
				model.addAttribute("vcpeNetworkList", vcpeNetworkBO.getAllVCPENetworks());
			} else {
				model.addAttribute("errorMsg", messageSource
						.getMessage("vcpenetwork.update.message.error.fields", null, locale));
			}
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.update.message.error", null, locale) + ": " + e.getMessage());
		} finally {
			model.addAttribute("action", new String("update"));
		}
		return "logicalForm";
	}

	/**
	 * Delete a VCPE Network
	 * 
	 * @param vcpeNetworkId
	 * @param model
	 * @param locale
	 * @return
	 */
	protected String delete(String vcpeNetworkId, Model model, Locale locale) {
		LOGGER.debug("delete entity with id: " + vcpeNetworkId);
		try {
			vcpeNetworkBO.delete(vcpeNetworkId);
			model.addAttribute("infoMsg", messageSource
					.getMessage("vcpenetwork.delete.message.info", null, locale));
			model.addAttribute("vcpeNetworkList", vcpeNetworkBO.getAllVCPENetworks());
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.delete.message.error", null, locale));
		}
		return "home";
	}

}
