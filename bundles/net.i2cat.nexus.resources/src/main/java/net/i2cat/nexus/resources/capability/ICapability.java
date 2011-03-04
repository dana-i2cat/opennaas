package net.i2cat.nexus.resources.capability;

import net.i2cat.nexus.resources.ILifecycle;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.Information;

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
}
