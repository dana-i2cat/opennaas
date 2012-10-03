package org.opennaas.web.controllers;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.opennaas.web.bos.VCPENetworkBO;
import org.opennaas.web.entities.VCPENetwork;
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
@RequestMapping(value = "/vcpeNetwork")
public class VCPENetworkController {

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
		model.addAttribute(new VCPENetwork());
		return "createVCPENetwork";
	}

	/**
	 * Create a VCPE Network
	 * 
	 * @param vcpeNetwork
	 * @param result
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "create")
	public String create(@Valid VCPENetwork vcpeNetwork, BindingResult result, Model model, Locale locale) {
		if (!result.hasErrors()) {
			String vcpeNetworkId = vcpeNetworkBO.create(vcpeNetwork);
			vcpeNetwork.setId(vcpeNetworkId);
			vcpeNetworkBO.start(vcpeNetworkId);
			model.addAttribute("infoMsg", messageSource
					.getMessage("vcpenetwork.create.message.info", null, locale));
		} else {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.create.message.error", null, locale));
		}
		return "createVCPENetwork";
	}

	/**
	 * Create a VCPE Network
	 * 
	 * @param vcpeNetwork
	 * @param result
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "delete")
	public String delete(String vcpeNetworkId, Model model, Locale locale) {
		vcpeNetworkBO.stop(vcpeNetworkId);
		vcpeNetworkBO.delete(vcpeNetworkId);
		return "createVCPENetwork";
	}

	/**
	 * Create a VCPE Network
	 * 
	 * @param vcpeNetwork
	 * @param result
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "edit")
	public String edit(String vcpeNetworkId, Model model, Locale locale) {
		model.addAttribute(vcpeNetworkBO.getVCPENetwork(vcpeNetworkId));
		return "createVCPENetwork";
	}

	/**
	 * Redirect to the form to create a VCPENetwork
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "list")
	public String list(Model model) {
		model.addAttribute("vcpeNetworkList", vcpeNetworkBO.getAllVCPENetwork());
		return "listVCPENetwork";
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
