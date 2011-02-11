package net.i2cat.nexus.resources.capability;

import java.util.Hashtable;
import java.util.Properties;

import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.Information;
import net.i2cat.nexus.resources.message.ICapabilityMessage;

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

	/** The current state of the capability **/
	protected State state;

	protected String capabilityId = null;
	protected String resourceId = null;

	/**
	 * Hashtable to store incoming requests with the correlation tag as the key
	 */
	protected Hashtable<String, ICapabilityMessage> incommingRequests = new Hashtable<String, ICapabilityMessage>();
	
	/**
	 * A tables to store outgoing requests with the correlation tag as the key. The type of Object in the value depends
	 * on the type of object that sent the request and expects to get a response.
	 */
	protected Hashtable<String, Object> outgoingRequests = new Hashtable<String, Object>();

	public AbstractCapability(CapabilityDescriptor descriptor, String resourceId) {
		this.descriptor = descriptor;
		this.resourceId = resourceId;
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

	public void sendMessage(ICapabilityMessage message, String moduleID, Object obj)
			throws CapabilityException {
		outgoingRequests.put(message.getMessageID(), obj);
		sendMessage(message, moduleID);
	}
	

	public void sendMessage(ICapabilityMessage payload, String capability) throws CapabilityException {
		Properties properties = new Properties();
		properties.setProperty("CAPABILITY", capability+"-"+resourceId);
		sendMessage(payload, properties);
	}

	public abstract void sendMessage(ICapabilityMessage payload, Properties properties) throws CapabilityException;

	public void sendResponse(ICapabilityMessage responseMessage, String correlation)
			throws CapabilityException {
		Properties properties = new Properties();
		ICapabilityMessage request = incommingRequests.remove(correlation);
		if (request != null) {
			if (request.getRequestor() != null) {
				properties.setProperty("CAPABILITY", request.getRequestor());
			}
			responseMessage.setMessageID(request.getMessageID());
		}

		sendMessage(responseMessage, properties);
	}
	
	protected abstract void initializeCapability() throws CapabilityException;

	protected abstract void activateCapability() throws CapabilityException;

	protected abstract void deactivateCapability() throws CapabilityException;

	protected abstract void shutdownCapability() throws CapabilityException;
}