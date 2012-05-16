package org.opennaas.web.actions;

import org.apache.log4j.Logger;

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
	private static final Logger	log					= Logger.getLogger(UnhandledExceptionHandler.class);

	@Override()
	public String execute() {
		if (exception != null) {
			if (exception.getLocalizedMessage() != null) {
				String message = exception.getMessage().toString();
				log.error(message);
				addActionError(message);
			}
			if (exception.getCause() != null) {
				String cause = exception.getCause().toString();
				log.error(cause);
				addActionError(cause);
			}
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
