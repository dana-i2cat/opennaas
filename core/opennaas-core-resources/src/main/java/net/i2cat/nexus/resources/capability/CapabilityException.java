package net.i2cat.nexus.resources.capability;

import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;

/**
 * @author Scott Campbell (CRC)
 * 
 */
public class CapabilityException extends ResourceException {
	private static final long		serialVersionUID	= 1522408034870437847L;

	private CapabilityDescriptor	capabilityDescriptor;

	public CapabilityException(Exception e) {
		super(e);
	}

	public CapabilityException() {
		super();
	}

	public CapabilityException(String msg) {
		super(msg);
	}

	public CapabilityException(String msg, Exception e) {
		super(msg, e);
	}

	public CapabilityException(String message, CapabilityDescriptor descriptor) {
		super(message);
		this.setCapabilityDescriptor(descriptor);
	}

	/**
	 * @param capabilityDescriptor
	 *            the capabilityDescriptor to set
	 */
	public void setCapabilityDescriptor(
			CapabilityDescriptor capabilityDescriptor) {
		this.capabilityDescriptor = capabilityDescriptor;
	}

	/**
	 * @return the capabilityDescriptor
	 */
	public CapabilityDescriptor getCapabilityDescriptor() {
		return capabilityDescriptor;
	}
}