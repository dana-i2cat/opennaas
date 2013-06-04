/**
 * 
 */
package org.opennaas.gui.nfvrouting.controllers;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.opennaas.gui.nfvrouting.bos.VCPENetworkBO;
import org.opennaas.gui.routing.services.rest.RestServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This class has the common mehtods for the different VCPE <br>
 * Spring MVC don't allow two classes (or subclasses) with same url <br>
 * We need this class to call the common code with an unique url <br>
 * 
 * @author Jordi
 */
@Controller
public class IsFreeController {

	private static final Logger						LOGGER	= Logger.getLogger(IsFreeController.class);

	@Autowired
	protected VCPENetworkBO							vcpeNetworkBO;

	@Autowired
	protected ReloadableResourceBundleMessageSource	messageSource;

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

}
