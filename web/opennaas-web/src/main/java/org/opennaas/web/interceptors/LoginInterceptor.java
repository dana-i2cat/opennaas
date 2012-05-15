package org.opennaas.web.interceptors;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.xwork.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.StrutsStatics;
import org.opennaas.web.entities.User;
import org.opennaas.web.utils.Constants;
import org.opennaas.web.utils.DigestUtils;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class LoginInterceptor extends AbstractInterceptor implements StrutsStatics {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -8390169950883096958L;
	private static final Logger	logger				= Logger.getLogger(LoginInterceptor.class);
	private ResourceBundle		resources			= ResourceBundle.getBundle("ApplicationResources");

	@Override
	public void init() {
		logger.info("Intializing LoginInterceptor");
	}

	@Override
	public void destroy() {
	}

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		final ActionContext context = invocation.getInvocationContext();
		HttpServletRequest request = (HttpServletRequest) context.get(HTTP_REQUEST);
		Map<String, Object> strutsSession = context.getSession();
		Object user = strutsSession.get(Constants.USER_LOGGED);
		if (user == null) {
			if (processLogin(invocation, request, strutsSession)) {
				return Action.SUCCESS;
			}
			return Action.LOGIN;
		}

		return invocation.invoke();
	}

	/**
	 * Attempt to process the user's login attempt
	 */
	public boolean processLogin(ActionInvocation invocation,
			HttpServletRequest request, Map<String, Object> strutsSession) {
		boolean res = false;
		Object action = invocation.getAction();
		ActionSupport as = (ActionSupport) action;

		if (StringUtils.isBlank(request.getParameter(Constants.USER_NAME)) &&
				StringUtils.isBlank(request.getParameter(Constants.PASSWORD))) {
			logger.error("You have to login first");
			as.addActionError("You have to login first");
		} else if (StringUtils.isBlank(request.getParameter(Constants.USER_NAME))) {
			logger.error("Login error, userName is empty");
			as.addActionError("Login error, userName is empty");
		} else if (StringUtils.isBlank(request.getParameter(Constants.PASSWORD))) {
			logger.error("Login error, password is empty");
			as.addActionError("Login error, password is empty");
		} else {
			try {
				String username1 = request.getParameter(Constants.USER_NAME);
				String password1 = request.getParameter(Constants.PASSWORD);

				String username2 = resources.getString(Constants.USER_NAME);
				String password2 = resources.getString(Constants.PASSWORD);

				if (StringUtils.equals(username1, username2)
						&& DigestUtils.compareDigestValues(password1, password2)) {
					logger.info("Login with user: " + username1);
					User user = new User();
					user.setUserName(username1);
					strutsSession.put(Constants.USER_LOGGED, user);
					res = true;
				} else {
					if (action instanceof ActionSupport) {
						logger.error("Incorrect values: login error with user: " + username1);
						as.addActionError("Incorrect values: login error with user: " + username1);
					}
				}
			} catch (Exception e) {
				if (action instanceof ActionSupport) {
					as = (ActionSupport) action;
					as.addActionError("login failed");
				}
				logger.error("login failed", e);
			}
		}
		return res;
	}

	public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String string1 = "@opennaas2012";
		String string2 = "f0bf4a4fe3c0807d9d181bb933b40a18d3cf2dec";

		System.out.println(new LoginInterceptor().compareDigestValues(string1, string2));
	}

	/**
	 * @param value1
	 *            , string
	 * @param value2
	 *            , digest
	 * @return true if digest of value1 is equals to value2
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 */
	private boolean compareDigestValues(String value1, String value2)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		byte[] aValue = getHash(value1.getBytes("UTF8"));
		System.out.println(convertByteToHex(aValue) + " = " + value2);
		return convertByteToHex(aValue).equals(value2);
	}

	/**
	 * @param content
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private byte[] getHash(byte[] content) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA1");
		return md.digest(content);
	}

	/**
	 * @param byteData
	 * @return
	 */
	private String convertByteToHex(byte[] byteData) {
		// convert the byte to hex format method 1
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

}