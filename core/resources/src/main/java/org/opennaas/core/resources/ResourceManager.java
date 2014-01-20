package org.opennaas.core.resources;

/*
 * #%L
 * OpenNaaS :: Core :: Resources
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.descriptor.network.NetworkTopology;

/**
 * This class is the entry point to the resource for resource clients.
 * 
 * @author Eduard Grasa
 * @author Roc Vallès <roc.valles@i2cat.net>
 * @author Adrian Rosello Rey (i2CAT)
 * @author Héctor Fernández
 * 
 */
public class ResourceManager implements IResourceManager {

	Log												logger	= LogFactory.getLog(ResourceManager.class);
	/** The map of engine repository services, stored by type */
	private final Map<String, IResourceRepository>	resourceRepositories;

	/**
	 * Default constructor
	 */
	public ResourceManager() {
		logger.debug("Resource Manager started, waiting for Resource repositories");
		resourceRepositories = new HashMap<String, IResourceRepository>();
	}

	/**
	 * Called by blueprint every time a resource repository is registered
	 * 
	 * @param serviceInstance
	 * @param serviceProperties
	 */
	public synchronized void resourceRepositoryAdded(IResourceRepository serviceInstance, Map<?, ?> serviceProperties) {
		if (serviceInstance != null && serviceProperties != null) {
			logger.debug("New resource repository added for resources of type: " + serviceProperties.get("type"));

			// Add it to the map
			resourceRepositories.put((String) serviceProperties.get("type"), serviceInstance);
		}
	}

	/**
	 * Called by blueprint every time a resource repository is unregistered
	 * 
	 * @param serviceInstance
	 * @param serviceProperties
	 */
	public synchronized void resourceRepositoryRemoved(IResourceRepository serviceInstance, Map<?, ?> serviceProperties) {
		if (serviceInstance != null && serviceProperties != null) {
			logger.debug("Existing resource repository removed :" + serviceInstance.toString() + " " + serviceProperties.get("type"));
			// Remove it from the map
			resourceRepositories.remove(serviceProperties.get("type"));
		}
	}

	/**
	 * Create a new resource with a given resourceDescriptor
	 * 
	 * @param resourceDescriptor
	 * @returns the new resource
	 * @throws ResourceException
	 */
	@Override
	public synchronized IResource createResource(ResourceDescriptor resourceDescriptor) throws ResourceException {
		IResourceRepository repo = getResourceRepository(resourceDescriptor.getInformation().getType());
		IResource resource = repo.createResource(resourceDescriptor);
		return resource;
	}

	/**
	 * List all the existing resources of a given type. If type is null, list all the resources of all types
	 * 
	 * @return The list of the resources contained on the given type repository. Is the type is not a valid type of repository it will return null
	 *         value.
	 */
	@Override
	public synchronized List<IResource> listResourcesByType(String type) {
		if (type == null) {
			// return all resources
			return this.listResources();
		} else {
			// return resources of a given type
			IResourceRepository repo = resourceRepositories.get(type);
			if (repo != null) {
				return new ArrayList<IResource>(repo.listResources());
			} else {
				return null;
			}
		}
	}

	/**
	 * List all the existing resources
	 * 
	 * @return
	 */
	@Override
	public synchronized List<IResource> listResources() {
		logger.info("Resource Manager: listing the resources");
		List<IResource> result = new ArrayList<IResource>();
		for (IResourceRepository repository : resourceRepositories.values()) {
			result.addAll(repository.listResources());
		}
		return result;
	}

	/**
	 * Modify the existing resource that matches the id
	 * 
	 * @param resourceDescriptor
	 * @return the modified resource
	 * @throws ResourceException
	 */
	@Override
	public synchronized IResource modifyResource(IResourceIdentifier resourceIdentifier, ResourceDescriptor resourceDescriptor)
			throws ResourceException {
		IResource previousResource = getResource(resourceIdentifier);
		IResource resource = null;
		// only can modify if the new type is the same as the previous resource
		if (resourceDescriptor.getInformation().getType().equals(previousResource.getResourceDescriptor().getInformation().getType())) {
			IResourceRepository repo = getResourceRepository(resourceDescriptor.getInformation().getType());
			resource = repo.modifyResource(resourceIdentifier.getId(), resourceDescriptor);
		} else {
			throw new ResourceException("Cannot modify the type of the resource.");
		}
		return resource;
	}

