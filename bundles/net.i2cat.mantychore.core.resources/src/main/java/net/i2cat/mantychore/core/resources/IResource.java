package net.i2cat.mantychore.core.resources;

import java.util.List;

import net.i2cat.mantychore.core.resources.capability.ICapability;
import net.i2cat.mantychore.core.resources.descriptor.ResourceDescriptor;
import net.i2cat.mantychore.core.resources.descriptor.Information;

/**
 * The top level resource interface
 * 
 * @author Mathieu Lemay (c)2009 Inocybe Technologies inc.
 * 
 */
public interface IResource extends ILifecycle {
	
	public IResourceIdentifier getResourceIdentifier();

	public void setResourceIdentifier(IResourceIdentifier identifier);

	/**
	 * Get the resource instance descriptor
	 * 
	 * @return
	 */
	public ResourceDescriptor getResourceDescriptor();

	/**
	 * Set the resource descriptor
	 * 
	 * @param resourceDescriptor
	 */
	public void setResourceDescriptor(ResourceDescriptor resourceDescriptor);

	/**
	 * Set all the capabilities of this resource
	 * 
	 * @param capabilities
	 *            the resource capabilities
	 */
	public void setCapabilities(List<ICapability> capabilities);

	/**
	 * Get all the capabilities of this resource
	 * 
	 * @return the engine capabilities
	 */
	public List<ICapability> getCapabilities();

	/**
	 * Add a capability to this resource
	 * 
	 * @param capability
	 *            the actual capability
	 */
	public void addCapability(ICapability capability);

	/**
	 * Remove a capability from this resource
	 * 
	 * @param information
	 *            the information of the capability
	 * @return the removed capability (null if the capability does not exist)
	 */
	public ICapability removeCapability(Information information);

	/**
	 * Get a particular capability form this resource
	 * 
	 * @return the capability
	 */
	public ICapability getCapability(Information information);

	/**
	 * Start the Resource. The resource must already be instantiated and in the
	 * initialized state. This method will perform the necessary bootstrapping to
	 * transistion from INITIALIZED to ACTIVE State
	 */
	public void start() throws ResourceException;

	/**
	 * Stop the resource. This method will transition to the SHUTDOWN State
	 */
	public void stop() throws ResourceException;
}
