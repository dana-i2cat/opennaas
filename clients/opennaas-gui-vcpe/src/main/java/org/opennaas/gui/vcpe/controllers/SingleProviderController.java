package org.opennaas.gui.vcpe.controllers;

import java.util.Locale;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.opennaas.gui.vcpe.entities.SingleProviderLogical;
import org.opennaas.gui.vcpe.entities.SingleProviderPhysical;
import org.opennaas.gui.vcpe.services.rest.RestServiceException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
	 * Redirect to the physical view
	 * 
	 * @param templateType
	 * @param model
	 * @param locale
	 * @return
	 */
	@Override
	@RequestMapping(method = RequestMethod.GET, value = "/secure/noc/vcpeNetwork/singleProvider/physical")
	public String getPhysicalForm(@RequestParam("templateType") String templateType, Model model, Locale locale) {
		return super.getPhysicalForm(templateType, model, locale);
	}

	/**
	 * Redirect to the form to create a single provider VCPENetwork
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/secure/noc/vcpeNetwork/singleProvider/logical")
	public String getLogicalForm(SingleProviderPhysical physical, Model model, Locale locale) {
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
	@RequestMapping(method = RequestMethod.POST, value = "/secure/noc/vcpeNetwork/singleProvider/create")
	public String create(@Valid SingleProviderLogical logical, BindingResult result, Model model, Locale locale) {
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
	@RequestMapping(method = RequestMethod.GET, value = "/secure/noc/vcpeNetwork/singleProvider/edit")
	public String edit(String vcpeNetworkId, Model model, Locale locale) {
		return super.edit(vcpeNetworkId, model, locale);
	}

	/**
	 * Update a VCPE Network
	 * 
	 * @param logical
	 * @param result
	 * @param model
	 * @param locale
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/secure/noc/vcpeNetwork/singleProvider/update")
	public String update(@Valid SingleProviderLogical logical, BindingResult result, Model model, Locale locale) {
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
	@RequestMapping(method = RequestMethod.GET, value = "/secure/noc/vcpeNetwork/singleProvider/delete")
	public String delete(String vcpeNetworkId, Model model, Locale locale) {
		return super.delete(vcpeNetworkId, model, locale);
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
		return updateIpsFormSecure(vcpeNetworkId, model, locale);
	}

	/**
	 * Redirect to the form to modify the ip's. Noc entry method
	 * 
	 * @param vcpeNetworkId
	 * @param model
	 * @param locale
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/secure/noc/vcpeNetwork/singleProvider/updateIpsForm")
	public String updateIpsFormSecure(String vcpeNetworkId, Model model, Locale locale) {
		LOGGER.debug("updateIpsForm entity with id: " + vcpeNetworkId);
		try {
			model.addAttribute("logicalInfrastructure", vcpeNetworkBO.getById(vcpeNetworkId));
			model.addAttribute("vcpeNetworkList", vcpeNetworkBO.getAllVCPENetworks());
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.edit.message.error", null, locale));
		}
		return "updateIpsVCPENetwork";
	}

	/**
	 * Redirect to the form to modify the ip's
	 * 
	 * @param logical
	 * @param model
	 * @param locale
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/secure/vcpeNetwork/singleProvider/updateIps")
	public String updateIps(SingleProviderLogical logical, Model model, Locale locale) {
		return updateIpsSecure(logical, model, locale);
	}

	/**
	 * Redirect to the form to modify the ip's
	 * 
	 * @param logical
	 * @param model
	 * @param locale
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/secure/noc/vcpeNetwork/singleProvider/updateIps")
	public String updateIpsSecure(SingleProviderLogical logical, Model model, Locale locale) {
		LOGGER.debug("update Ips of VCPENetwork: " + logical);
		try {
			vcpeNetworkBO.updateIps(logical);
			model.addAttribute("logicalInfrastructure", vcpeNetworkBO.getById(logical.getId()));
			model.addAttribute("vcpeNetworkList", vcpeNetworkBO.getAllVCPENetworks());
			model.addAttribute("infoMsg", messageSource
					.getMessage("vcpenetwork.updateIps.message.info", null, locale));
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.updateIps.message.error", null, locale));
		}
		return "updateIpsVCPENetwork";
	}

	/**
	 * Method for the client user to update the VRRP Ip
	 * 
	 * @param singleProviderLogical
	 * @param model
	 * @param locale
	 * @return
	 * @throws RestServiceException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/secure/vcpeNetwork/singleProvider/updateVRRPIp")
	public String updateVRRPIpClient(SingleProviderLogical logical, Model model, Locale locale) throws RestServiceException {
		return updateVRRPIp(logical, model, locale);
	}

	/**
	 * Modifiy the VRRP Ip
	 * 
	 * @param singleProviderLogical
	 * @param model
	 * @param locale
	 * @return
	 * @throws RestServiceException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/secure/noc/vcpeNetwork/singleProvider/updateVRRPIp")
	public String updateVRRPIp(SingleProviderLogical logical, Model model, Locale locale) throws RestServiceException {
		LOGGER.debug("update VRRP ip of VCPENetwork: " + logical);
		try {
			vcpeNetworkBO.updateVRRPIp(logical);
			model.addAttribute("logicalInfrastructure", vcpeNetworkBO.getById(logical.getId()));
			model.addAttribute("vcpeNetworkList", vcpeNetworkBO.getAllVCPENetworks());
			model.addAttribute("infoMsg", messageSource
					.getMessage("vcpenetwork.updateVRRPIp.message.info", null, locale));
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.updateVRRPIp.message.error", null, locale));
		}
		return "logicalForm";
	}

	/**
	 * Change the priority in VRRP
	 * 
	 * @param singleProviderLogical
	 * @param model
	 * @param locale
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/secure/noc/vcpeNetwork/singleProvider/changeVRRPPriority")
	public String changeVRRPPriority(SingleProviderLogical logical, Model model, Locale locale) {
		LOGGER.debug("change priority VRRP of VCPENetwork: " + logical);
		try {
			model.addAttribute("action", "update");
			model.addAttribute("vcpeNetworkList", vcpeNetworkBO.getAllVCPENetworks());
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