	/**
	 * Remove the existing resource that matches the id
	 * 
	 * @param resourceIdentifier
	 * @throws ResourceException
	 */
	@Override
	public synchronized void removeResource(IResourceIdentifier resourceIdentifier) throws ResourceException {
		IResourceRepository repo = getResourceRepository(resourceIdentifier.getType());
		repo.removeResource(resourceIdentifier.getId());
	}

	/**
	 * Start the existing resource that matches the id
	 * 
	 * @param resourceIdentifier
	 * @throws ResourceException
	 */
	@Override
	public synchronized void startResource(IResourceIdentifier resourceIdentifier) throws ResourceException {
		IResourceRepository repo = getResourceRepository(resourceIdentifier.getType());
		repo.startResource(resourceIdentifier.getId());
	}

	/**
	 * Stop the existing resource that matches the id
	 * 
	 * @param resourceIdentifier
	 * @throws ResourceException
	 */
	@Override
	public synchronized void stopResource(IResourceIdentifier resourceIdentifier) throws ResourceException {
		IResourceRepository repo = getResourceRepository(resourceIdentifier.getType());
		repo.stopResource(resourceIdentifier.getId());
	}

	/**
	 * Stop the existing resource that matches the id
	 * 
	 * @param resourceIdentifier
	 * @throws ResourceException
	 */
	@Override
	public synchronized void forceStopResource(IResourceIdentifier resourceIdentifier) throws ResourceException {
		IResourceRepository repo = getResourceRepository(resourceIdentifier.getType());
		logger.info("Getting resourceId: " + resourceIdentifier.getId());
		repo.forceStopResource(resourceIdentifier.getId());
	}

	@Override
	public synchronized void exportResourceDescriptor(IResourceIdentifier resourceIdentifier, String fileName) throws ResourceException {
		IResourceRepository repo = getResourceRepository(resourceIdentifier.getType());
		ResourceDescriptor resourceDescriptor = repo.getResource(resourceIdentifier.getId()).getResourceDescriptor();
		try {
			File file = new File(fileName);
			JAXBContext context = JAXBContext.newInstance(ResourceDescriptor.class);
			context.createMarshaller().marshal(resourceDescriptor, file);
			logger.info("ResourceDescriptor of resource " + resourceDescriptor.getId() + " exported to file " + fileName);
		} catch (Exception ex) {
			throw new ResourceException(ex.getMessage(), ex);
		}
	}

	public synchronized void exportNetworkTopology(IResourceIdentifier resourceIdentifier, String fileName) throws ResourceException {
		IResourceRepository repo = getResourceRepository(resourceIdentifier.getType());
		ResourceDescriptor resourceDescriptor = repo.getResource(resourceIdentifier.getId()).getResourceDescriptor();
		if (resourceDescriptor.getNetworkTopology() != null) {
			try {
				File file = new File(fileName);
				JAXBContext context = JAXBContext.newInstance(NetworkTopology.class);
				context.createMarshaller().marshal(resourceDescriptor.getNetworkTopology(), file);
				logger.info("NetworkTopology of resource " + resourceIdentifier.getId() + " exported to file " + fileName);
				resourceDescriptor.setNetworkTopology(null); // Reset networkTopology
			} catch (Exception ex) {
				throw new ResourceException(ex.getMessage(), ex);
			}
		} else {
			throw new ResourceException("The resource hasn't a network topology to export.");
		}
	}

	/**
	 * Get an existing resource
	 * 
	 * @param engineIdentifier
	 *            the id of the resource to get
	 * @return the resource
	 * @throws ResourceException
	 *             if resource is not found
	 */
	@Override
	public synchronized IResource getResource(IResourceIdentifier resourceIdentifier) throws ResourceException {
		IResourceRepository repo = getResourceRepository(resourceIdentifier.getType());
		return repo.getResource(resourceIdentifier.getId());
	}

	private synchronized IResourceRepository getResourceRepository(String resourceType) throws ResourceException {
		IResourceRepository repo = resourceRepositories.get(resourceType);
		if (repo == null) {
			throw new ResourceException("Didn't find an engine repository for engine type: " + resourceType);
		}
		return repo;
	}

	/**
	 * @return the available resource types
	 */
	@Override
	public synchronized GenericListWrapper<String> getResourceTypesAPI() {

		return new GenericListWrapper<String>(this.getResourceTypes());
	}

	/**
	 * @return the available resource types
	 */
	@Override
	public synchronized List<String> getResourceTypes() {
		return new ArrayList<String>(resourceRepositories.keySet());
	}

