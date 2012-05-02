package org.opennaas.core.resources.capability;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;

/**
 * Basic interface all resource capabilities must implement
 * 
 * @author Mathieu Lemay - ITI
 * @version 1.0
 */

public interface ICapability {

	/**
	 * Get the name of this capability. Name must be unique for capabilities (must not be two capabilities with same name)
	 * 
	 * @return
	 */
	public String getCapabilityName();

	/**
	 * Get the descriptor for this capability
	 * 
	 */
	public CapabilityDescriptor getCapabilityDescriptor();

	/**
	 * Set the descriptor for this capability
	 * 
	 * @param descriptor
	 */
	// TODO REMOVE FROM THIS INTERFACE
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
	// TODO REMOVE FROM THIS INTERFACE
	public void setResource(IResource resource);

}
