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

import com.sun.jersey.api.client.ClientHandlerException;

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
		model.addAttribute("vcpeNetwork", new VCPENetwork());
		return "createVCPENetwork";
	}

	/**
	 * Create a VCPE Network
	 * 
	 * @param vcpeNetwork
	 * @param result
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid VCPENetwork vcpeNetwork, BindingResult result, Model model, Locale locale) {
		if (!result.hasErrors()) {
			vcpeNetworkBO.createVCPENetwork(vcpeNetwork);
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
	@RequestMapping(method = RequestMethod.POST)
	public String delete(String vcpeNetworkName, Model model, Locale locale) {
		vcpeNetworkBO.deleteVCPENetwork(vcpeNetworkName);
		return "createVCPENetwork";
	}

	/**
	 * Create a VCPE Network
	 * 
	 * @param vcpeNetwork
	 * @param result
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String edit(String vcpeNetworkName, Model model, Locale locale) {
		model.addAttribute("vcpeNetwork", vcpeNetworkBO.getVCPENetworkByName(vcpeNetworkName));
		return "createVCPENetwork";
	}

	/**
	 * Handle the ClientHandlerException
	 * 
	 * @param ex
	 * @param request
	 * @return
	 */
	@ExceptionHandler(ClientHandlerException.class)
	public String clientHandlerException(ClientHandlerException ex, HttpServletRequest request) {
		request.setAttribute("exception", ex.getMessage());
		return "exception";
	}
}
