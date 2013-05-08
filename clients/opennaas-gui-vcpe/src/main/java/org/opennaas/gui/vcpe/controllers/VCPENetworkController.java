package org.opennaas.gui.vcpe.controllers;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

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
	 * Redirect to the physical view
	 * 
	 * @param templateType
	 * @param model
	 * @param locale
	 * @return
	 */
	public String getPhysicalForm(String templateType, Model model, Locale locale) {
		LOGGER.debug("get the Physical Form");
		String view = "physicalForm";
		try {
			model.addAttribute("physicalInfrastructure", vcpeNetworkBO.getPhysicalInfrastructure(templateType));
		} catch (RestServiceException e) {
			view = "home";
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.list.message.error", null, locale));
		}
		return view;
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
		String view = "logicalForm";
		try {
			model.addAttribute("logicalInfrastructure", vcpeNetworkBO.getLogicalInfrastructure(physical));
			model.addAttribute("usersNOC", getUsersNOC());
			model.addAttribute("usersClient", getUsersClient());
			model.addAttribute("action", new String("create"));
		} catch (RestServiceException e) {
			view = "physicalForm";
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.create.message.error", null, locale) + ": " + e.getMessage());
		}
		return view;
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
	protected String create(LogicalInfrastructure logicalInfrastructure, BindingResult result, Model model, Locale locale, HttpSession session) {
		LOGGER.debug("add entity: " + logicalInfrastructure);
		try {
			if (!result.hasErrors()) {
				String vcpeNetworkId = vcpeNetworkBO.create(logicalInfrastructure);
				model.addAttribute("logicalInfrastructure", vcpeNetworkBO.getById(vcpeNetworkId));
				model.addAttribute("infoMsg", messageSource.getMessage("vcpenetwork.create.message.info", null, locale));
				model.addAttribute("action", new String("update"));
				session.setAttribute("vcpeNetworkList", vcpeNetworkBO.getAllVCPENetworks());
			} else {
				model.addAttribute("errorMsg", messageSource.getMessage("vcpenetwork.create.message.error.fields", null, locale));
				model.addAttribute("action", new String("create"));
			}
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource.getMessage("vcpenetwork.create.message.error", null, locale) + ": " + e.getMessage());
			model.addAttribute("action", new String("create"));
		} finally {
			model.addAttribute("usersNOC", getUsersNOC());
			model.addAttribute("usersClient", getUsersClient());
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
			model.addAttribute("logicalInfrastructure", vcpeNetworkBO.getById(vcpeNetworkId));
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.edit.message.error", null, locale));
		} finally {
			model.addAttribute("usersNOC", getUsersNOC());
			model.addAttribute("usersClient", getUsersClient());
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
	protected String update(LogicalInfrastructure logicalInfrastructure, BindingResult result, Model model, Locale locale) {
		LOGGER.debug("update entity: " + logicalInfrastructure);
		try {
			if (!result.hasErrors()) {
				LOGGER.debug("removing the old environment");
				vcpeNetworkBO.delete(logicalInfrastructure.getId());

				LOGGER.debug("create the new environment");
				String vcpeNetworkId = vcpeNetworkBO.create(logicalInfrastructure);

				model.addAttribute("logicalInfrastructure", vcpeNetworkBO.getById(vcpeNetworkId));
				model.addAttribute("infoMsg", messageSource.getMessage("vcpenetwork.update.message.info", null, locale));
			} else {
				model.addAttribute("errorMsg", messageSource
						.getMessage("vcpenetwork.update.message.error.fields", null, locale));
			}
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.update.message.error", null, locale) + ": " + e.getMessage());
		} finally {
			model.addAttribute("usersNOC", getUsersNOC());
			model.addAttribute("usersClient", getUsersClient());
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
	protected String delete(String vcpeNetworkId, Model model, Locale locale, HttpSession session) {
		LOGGER.debug("delete entity with id: " + vcpeNetworkId);
		try {
			vcpeNetworkBO.delete(vcpeNetworkId);
			session.setAttribute("vcpeNetworkList", vcpeNetworkBO.getAllVCPENetworks());
			model.addAttribute("infoMsg", messageSource
					.getMessage("vcpenetwork.delete.message.info", null, locale));
		} catch (RestServiceException e) {
			model.addAttribute("errorMsg", messageSource
					.getMessage("vcpenetwork.delete.message.error", null, locale));
		}
		return "home";
	}

	/**
	 * @return all the users Client
	 */
	private Map<String, String> getUsersClient() {
		Map<String, String> clients = new LinkedHashMap<String, String>();
		clients.put("client1", "Client 1");
		clients.put("client2", "Client 2");
		return clients;
	}

	/**
	 * @return all the users NOC
	 */
	private Map<String, String> getUsersNOC() {
		Map<String, String> nocs = new LinkedHashMap<String, String>();
		nocs.put("noc1", "NOC 1");
		nocs.put("noc2", "NOC 2");
		nocs.put("noc3", "NOC 3");
		return nocs;
	}

}
