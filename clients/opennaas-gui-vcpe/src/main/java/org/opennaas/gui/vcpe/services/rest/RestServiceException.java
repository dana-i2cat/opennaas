/**
 * 
 */
package org.opennaas.gui.vcpe.services.rest;


/**
 * @author Jordi
 */
public class RestServiceException extends Exception {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * 
	 */
	public RestServiceException() {
	}

	/**
	 * @param message
	 */
	public RestServiceException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public RestServiceException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RestServiceException(String message, Throwable cause) {
		super(message, cause);
	}

}
