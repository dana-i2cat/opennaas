package org.opennaas.core.resources.capability;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;

/**
 * This interface must be implemented by the classes that create capabilities of a given resource. ICapabilityFactories act as helper classes of
 * IResourceRepositories, and initialize the resource capabilities by getting them from the right location (OSGi registry, explicitly calling new,
 * method injection, ...)
 * 
 * @author Mathieu Lemay (ITI)
 * 
 */
public interface ICapabilityFactory {
	/**
	 * Use the information in the engine descriptor to create an instance of a module. Once it is created, add it to the engine context
	 * 
	 * @param moduleDescriptor
	 *            the object that contains the engine configuration, model and modules
	 * @return a newly instantiated engine module
	 * @throws ResourceException
	 */
	public ICapability create(CapabilityDescriptor moduleDescriptor, String resourceId) throws CapabilityException;

	public ICapability create(IResource resource) throws CapabilityException;

	public String getType();

	public void setType(String type);

}
