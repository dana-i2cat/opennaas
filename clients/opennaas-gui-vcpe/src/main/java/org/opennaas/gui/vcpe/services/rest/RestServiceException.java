/**
 * 
 */
package org.opennaas.gui.vcpe.services.rest;

import com.sun.jersey.api.client.ClientResponse;

/**
 * @author Jordi
 */
public class RestServiceException extends Exception {

	private ClientResponse		response;

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

	/**
	 * @param message
	 * @param cause
	 */
	public RestServiceException(ClientResponse response) {
		this.response = response;
	}

	/**
	 * @return the response
	 */
	public ClientResponse getResponse() {
		return response;
	}

	/**
	 * @param response
	 *            the response to set
	 */
	public void setResponse(ClientResponse response) {
		this.response = response;
	}

}
