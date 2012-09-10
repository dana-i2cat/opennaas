package org.opennaas.web.controllers;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.opennaas.web.bos.LogicalRouterBO;
import org.opennaas.web.entities.LogicalRouter;
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
@RequestMapping(value = "/logicalRouter")
public class LogicalRouterController {

	@Autowired
	private LogicalRouterBO							logicalRouterBO;
	@Autowired
	private ReloadableResourceBundleMessageSource	messageSource;

	/**
	 * Redirect to the form to create a LogicalRouter
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String getCreateForm(Model model) {
		model.addAttribute(new LogicalRouter());
		return "logicalRouter/createLogicalRouter";
	}

	/**
	 * Create a LogicalRouter
	 * 
	 * @param logicalRouter
	 * @param result
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid LogicalRouter logicalRouter, BindingResult result, Model model, Locale locale) {
		if (!result.hasErrors()) {
			logicalRouterBO.createLogicalRouter();
			model.addAttribute("infoMsg", messageSource
					.getMessage("logicalrouter.create.message.info", null, locale));
		} else {
			model.addAttribute("errorMsg", messageSource
					.getMessage("logicalrouter.create.message.error", null, locale));
		}
		return "logicalRouter/createLogicalRouter";
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
