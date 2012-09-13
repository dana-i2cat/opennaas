package org.opennaas.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/home")
public class HomeController {

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView redirect() {
		return new ModelAndView("home");
	}
}
