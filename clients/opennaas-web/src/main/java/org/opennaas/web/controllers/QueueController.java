package org.opennaas.web.controllers;

import org.opennaas.web.bos.QueueBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Jordi
 */
@Controller
@RequestMapping(value = "/queue")
public class QueueController {

	@Autowired
	private QueueBO	queueBO;

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
	@RequestMapping(method = RequestMethod.POST)
	public String executeQueue(Model model) {
		queueBO.execute();
		model.addAttribute("actions", queueBO.getActions());
		model.addAttribute("infoMsg", "Queue executed");
		return "queue/listActions";
	}
}
