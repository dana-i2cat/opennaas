package org.opennaas.extensions.ws.services;

import java.util.List;

import javax.jws.WebService;

import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ResourceIdentifier;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;

/**
 * @author Isart Canyameres
 */
@WebService(portName = "ResourceManagerPort", serviceName = "ResourceManagerService", targetNamespace = "http:/www.opennaas.org/ws")
public interface IResourceManagerService {

	/**
	 * Create a new resource with a given resourceDescriptor
	 * 
	 * @param resourceDescriptor
	 * @returns the new resource
	 * @throws ResourceException
	 */
	public ResourceIdentifier createResource(ResourceDescriptor resourceDescriptor) throws ResourceException;

	/**
	 * Modify the existing resource that matches the id (inside resourceDescriptor)
	 * 
	 * @param resourceIdentifier
	 * @param resourceDescriptor
	 * @return the modified resource
	 * @throws ResourceException
	 *             if failed to modify
	 */
	public ResourceIdentifier modifyResource(ResourceIdentifier resourceIdentifier, ResourceDescriptor resourceDescriptor) throws ResourceException;

	/**
	 * Remove the existing resource that matches the id
	 * 
	 * @param resourceIdentifier
	 * @throws ResourceException
	 */
	public void removeResource(ResourceIdentifier resourceIdentifier) throws ResourceException;

	/**
	 * List all the existing resources of a given type. If type is null, list all resources whatever its type is.
	 * 
	 * @return The list of the resources contained on the given type repository. Is the type is not a valid type of repository it will return null
	 *         value.
	 */
	public List<ResourceIdentifier> listResourcesByType(String type);

	/**
	 * List all resources in container.
	 * 
	 * @return
	 */
	public List<ResourceIdentifier> listResources();

	/**
	 * Returns a list of available resource types.
	 */
	public List<String> getResourceTypes();

	// TODO IResource cannot be used with JAXB. Interfaces not supported.
	// /**
	// * Get an existing resource
	// *
	// * @param resourceIdentifier
	// * the id of the resource to get
	// * @return the resource
	// * @throws ResourceException
	// * if resource is not found
	// */
	// public IResource getResource(IResourceIdentifier resourceIdentifier) throws ResourceException;
	//
	// /**
	// * Get an existing resource
	// *
	// * @param resourceId
	// * resource's resourceId
	// * @return the resource
	// * @throws ResourceException
	// */
	// public IResource getResourceById(String resourceId) throws ResourceException;

	// TODO remove when getResource is available
	/**
	 * Returns the ResourceDescriptor of resource identified by given resourceIdentifier
	 * 
	 * @param resourceIdentifier
	 * @return
	 * @throws ResourceException
	 *             if no resource is found with given identifier.
	 */
	public ResourceDescriptor getResourceDescriptor(ResourceIdentifier resourceIdentifier) throws ResourceException;

	/**
	 * Start an existing resource
	 * 
	 * @param resourceIdentifier
	 * @throws ResourceException
	 */
	public void startResource(ResourceIdentifier resourceIdentifier) throws ResourceException;

	/**
	 * Stop an existing resource
	 * 
	 * @param resourceIdentifier
	 * @throws ResourceException
	 */
	public void stopResource(ResourceIdentifier resourceIdentifier) throws ResourceException;

	/**
	 * Export a resource descriptor to an XML file
	 * 
	 * @param resourceIdentifier
	 * @param fileName
	 * @throws ResourceException
	 */
	public void exportResourceDescriptor(ResourceIdentifier resourceIdentifier, String fileName) throws ResourceException;

	/**
	 * 
	 * @param resourceType
	 * @param resourceName
	 * @return
	 * @throws ResourceException
	 */
	public ResourceIdentifier getIdentifierFromResourceName(String resourceType, String resourceName) throws ResourceException;

	/**
	 * 
	 * @param ID
	 * @return
	 * @throws ResourceException
	 */
	public String getNameFromResourceID(String ID) throws ResourceException;

	/**
	 * 
	 * @param resourceIdentifier
	 * @throws ResourceException
	 */
	public void forceStopResource(ResourceIdentifier resourceIdentifier) throws ResourceException;

}
