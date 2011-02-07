package net.i2cat.mantychore.core.resources.message;

import net.i2cat.mantychore.core.resources.capability.CapabilityException;

/**
 * A message to send when there is a problem that must be propagated to another capability or its client
 * @author Scott Campbell
 *
 */
public class CapabilityErrorMessage extends CapabilityMessage {

	private static final long serialVersionUID = -1875321361240751494L;

	private CapabilityException exception = null;

	/**
	 * @return the exception
	 */
	public CapabilityException getException() {
		return exception;
	}

	/**
	 * @param exception the exception to set
	 */
	public void setException(CapabilityException exception) {
		this.exception = exception;
	}
}
