package net.i2cat.nexus.resources.capability;

import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.Information;

/**
 * This class provides an abstract implementation for the ICapability
 * interface. This class must be extended by each module and must implement the
 * abstract lifecycle methods.
 * 
 * @author Mathieu Lemay (ITI)
 * @author Scott Campbell(CRC)
 * @author Eduard Grasa (i2CAT)
 * 
 */
public abstract class AbstractCapability implements ICapability{
	
	/** The descriptor for this capability **/
	protected CapabilityDescriptor descriptor;
	protected State state = null;
	protected String capabilityId = null;
	protected IResource resource = null;
	
	public AbstractCapability(CapabilityDescriptor descriptor) {
		this.descriptor = descriptor;
		this.capabilityId = descriptor.getCapabilityInformation().getType();
		setState(State.INSTANTIATED);
	}

	public CapabilityDescriptor getCapabilityDescriptor() {
		return descriptor;
	}

	public Information getCapabilityInformation() {
		return descriptor.getCapabilityInformation();
	}
	
	/**
	 * The resource where this capability belongs
	 * @param resource
	 */
	public void setResource(IResource resource){
		this.resource = resource;
	}

	/**
	 * Returns the current capability state
	 * 
	 * @return state enum object
	 */
	public State getState() {
		return state;
	}

	/**
	 * Sets the current capability state
	 */
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * Initializes this capability, the status will be INITIALIZED, then will be
	 * ACTIVE if enabled.
	 * 
	 * @throws ResourceException
	 */
	public void initialize() throws CapabilityException {
		initializeCapability();
		state = State.INITIALIZED;
	}

	/**
	 * Activates this capability and change state to ACTIVE.
	 * 
	 * @throws ResourceException
	 */
	public void activate() throws CapabilityException {
		activateCapability();
		state = State.ACTIVE;
	}

	/**
	 * Deactivate this capability and change state to INACTIVE
	 * 
	 * @throws ResourceException
	 */
	public void deactivate() throws CapabilityException {
		deactivateCapability();
		state = State.INACTIVE;
	}

	/**
	 * Prepares capability for Garbage Collection state will be SHUTDOWN until it
	 * is collected.
	 * 
	 * @throws ResourceException
	 */
	public void shutdown() throws CapabilityException {
		shutdownCapability();
		state = State.SHUTDOWN;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (getCapabilityInformation() != null) {
			builder.append("\nCapability Type: " + getCapabilityInformation().getType());
			builder.append("\nCapability Description: " + getCapabilityInformation().getDescription());
			builder.append("\nCapability Version: " + getCapabilityInformation().getName());
		}
		return builder.toString();
	}

	public void setCapabilityDescriptor(CapabilityDescriptor descriptor) {
		this.descriptor = descriptor;
	}
	
	protected abstract void initializeCapability() throws CapabilityException;

	protected abstract void activateCapability() throws CapabilityException;

	protected abstract void deactivateCapability() throws CapabilityException;

	protected abstract void shutdownCapability() throws CapabilityException;
}