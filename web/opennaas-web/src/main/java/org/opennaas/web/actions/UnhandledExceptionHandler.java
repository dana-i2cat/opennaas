package org.opennaas.web.actions;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Jordi
 */
public class UnhandledExceptionHandler extends ActionSupport {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private Exception			exception;

	@Override()
	public String execute() {
		if (exception != null) {
			addActionError(getText(exception.getMessage()));
			addActionError(getText(exception.getCause().toString()));
		}
		return ERROR;
	}

	/**
	 * @return the exception
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * @param exception
	 *            the exception to set
	 */
	public void setException(Exception exception) {
		this.exception = exception;
	}

}
