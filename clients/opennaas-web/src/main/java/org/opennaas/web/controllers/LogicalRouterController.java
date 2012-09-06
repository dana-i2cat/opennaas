package org.opennaas.web.controllers;

import javax.validation.Valid;

import org.opennaas.web.bos.LogicalRouterBO;
import org.opennaas.web.entities.LogicalRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Jordi
 */
@Controller
@RequestMapping(value = "/logicalRouter")
public class LogicalRouterController {

	@Autowired
	private LogicalRouterBO	logicalRouterBO;

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
	 * @param logicalRouter
	 * @param result
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid LogicalRouter logicalRouter, BindingResult result) {
		logicalRouterBO.createLogicalRouter();
		return "logicalRouter/createLogicalRouter";
	}

}
