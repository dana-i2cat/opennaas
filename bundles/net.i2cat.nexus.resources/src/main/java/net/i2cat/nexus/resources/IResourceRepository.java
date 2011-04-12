package net.i2cat.nexus.resources;

import java.util.List;
import java.util.Map;

import net.i2cat.nexus.resources.capability.ICapabilityFactory;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;

/**
 * Implementations of this interface are responsible for managing instances of a specific resource type.
 * Each resource type has an IResourceRepository and is wired as a singleton and registered with the OSGI Registry
 * @author Eduard Grasa
 *
 */
public interface IResourceRepository {
	
	/**
	 * Creates and initializes a new instance of a resource. If there are any problems during the resource 
	 * initialization phase, it will throw an exception explaining why
	 * @param resourceDescriptor the resource configuration information
	 * @return the new resource instance
	 * @throws ResourceException
	 */
	public IResource createResource(ResourceDescriptor resourceDescriptor) throws ResourceException;
	
	/**
	 * Stops and destroys a running resource. If no resource is found with the given id, an exception is thrown
	 * @param identifier the identifier  of the resource to be removed
	 * @throws ResourceException
	 */
	public void removeResource(String identifier) throws ResourceException;
	
	/**
	 * Start a resource. If no resource is found with the given id, an exception is thrown
	 * @param identifier the identifier  of the resource to be started
	 * @throws ResourceException
	 */
	public void startResource(String identifier) throws ResourceException;
	
	/**
	 * Stops a running resource. If no resource is found with the given id, an exception is thrown
	 * @param identifier the identifier  of the resource to be stopped
	 * @throws ResourceException
	 */
	public void stopResource(String identifier) throws ResourceException;
	
	/**
	 * Saves a modified resource and reinitializes it. If there are any problems during the resource 
	 * initialization phase, it will throw an exception explaining why
	 * @param resourceDescriptor the new resource configuration
	 * @return the modified resource instance
	 * @throws ResourceException
	 */
	public IResource modifyResource(ResourceDescriptor resourceDescriptor) throws ResourceException;
	
	/**
	 * List all the running resources in this repository. If there are no resource it will return an empty list
	 * @return
	 */
	public List<IResource> listResources();
	
	/**
	 * Get the resource that matches the given id. If no resource is found, an exception is thrown
	 * @param id the id of the resource instance
	 * @return the resource instance
	 * @throws ResourceException
	 */
	public IResource getResource(String id) throws ResourceException;
	
	/**
	 * Get the list of ICapabilityFactories
	 * @return the list of ICapabilityFactories
	 */
	public Map<String,ICapabilityFactory> getCapabilityFactories();
	
	public String getResourceType();
}