package org.opennaas.gui.vcpe.controllers;

import java.util.Locale;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.opennaas.gui.vcpe.entities.SingleProviderLogical;
import org.opennaas.gui.vcpe.entities.SingleProviderPhysical;
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
public class SingleProviderController extends VCPENetworkController {

	private static final Logger	LOGGER	= Logger.getLogger(SingleProviderController.class);

	/**
	 * Redirect to the single provider physical view
	 * 
	 * @param templateType
	 * @param model
	 * @param locale
	 * @return
	 */
	@Override
	@RequestMapping(method = RequestMethod.GET, value = "/secure/admin/vcpeNetwork/singleProvider/physical")
	public String getPhysicalForm(@RequestParam("templateType") String templateType, Model model, Locale locale) {
		return super.getPhysicalForm(templateType, model, locale);
	}

	/**
	 * Redirect to the form to create a single provider VCPENetwork
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/secure/admin/vcpeNetwork/singleProvider/logical")
	public String getLogicalForm(@ModelAttribute("physicalInfrastructure") SingleProviderPhysical physical, Model model, Locale locale) {
		return super.getLogicalForm(physical, model, locale);
	}

	/**
	 * Create a single provider VCPE Network
	 * 
	 * @param logical
	 * @param result
	 * @return
	 * @throws RestServiceException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/secure/admin/vcpeNetwork/singleProvider/create")
	public String create(@Valid @ModelAttribute("logicalInfrastructure") SingleProviderLogical logical,
			BindingResult result, Model model, Locale locale, HttpSession session) {
		return super.create(logical, result, model, locale, session);
	}

	/**
	 * Delete a single provider VCPE Network
	 * 
	 * @param vcpeNetworkId
	 * @param model
	 * @param locale
	 * @return
	 */
	@Override
	@RequestMapping(method = RequestMethod.GET, value = "/secure/admin/vcpeNetwork/singleProvider/delete")
	public String delete(String vcpeNetworkId, Model model, Locale locale, HttpSession session) {
		return super.delete(vcpeNetworkId, model, locale, session);
	}

	/**
	 * Edit a single provider VCPE Network
	 * 
	 * @param vcpeNetwork
	 * @param result
	 * @return
	 */
	@Override
	@RequestMapping(method = RequestMethod.GET, value = "/secure/vcpeNetwork/singleProvider/edit")
	public String edit(String vcpeNetworkId, Model model, Locale locale) {
		return super.edit(vcpeNetworkId, model, locale);
	}

