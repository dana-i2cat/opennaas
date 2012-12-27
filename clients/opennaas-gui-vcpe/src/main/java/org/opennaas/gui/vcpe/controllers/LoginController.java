/**
 * 
 */
package org.opennaas.gui.vcpe.controllers;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles and retrieves the login or denied page depending on the URI template
 */
@Controller
@RequestMapping("/auth")
public class LoginController {

	protected static Logger	logger	= Logger.getLogger(LoginController.class);

	/**
	 * Handles and retrieves the login JSP page
	 * 
	 * @return the name of the JSP page
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String getLoginPage(@RequestParam(value = "error", required = false) boolean error,
			Model model) {
		logger.debug("Received request to show login page");
		if (error == true) {
			model.addAttribute("error", "You have entered an invalid username or password!");
		}
		return "login";
	}

	/**
	 * Handles and retrieves the denied JSP page. <br>
	 * This is shown whenever a regular user tries to access an admin only page.
	 * 
	 * @return the name of the JSP page
	 */
	@RequestMapping(value = "/denied", method = RequestMethod.GET)
	public String getDeniedPage() {
		logger.debug("Received request to show denied page");
		return "denied";
	}

	/**
	 * Handles and retrieves the login JSP page
	 * 
	 * @return the name of the JSP page
	 */
	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String getSuccessPage(@RequestParam(value = "error", required = false) boolean error,
			ModelMap model) {
		logger.debug("Received request to show home page");
		return "home";
	}

}