package org.opennaas.gui.vcpe.controllers;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.opennaas.gui.vcpe.bos.VCPENetworkBO;
import org.opennaas.gui.vcpe.entities.VCPENetwork;
import org.opennaas.gui.vcpe.services.rest.RestServiceException;
import org.opennaas.gui.vcpe.utils.model.TemplateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Jordi
 */
@Controller
public class VCPENetworkController {

	private static final Logger						LOGGER	= Logger.getLogger(VCPENetworkController.class);

	@Autowired
	private VCPENetworkBO							vcpeNetworkBO;

	@Autowired
	private ReloadableResourceBundleMessageSource	messageSource;

	@Autowired
	private TemplateUtils							templateUtils;

	/**
	 * Redirect to the form to create a VCPENetwork
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String createForm(Model model) {
		LOGGER.debug("form to create a VCPENetwork");
		model.addAttribute(templateUtils.getDefaultVCPENetwork());
		model.addAttribute("action", new String("create"));
		return "createVCPENetwork";
	}

	/**
	 * Create a VCPE Network
	 * 
	 * @param vcpeNetwork
	 * @param result
	 * @return
	 * @throws RestServiceException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/secure/noc/vcpeNetwork/create")
	public String create(@Valid VCPENetwork vcpeNetwork, BindingResult result, Model model, Locale locale) {
		LOGGER.debug("add entity: " + vcpeNetwork);
		try {
			if (!result.hasErrors()) {
				vcpeNetwork.setId(vcpeNetworkBO.create(vcpeNetwork));
				model.addAttribute("vcpeNetworkList", vcpeNetworkBO.getAllVCPENetworks());
				model.addAttribute("infoMsg", messageSource
						.getMessage("vcpenetwork.create.message.info", null, locale));
			} else {
				model.addAttribute("errorMsg", messageSource
						.getMessage("vcpenetwork.create.message.error", null, locale));
			}
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.create.message.error", null, locale) + ": " + e.getMessage());
		}

		return "listVCPENetwork";
	}

	/**
	 * Edit a VCPE Network
	 * 
	 * @param vcpeNetwork
	 * @param result
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/secure/noc/vcpeNetwork/edit")
	public String edit(String vcpeNetworkId, Model model, Locale locale) {
		LOGGER.debug("edit entity with id: " + vcpeNetworkId);
		try {
			model.addAttribute(vcpeNetworkBO.getById(vcpeNetworkId));
			model.addAttribute("action", new String("update"));
			model.addAttribute("noticeMsg", "Not implemented");
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.edit.message.error", null, locale));
		}
		return "createVCPENetwork";
	}

	/**
	 * Create a VCPE Network
	 * 
	 * @param vcpeNetwork
	 * @param result
	 * @return
	 * @throws RestServiceException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/secure/noc/vcpeNetwork/update")
	public String update(@Valid VCPENetwork vcpeNetwork, BindingResult result, Model model, Locale locale) {
		LOGGER.debug("update entity: " + vcpeNetwork);
		// TODO
		model.addAttribute("noticeMsg", messageSource
				.getMessage("message.info.notimplemented", null, locale));
		return "createVCPENetwork";
	}

	/**
	 * Create a VCPE Network
	 * 
	 * @param vcpeNetwork
	 * @param result
	 * @return
	 * @throws RestServiceException
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/secure/noc/vcpeNetwork/delete")
	public String delete(String vcpeNetworkId, Model model, Locale locale) {
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
		return "listVCPENetwork";
	}

	/**
	 * List all the VCPENetwork
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/secure/vcpeNetwork/list")
	public String list(Model model, Locale locale) {
		LOGGER.debug("list all entities");
		try {
			model.addAttribute("vcpeNetworkList", vcpeNetworkBO.getAllVCPENetworks());
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.list.message.error", null, locale));
		}
		return "listVCPENetwork";
	}

	/**
	 * View a VCPENetwork
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/secure/vcpeNetwork/view")
	public String view(String vcpeNetworkId, Model model, Locale locale) {
		LOGGER.debug("view entity with id: " + vcpeNetworkId);
		try {
			model.addAttribute("vcpenetwork", vcpeNetworkBO.getById(vcpeNetworkId));
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.view.message.error", null, locale));
		}
		return "viewVCPENetwork";
	}

	/**
	 * Redirect to the form to modify the ip's
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/secure/vcpeNetwork/updateIpsForm")
	public String updateIpsForm(String vcpeNetworkId, Model model, Locale locale) {
		LOGGER.debug("updateIpsForm entity with id: " + vcpeNetworkId);
		try {
			model.addAttribute(vcpeNetworkBO.getById(vcpeNetworkId));
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.edit.message.error", null, locale));
		}
		return "updateIpsVCPENetwork";
	}

	/**
	 * Redirect to the form to modify the ip's
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/secure/vcpeNetwork/updateIps")
	public String updateIps(VCPENetwork vcpeNetwork, Model model, Locale locale) {
		LOGGER.debug("updateIps of VCPENetwork" + vcpeNetwork);
		try {
			model.addAttribute(vcpeNetworkBO.updateIps(vcpeNetwork));
			model.addAttribute("infoMsg", messageSource
					.getMessage("vcpenetwork.updateIps.message.info", null, locale));
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.updateIps.message.error", null, locale));
		}
		return "updateIpsVCPENetwork";
	}

	/**
	 * Check if the VLAN is free in the environment
	 * 
	 * @param vlan
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/secure/vcpeNetwork/isVLANFree")
	public @ResponseBody
	String isVLANFree(String vlan, Model model, Locale locale) {
		LOGGER.debug("Check if the VLAN " + vlan + " is free");
		Boolean isFree = false;
		try {
			isFree = vcpeNetworkBO.isVLANFree(vlan);
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.check.ip.message.error", null, locale));
		}
		return isFree.toString();
	}

	/**
	 * Check if the VLAN is free in the environment
	 * 
	 * @param vlan
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/secure/vcpeNetwork/isIPFree")
	public @ResponseBody
	String isIPFree(String ip, Model model, Locale locale) {
		LOGGER.debug("Check if the IP " + ip + " is free");
		Boolean isFree = false;
		try {
			isFree = vcpeNetworkBO.isIPFree(ip);
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.check.ip.message.error", null, locale));
		}
		return isFree.toString();
	}

	/**
	 * Check if the interface is free in the environment
	 * 
	 * @param vlan
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/secure/vcpeNetwork/isInterfaceFree")
	public @ResponseBody
	String isInterfaceFree(String iface, String port, Model model, Locale locale) {
		LOGGER.debug("Check if the interface " + iface + "." + port + " is free");
		Boolean isFree = false;
		try {
			isFree = vcpeNetworkBO.isInterfaceFree(iface, port);
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.check.interface.message.error", null, locale));
		}
		return isFree.toString();
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
}