	/**
	 * Update a single provider VCPE Network (admin user)
	 * 
	 * @param logical
	 * @param result
	 * @param model
	 * @param locale
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/secure/admin/vcpeNetwork/singleProvider/update")
	public String updateAdmin(@Valid @ModelAttribute("logicalInfrastructure") SingleProviderLogical logical,
			BindingResult result, Model model, Locale locale, HttpSession session) {
		return update(logical, result, model, locale, session);
	}

	/**
	 * Update a single provider VCPE Network
	 * 
	 * @param logical
	 * @param result
	 * @param model
	 * @param locale
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/secure/vcpeNetwork/singleProvider/update")
	public String update(@Valid @ModelAttribute("logicalInfrastructure") SingleProviderLogical logical,
			BindingResult result, Model model, Locale locale, HttpSession session) {
		return super.update(logical, result, model, locale, session);
	}

	/**
	 * Redirect to the form to modify the ip's. Client entry method
	 * 
	 * @param vcpeNetworkId
	 * @param model
	 * @param locale
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/secure/vcpeNetwork/singleProvider/updateIpsForm")
	public String updateIpsForm(String vcpeNetworkId, Model model, Locale locale) {
		LOGGER.debug("updateIpsForm entity with id: " + vcpeNetworkId);
		try {
			model.addAttribute("logicalInfrastructure", vcpeNetworkBO.getById(vcpeNetworkId));
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.edit.message.error", null, locale));
		}
		return "updateIpsVCPENetwork";
	}

	/**
	 * Update the ip's
	 * 
	 * @param logical
	 * @param model
	 * @param locale
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/secure/vcpeNetwork/singleProvider/updateIps")
	public String updateIps(@ModelAttribute("logicalInfrastructure") SingleProviderLogical logical, Model model, Locale locale) {
		LOGGER.debug("update Ips of VCPENetwork: " + logical);
		try {
			vcpeNetworkBO.updateIps(logical);
			model.addAttribute("logicalInfrastructure", vcpeNetworkBO.getById(logical.getId()));
			model.addAttribute("infoMsg", messageSource
					.getMessage("vcpenetwork.updateIps.message.info", null, locale));
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.updateIps.message.error", null, locale));
		}
		return "updateIpsVCPENetwork";
	}

	/**
	 * Modify the VRRP IP address (admin user)
	 * 
	 * @param singleProviderLogical
	 * @param model
	 * @param locale
	 * @return
	 * @throws RestServiceException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/secure/admin/vcpeNetwork/singleProvider/updateVRRPIp")
	public String updateVRRPIpAdmin(@ModelAttribute("logicalInfrastructure") SingleProviderLogical logical,
			Model model, Locale locale) {
		return updateVRRPIp(logical, model, locale);
	}

	/**
	 * Modify the VRRP IP address (NOC user)
	 * 
	 * @param singleProviderLogical
	 * @param model
	 * @param locale
	 * @return
	 * @throws RestServiceException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/secure/noc/vcpeNetwork/singleProvider/updateVRRPIp")
	public String updateVRRPIpNoc(@ModelAttribute("logicalInfrastructure") SingleProviderLogical logical,
			Model model, Locale locale) {
		return updateVRRPIp(logical, model, locale);
	}

	/**
	 * Modify the VRRP IP address
	 * 
	 * @param singleProviderLogical
	 * @param model
	 * @param locale
	 * @return
	 * @throws RestServiceException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/secure/vcpeNetwork/singleProvider/updateVRRPIp")
	public String updateVRRPIp(@ModelAttribute("logicalInfrastructure") SingleProviderLogical logical,
			Model model, Locale locale) {
		LOGGER.debug("update VRRP ip of VCPENetwork: " + logical);
		try {
			vcpeNetworkBO.updateVRRPIp(logical);
			model.addAttribute("action", "update");
			model.addAttribute("logicalInfrastructure", vcpeNetworkBO.getById(logical.getId()));
			model.addAttribute("infoMsg", messageSource
					.getMessage("vcpenetwork.updateVRRPIp.message.info", null, locale));
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.updateVRRPIp.message.error", null, locale));
		}
		return "logicalForm";
	}

	/**
	 * Change the priority in VRRP (admin user)
	 * 
	 * @param singleProviderLogical
	 * @param model
	 * @param locale
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/secure/admin/vcpeNetwork/singleProvider/changeVRRPPriority")
	public String changeVRRPPriorityAdmin(@ModelAttribute("logicalInfrastructure") SingleProviderLogical logical,
			Model model, Locale locale) {
		return changeVRRPPriority(logical, model, locale);
	}

	/**
	 * Change the priority in VRRP (NOC user)
	 * 
	 * @param singleProviderLogical
	 * @param model
	 * @param locale
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/secure/noc/vcpeNetwork/singleProvider/changeVRRPPriority")
	public String changeVRRPPriorityNoc(@ModelAttribute("logicalInfrastructure") SingleProviderLogical logical,
			Model model, Locale locale) {
		return changeVRRPPriority(logical, model, locale);
	}

	/**
	 * Change the priority in VRRP
	 * 
	 * @param singleProviderLogical
	 * @param model
	 * @param locale
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/secure/vcpeNetwork/singleProvider/changeVRRPPriority")
	public String changeVRRPPriority(@ModelAttribute("logicalInfrastructure") SingleProviderLogical logical,
			Model model, Locale locale) {
		LOGGER.debug("change priority VRRP of VCPENetwork: " + logical);
		try {
			model.addAttribute("action", "update");
			model.addAttribute("logicalInfrastructure", vcpeNetworkBO.changeVRRPPriority(logical));
			model.addAttribute("infoMsg", messageSource
					.getMessage("vcpenetwork.changeVRRPPriority.message.info", null, locale));
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.changeVRRPPriority.message.error", null, locale));
		}
		return "logicalForm";
	}

}
