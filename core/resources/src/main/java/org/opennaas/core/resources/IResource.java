package org.opennaas.core.resources;

import java.util.List;

import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.profile.IProfile;

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
	public void setCapabilities(List<? extends ICapability> capabilities);

	/**
	 * Get all the capabilities of this resource
	 * 
	 * @return the engine capabilities
	 */
	public List<? extends ICapability> getCapabilities();

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
	 * Get capability by type
	 * 
	 * @return the capability
	 * @throws ResourceException
	 *             if this resource does not have any capability of given type
	 */
	public ICapability getCapabilityByType(String type) throws ResourceException;

	/**
	 * Get capabilities of this resource that implement given interface
	 * 
	 * @param interfaze
	 * @return Capabilities of this resource with given interface
	 */
	public List<ICapability> getCapabilitiesByInterface(Class<? extends ICapability> interfaze);

	/**
	 * Get first capability in this resource capabilities implementing given interface
	 * 
	 * @param interfaze
	 * @return Capability of this resource with given interface
	 * @throws ResourceException
	 *             if this resource does not have any capability of given type
	 * 
	 */
	public ICapability getCapabilityByInterface(Class<? extends ICapability> interfaze) throws ResourceException;

	/**
	 * Start the Resource. The resource must already be instantiated and in the initialized state. This method will perform the necessary
	 * bootstrapping to transition from INITIALIZED to ACTIVE State
	 * 
	 * @throws CorruptStateException
	 */
	public void start() throws ResourceException, CorruptStateException;

	/**
	 * Stop the resource. This method will transition to the SHUTDOWN State
	 * 
	 * @throws CorruptStateException
	 */
	public void stop() throws ResourceException, CorruptStateException;

	public void setModel(IModel model);

	public IModel getModel();

	public IProfile getProfile();

	public void setProfile(IProfile profile);

	public IResourceBootstrapper getBootstrapper();

	public void setBootstrapper(IResourceBootstrapper bootstrapper);

}
