package org.opennaas.web.controllers;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.opennaas.web.bos.QueueBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sun.jersey.api.client.ClientHandlerException;

/**
 * @author Jordi
 */
@Controller
@RequestMapping(value = "/queue")
public class QueueController {

	@Autowired
	private QueueBO									queueBO;
	@Autowired
	private ReloadableResourceBundleMessageSource	messageSource;

	/**
	 * Get the actions of the queue
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String getActions(Model model) {
		model.addAttribute("actions", queueBO.getActions());
		return "queue/listActions";
	}

	/**
	 * Execute the queue
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/execute", method = RequestMethod.GET)
	public String execute(Model model, Locale locale) {
		queueBO.execute();
		model.addAttribute("actions", queueBO.getActions());
		model.addAttribute("infoMsg", messageSource
				.getMessage("queue.execute.message.info", null, locale));
		return "queue/listActions";
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
