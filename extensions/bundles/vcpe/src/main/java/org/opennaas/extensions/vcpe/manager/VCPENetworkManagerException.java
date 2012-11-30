/**
 * 
 */
package org.opennaas.extensions.vcpe.manager;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * @author Jordi
 */
public class VCPENetworkManagerException extends WebApplicationException {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * 
	 */
	public VCPENetworkManagerException() {
	}

	/**
	 * @param message
	 */
	public VCPENetworkManagerException(String message) {
		super(Response.serverError().entity(message).build());
	}

	/**
	 * @param status
	 */
	public VCPENetworkManagerException(int status) {
		super(status);
	}

	/**
	 * @param response
	 */
	public VCPENetworkManagerException(Response response) {
		super(response);
	}

	/**
	 * @param cause
	 */
	public VCPENetworkManagerException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param cause
	 * @param status
	 */
	public VCPENetworkManagerException(Throwable cause, int status) {
		super(cause, status);
	}

	/**
	 * @param cause
	 * @param status
	 */
	public VCPENetworkManagerException(Throwable cause, Status status) {
		super(cause, status);
	}

	/**
	 * @param cause
	 * @param response
	 */
	public VCPENetworkManagerException(Throwable cause, Response response) {
		super(cause, response);
	}

}
