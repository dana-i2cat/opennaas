package org.opennaas.gui.vcpe.controllers;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Jordi
 */
@Controller
@RequestMapping(value = "/secure/home")
public class HomeController {

	private static final Logger	LOGGER	= Logger.getLogger(HomeController.class);

	/**
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String redirect(HttpServletRequest request) {
		LOGGER.debug("redirect to home page");
		return "redirect:/secure/vcpeNetwork/home";
	}
}
