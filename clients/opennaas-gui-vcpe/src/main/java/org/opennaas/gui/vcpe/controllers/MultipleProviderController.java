package org.opennaas.gui.vcpe.controllers;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.opennaas.gui.vcpe.entities.MultipleProviderLogical;
import org.opennaas.gui.vcpe.entities.MultipleProviderPhysical;
import org.opennaas.gui.vcpe.services.rest.RestServiceException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Jordi
 */
@Controller
public class MultipleProviderController extends VCPENetworkController {

	private static final Logger	LOGGER	= Logger.getLogger(MultipleProviderController.class);

	/**
	 * Redirect to the physical view
	 * 
	 * @param templateType
	 * @param model
	 * @param locale
	 * @return
	 */
	@Override
	@RequestMapping(method = RequestMethod.GET, value = "/secure/noc/vcpeNetwork/multipleProvider/physical")
	public String getPhysicalForm(@RequestParam("templateType") String templateType, Model model, Locale locale) {
		return super.getPhysicalForm(templateType, model, locale);
	}

	/**
	 * Redirect to the form to create a single provider VCPENetwork
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/secure/noc/vcpeNetwork/multipleProvider/logical")
	public String getLogicalForm(MultipleProviderPhysical physical, Model model, Locale locale) {
		return super.getLogicalForm(physical, model, locale);
	}

	/**
	 * Create a VCPE Network
	 * 
	 * @param logical
	 * @param result
	 * @return
	 * @throws RestServiceException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/secure/noc/vcpeNetwork/multipleProvider/create")
	public String create(@Valid MultipleProviderLogical logical, BindingResult result, Model model, Locale locale) {
		return super.create(logical, result, model, locale);
	}

	/**
	 * Edit a VCPE Network
	 * 
	 * @param vcpeNetwork
	 * @param result
	 * @return
	 */
	@Override
	@RequestMapping(method = RequestMethod.GET, value = "/secure/noc/vcpeNetwork/multipleProvider/edit")
	public String edit(String vcpeNetworkId, Model model, Locale locale) {
		return super.edit(vcpeNetworkId, model, locale);
	}

	/**
	 * Update a VCPE Network
	 * 
	 * @param singleProviderLogical
	 * @param result
	 * @param model
	 * @param locale
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/secure/noc/vcpeNetwork/multipleProvider/update")
	public String update(@Valid MultipleProviderLogical logical, BindingResult result, Model model, Locale locale) {
		return super.update(logical, result, model, locale);
	}

	/**
	 * Delete a VCPE Network
	 * 
	 * @param vcpeNetworkId
	 * @param model
	 * @param locale
	 * @return
	 */
	@Override
	@RequestMapping(method = RequestMethod.GET, value = "/secure/noc/vcpeNetwork/multipleProvider/delete")
	public String delete(String vcpeNetworkId, Model model, Locale locale) {
		return super.delete(vcpeNetworkId, model, locale);
	}

	/**
	 * Handle the Exception and subclasses
	 * 
	 * @param ex
	 * @param request
	 * @return
	 */
	@Override
	@ExceptionHandler(Exception.class)
	public String exception(Exception ex, HttpServletRequest request) {
		request.setAttribute("exception", ex.getMessage());
		return "exception";
	}
}
