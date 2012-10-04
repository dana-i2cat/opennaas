package org.opennaas.web.controllers;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/home")
public class HomeController {

	private static final Logger	LOGGER	= Logger.getLogger(HomeController.class);

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView redirect() {
		LOGGER.debug("redirect to home page");
		return new ModelAndView("home");
	}
}
