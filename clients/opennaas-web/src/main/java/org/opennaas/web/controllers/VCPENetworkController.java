package org.opennaas.web.controllers;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.opennaas.web.bos.VCPENetworkBO;
import org.opennaas.web.entities.VCPENetwork;
import org.opennaas.web.services.rest.RestServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

	/**
	 * Redirect to the form to create a VCPENetwork
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String getCreateForm(Model model) {
		LOGGER.debug("form to create a VCPENetwork");
		model.addAttribute(new VCPENetwork());
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
				model.addAttribute("infoMsg", messageSource
						.getMessage("vcpenetwork.create.message.info", null, locale));
			} else {
				model.addAttribute("errorMsg", messageSource
						.getMessage("vcpenetwork.create.message.error", null, locale));
			}
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.create.message.error", null, locale) + ": " + e.getResponse());
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
	@RequestMapping(method = RequestMethod.GET, value = "/secure/noc/vcpeNetwork/delete")
	public String delete(String vcpeNetworkId, Model model, Locale locale) {
		LOGGER.debug("delete entity with id: " + vcpeNetworkId);
		try {
			vcpeNetworkBO.delete(vcpeNetworkId);
			model.addAttribute("infoMsg", messageSource
					.getMessage("vcpenetwork.delete.message.info", null, locale));
			model.addAttribute("vcpeNetworkList", vcpeNetworkBO.getAll());
		} catch (RestServiceException e) {
			model.addAttribute("infoMsg", messageSource
					.getMessage("vcpenetwork.delete.message.error", null, locale));
		}
		return "listVCPENetwork";
	}

	/**
	 * Create a VCPE Network
	 * 
	 * @param vcpeNetwork
	 * @param result
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/secure/noc/vcpeNetwork/edit")
	public String edit(String vcpeNetworkId, Model model, Locale locale) {
		LOGGER.debug("edit entity with id: " + vcpeNetworkId);
		model.addAttribute(vcpeNetworkBO.getById(vcpeNetworkId));
		return "createVCPENetwork";
	}

	/**
	 * List all the VCPENetwork
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/secure/vcpeNetwork/list")
	public String list(Model model) {
		LOGGER.debug("list all entities");
		model.addAttribute("vcpeNetworkList", vcpeNetworkBO.getAll());
		return "listVCPENetwork";
	}

	/**
	 * View a VCPENetwork
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/secure/vcpeNetwork/view")
	public String view(Model model) {
		// TODO
		LOGGER.debug("view all entities");
		return "viewVCPENetwork";
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
