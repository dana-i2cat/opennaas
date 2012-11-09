/**
 * 
 */
package org.opennaas.extensions.vcpe.manager;

/**
 * @author Jordi
 */
public class VCPENetworkManagerException extends Exception {

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
		super(message);
	}

	/**
	 * @param cause
	 */
	public VCPENetworkManagerException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public VCPENetworkManagerException(String message, Throwable cause) {
		super(message, cause);
	}

}
