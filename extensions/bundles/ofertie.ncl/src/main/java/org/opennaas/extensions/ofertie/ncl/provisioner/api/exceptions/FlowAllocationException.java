package org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions;

/**
 * An exception there have been an error allocating a flow.
 * @author Isart Canyameres Gimenez (i2cat) 
 *
 */
public class FlowAllocationException extends Exception {

	/**
	 * Auto-generated serial number.
	 */
	private static final long serialVersionUID = -7959827367903826425L;

	public FlowAllocationException() {
		super();
	}

	public FlowAllocationException(String message, Throwable cause) {
		super(message, cause);
	}

	public FlowAllocationException(String message) {
		super(message);
	}

	public FlowAllocationException(Throwable cause) {
		super(cause);
	}
	
}
