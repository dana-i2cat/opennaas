package org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions;

/**
 * An Exception telling there has been an error in Provisioner component.
 * @author Isart Canyameres Gimenez (i2cat) 
 *
 */
public class ProvisionerException extends Exception {

	/**
	 * Auto-generated serial number.
	 */
	private static final long serialVersionUID = 719860637983170670L;

	public ProvisionerException() {
		super();
	}

	public ProvisionerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProvisionerException(String message) {
		super(message);
	}

	public ProvisionerException(Throwable cause) {
		super(cause);
	}

}
