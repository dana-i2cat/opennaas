package org.opennaas.gui.vcpe.controllers;

import java.util.Locale;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.opennaas.gui.vcpe.entities.MultipleProviderLogical;
import org.opennaas.gui.vcpe.entities.MultipleProviderPhysical;
import org.opennaas.gui.vcpe.services.rest.RestServiceException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
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
	 * Redirect to the form to create a multiple provider VCPENetwork
	 * 
	 * @param model
	 * @param physical
	 * @param locale
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/secure/noc/vcpeNetwork/multipleProvider/logical")
	public String getLogicalForm(@ModelAttribute("physicalInfrastructure") MultipleProviderPhysical physical, Model model, Locale locale) {
		return super.getLogicalForm(physical, model, locale);
	}

	/**
	 * Create a multiple provider VCPE Network
	 * 
	 * @param logical
	 * @param result
	 * @param model
	 * @return
	 * @throws RestServiceException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/secure/noc/vcpeNetwork/multipleProvider/create")
	public String create(@Valid @ModelAttribute("logicalInfrastructure") MultipleProviderLogical logical, BindingResult result, Model model,
			Locale locale) throws RestServiceException {
		return super.create(logical, result, model, locale);
	}

	/**
	 * Edit a multiple provider VCPE Network
	 * 
	 * @param vcpeNetworkId
	 * @param result
	 * @return
	 */
	@Override
	@RequestMapping(method = RequestMethod.GET, value = "/secure/noc/vcpeNetwork/multipleProvider/edit")
	public String edit(String vcpeNetworkId, Model model, Locale locale) {
		return super.edit(vcpeNetworkId, model, locale);
	}

	/**
	 * Update a multiple provider VCPE Network
	 * 
	 * @param singleProviderLogical
	 * @param result
	 * @param model
	 * @param locale
	 * @return
	 * @throws RestServiceException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/secure/noc/vcpeNetwork/multipleProvider/update")
	public String update(@Valid @ModelAttribute("logicalInfrastructure") MultipleProviderLogical logical,
			BindingResult result, Model model, Locale locale) throws RestServiceException {
		return super.update(logical, result, model, locale);
	}

	/**
	 * Delete a multiple provider VCPE Network
	 * 
	 * @param vcpeNetworkId
	 * @param model
	 * @param locale
	 * @return
	 * @throws RestServiceException
	 */
	@Override
	@RequestMapping(method = RequestMethod.GET, value = "/secure/noc/vcpeNetwork/multipleProvider/delete")
	public String delete(String vcpeNetworkId, Model model, Locale locale) throws RestServiceException {
		return super.delete(vcpeNetworkId, model, locale);
	}

}
