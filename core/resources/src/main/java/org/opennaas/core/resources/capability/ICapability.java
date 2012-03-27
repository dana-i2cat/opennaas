package org.opennaas.core.resources.capability;

import org.opennaas.core.resources.ILifecycle;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;

/**
 * Basic interface all resource capabilities must implement
 *
 * @author Mathieu Lemay - ITI
 * @version 1.0
 */

public interface ICapability extends ILifecycle {

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
	 *
	 * @param resource
	 */
	public void setResource(IResource resource);

	/**
	 * Operation to send messages from a capability
	 */
	public Object sendMessage(String idOperation, Object paramsModel) throws CapabilityException;

}
