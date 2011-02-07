package net.i2cat.mantychore.core.resources.capability;

import java.util.Properties;

import net.i2cat.mantychore.core.resources.ILifecycle;
import net.i2cat.mantychore.core.resources.IResource;
import net.i2cat.mantychore.core.resources.descriptor.CapabilityDescriptor;
import net.i2cat.mantychore.core.resources.descriptor.Information;
import net.i2cat.mantychore.core.resources.message.ICapabilityMessage;

/**
 * Basic interface all resource capabilities must implement
 * 
 * @author Mathieu Lemay - ITI
 * @version 1.0
 */

public interface ICapability extends ILifecycle{

	/**
	 * Get the descriptor for this capability
	 * 
	 * @param name
	 */
	public CapabilityDescriptor getCapabilityDescriptor();

	/**
	 * Set the descriptor for this capability
	 * 
	 * @param descriptor
	 */
	public void setCapabilityDescriptor(CapabilityDescriptor descriptor);

	/**
	 * Get the information from this capability
	 * 
	 * @return
	 */
	public Information getCapabilityInformation();
	
	/**
	 * The resource where this capability belongs
	 * @param resource
	 */
	public void setResource(IResource resource);

	/**
	 * Send a message to another capability in the same resource
	 * 
	 * @param message
	 * @param capabilityId
	 * @throws CapabilityException
	 */
	public void sendMessage(ICapabilityMessage message, String capabilityId) throws CapabilityException;

	/**
	 * Send a message to another capability in the same resource. The properties are
	 * name/value pairs that give more specific details about the destination
	 * module. How the properties are used depends on the method of sending
	 * messages between modules.
	 * 
	 * @param message
	 * @param properties
	 * @throws CapabilityException
	 */
	public void sendMessage(ICapabilityMessage message, Properties properties) throws CapabilityException;
	
	/**
	 * Before sending a message to another capability, store the object in the
	 * table of active outgoing messages so the response message can be properly
	 * correlated with the request when it comes in. 
	 * 
	 * @param message
	 * @param capability
	 * @param command
	 * @throws CapabilityException
	 */
	public void sendMessage(ICapabilityMessage message, String capabilityId, Object obj) throws CapabilityException;

	/**
	 * Send a response message back to a capability that sent a request message. The correlation string is used
	 * to get the original message from the table of incoming requests so it can address and correlate the 
	 * response message correctly
	 * @param responseMessage
	 * @param correlation
	 * @throws CapabilityException
	 */
	public void sendResponse(ICapabilityMessage responseMessage, String correlation) throws CapabilityException;
}