	/**
	 * Get the IdentifierInformation for a given name (Resource.Information.name) of resource
	 * 
	 * @return the ResourceIdentifier
	 */
	@Override
	public IResourceIdentifier getIdentifierFromResourceName(String resourceType, String resourceName) throws ResourceException {
		if (resourceType == null || resourceName == null) {
			throw new ResourceException("The identifiers can not be null");
		}
		IResourceRepository repo = getResourceRepository(resourceType);
		List<IResource> resources = repo.listResources();
		if (resources.isEmpty()) {
			throw new ResourceNotFoundException("Didn't find any resource in " + resourceType + " engine repository.");
		}
		IResourceIdentifier identifier = null;
		for (IResource resource : resources) {
			if ((resource.getResourceDescriptor().getInformation().getName()).equals(resourceName)) {
				identifier = resource.getResourceIdentifier();
			}
		}
		if (identifier == null) {
			throw new ResourceNotFoundException("Didn't find a resource with name: " + resourceName + " in the repository.");
		}
		return identifier;
	}

	/**
	 * Get the Name for a given resource ID (Resource.descriptor.id) of resource
	 * 
	 * @return the Name. If null didn't find a resource with this ID
	 */
	@Override
	public synchronized String getNameFromResourceID(String ID)
			throws ResourceException
	{
		for (IResourceRepository repository : resourceRepositories.values()) {
			for (IResource resource : repository.listResources()) {
				if ((resource.getResourceIdentifier().getId()).equals(ID)) {
					return resource.getResourceDescriptor().getInformation().getName();
				}
			}
		}

		return null;
	}

	@Override
	public synchronized Resource getResourceById(String resourceId) throws ResourceException {
		for (IResourceRepository repo : resourceRepositories.values()) {
			try {
				return (Resource) repo.getResource(resourceId);
			} catch (ResourceException e) {
				// ignore, try next repository
			}
		}
		throw new ResourceException("No resource with ID " + resourceId + " was found.");
	}

	@Override
	public ResourceDescriptor getResourceDescriptor(String resourceId) throws ResourceException {
		return getResourceById(resourceId).getResourceDescriptor();
	}

	@Override
	public void destroyAllResources() throws ResourceException {
		List<IResource> resources = listResources();

		for (IResource resource : resources)
		{
			if (resource.getState() == State.ACTIVE)
				stopResource(resource.getResourceIdentifier());
		}

		for (IResource resource : resources)
		{
			removeResource(resource.getResourceIdentifier());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.IResourceManager#modifyResource(java.lang.String, org.opennaas.core.resources.descriptor.ResourceDescriptor)
	 */
	@Override
	public String modifyResource(String resourceId, ResourceDescriptor resourceDescriptor) throws ResourceException {
		return modifyResource(getResourceById(resourceId).getResourceIdentifier(), resourceDescriptor).getResourceIdentifier().getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.IResourceManager#removeResource(java.lang.String)
	 */
	@Override
	public void removeResource(String resourceId) throws ResourceException {
		removeResource(getResourceById(resourceId).getResourceIdentifier());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.IResourceManager#startResource(java.lang.String)
	 */
	@Override
	public void startResource(String resourceId) throws ResourceException {
		startResource(getResourceById(resourceId).getResourceIdentifier());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.IResourceManager#stopResource(java.lang.String)
	 */
	@Override
	public void stopResource(String resourceId) throws ResourceException {
		stopResource(getResourceById(resourceId).getResourceIdentifier());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.IResourceManager#createResourceWS(org.opennaas.core.resources.descriptor.ResourceDescriptor)
	 */
	@Override
	public String createResourceWS(ResourceDescriptor resourceDescriptor) throws ResourceException {
		return createResource(resourceDescriptor).getResourceIdentifier().getId();
	}

	@Override
	public GenericListWrapper<String> listResourcesNameByType(String type) throws ResourceException {
		if (StringUtils.isEmpty(type))
			throw new ResourceException("You didn't specify any resource type.");

		IResourceRepository repo = resourceRepositories.get(type);

		if (repo == null)
			throw new ResourceException("Didn't find any resource repository of type " + type);

		List<IResource> resources = repo.listResources();
		List<String> resourcesNames = new ArrayList<String>();
		for (IResource resource : resources)
			resourcesNames.add(resource.getResourceDescriptor().getInformation().getName());

		return new GenericListWrapper<String>(resourcesNames);
	}

	@Override
	public String getIdentifierFromResourceTypeName(String resourceType, String resourceName) throws ResourceException {
		return this.getIdentifierFromResourceName(resourceType, resourceName).getId();
	}

	@Override
	public String getStatus(String resourceId) throws ResourceException {
		return this.getResourceById(resourceId).getState().name();

	}

}