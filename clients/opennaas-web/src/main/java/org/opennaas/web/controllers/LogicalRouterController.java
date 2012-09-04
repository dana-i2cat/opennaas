package org.opennaas.web.controllers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.Valid;

import org.opennaas.web.bos.LogicalRouterBO;
import org.opennaas.web.entities.LogicalRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Jordi
 */
@Controller
@RequestMapping(value = "/logicalRouter")
public class LogicalRouterController {

	private Map<Long, LogicalRouter>	logicalRouters	= new ConcurrentHashMap<Long, LogicalRouter>();
	@Autowired
	private LogicalRouterBO				logicalRouterBO;

	/**
	 * @param model
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String getCreateForm(Model model) {
		model.addAttribute(new LogicalRouter());
		return "logicalRouter/createForm";
	}

	/**
	 * @param logicalRouter
	 * @param result
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid LogicalRouter logicalRouter, BindingResult result) {
		if (result.hasErrors()) {
			return "logicalRouter/createForm";
		}
		logicalRouterBO.createLogicalRouter();
		this.logicalRouters.put(logicalRouter.assignId(), logicalRouter);
		return "redirect:/logicalRouter/" + logicalRouter.getId();
	}

	/**
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public String getView(@PathVariable Long id, Model model) {
		LogicalRouter logicalRouter = this.logicalRouters.get(id);
		if (logicalRouter == null) {
			throw new LogicalRouterException(id);
		}
		model.addAttribute(logicalRouter);
		return "logicalRouter/view";
	}

}
