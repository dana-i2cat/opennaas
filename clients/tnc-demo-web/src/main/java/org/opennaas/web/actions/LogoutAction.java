package org.opennaas.web.actions;

import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;
import org.opennaas.web.entities.User;
import org.opennaas.web.utils.Constants;

import com.opensymphony.xwork2.ActionSupport;

public class LogoutAction extends ActionSupport implements SessionAware {

	private static final long	serialVersionUID	= 6913527834543418999L;
	private static final Logger	LOGGER				= Logger.getLogger(LogoutAction.class);
	private Map<String, Object>	session;

	@Override
	public String execute() throws Exception {
		User user = (User) getSession().get(Constants.USER_LOGGED);
		LOGGER.info("Logout user: " + user.getUserName());
		getSession().remove(Constants.USER_LOGGED);
		return SUCCESS;
	}

	/**
	 * @param session
	 *            the session to set
	 */
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	/**
	 * @return the session
	 */
	public Map<String, Object> getSession() {
		return session;
	}

}