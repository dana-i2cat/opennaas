package org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions;

/**
 * An exception telling a flow allocation has been rejected.
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class FlowAllocationRejectedException extends FlowAllocationException {

	/**
	 * Auto-generated serial number.
	 */
	private static final long	serialVersionUID	= -769709389074540803L;

	public FlowAllocationRejectedException() {
		super();
	}

	public FlowAllocationRejectedException(String message, Throwable cause) {
		super(message, cause);
	}

	public FlowAllocationRejectedException(String message) {
		super(message);
	}

	public FlowAllocationRejectedException(Throwable cause) {
		super(cause);
	}

}
