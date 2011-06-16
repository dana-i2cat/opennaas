package net.i2cat.nexus.resources;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;

import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is the entry point to the resource for resource clients.
 * 
 * @author Eduard Grasa
 * 
 */
public class ResourceManager implements IResourceManager {

	Log											logger					= LogFactory.getLog(ResourceManager.class);
	/** The map of engine repository services, stored by type */
	private Map<String, IResourceRepository>	resourceRepositories	= null;

	/**
	 * Default constructor
	 */
	public ResourceManager() {
		logger.debug("Resource Manager started, waiting for Resource repositories");
		resourceRepositories = new Hashtable<String, IResourceRepository>();
	}

	/**
	 * Called by blueprint every time a resource repository is registered
	 * 
	 * @param serviceInstance
	 * @param serviceProperties
	 */
	public void resourceRepositoryAdded(IResourceRepository serviceInstance, Map serviceProperties) {
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
	public void resourceRepositoryRemoved(IResourceRepository serviceInstance, Map serviceProperties) {
		if (serviceInstance != null && serviceProperties != null) {
			logger.debug("Existing resource repository removed :" + serviceInstance.toString() + " " + serviceProperties.get("type"));
			// Remove it from the map
			resourceRepositories.remove((String) serviceProperties.get("type"));
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
	 * 
	 */
	@Override
	public synchronized List<IResource> listResourcesByType(String type) {
		List<IResource> result = new ArrayList<IResource>();

		if (type == null) {
			// return all resources
			return this.listResources();
		} else {
			// return resources of a given type
			IResourceRepository repo = resourceRepositories.get(type);
			if (repo != null) {
				result.addAll(repo.listResources());
			} else {
				return null;
			}
		}

		return result;
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
		Iterator<String> iterator = resourceRepositories.keySet().iterator();
		while (iterator.hasNext()) {
			result.addAll(resourceRepositories.get(iterator.next()).listResources());
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

	/**
	 * Get an existing resource
	 * 
	 * @param engineIdentifier
	 *            the id of the resource to get
	 * @return the resource
	 * @throws ResourceException
	 */
	@Override
	public synchronized IResource getResource(IResourceIdentifier resourceIdentifier) throws ResourceException {
		IResourceRepository repo = getResourceRepository(resourceIdentifier.getType());
		return repo.getResource(resourceIdentifier.getId());
	}

	private IResourceRepository getResourceRepository(String resourceType) throws ResourceException {
		IResourceRepository repo = resourceRepositories.get(resourceType);
		if (repo == null) {
			throw new ResourceException("Didn't find an engine repository for engine type: " + resourceType);
		}
		return repo;
	}

	/**
	 * @return the resourceRepositories
	 */
	public Map<String, IResourceRepository> getResourceRepositories() {
		return resourceRepositories;
	}

	/**
	 * Get the IdentifierInformation for a given name (Resource.Information.name) of resource
	 * 
	 * @return the ResourceIdentifier
	 */
	@Override
	public IResourceIdentifier getIdentifierFromResourceName(String resourceType, String resourceName) throws ResourceException {
		IResourceIdentifier identifier = null;
		IResourceRepository repo = null;

		if (resourceType == null || resourceName == null) {
			throw new ResourceException("The identifiers can not be null");
		}
		repo = resourceRepositories.get(resourceType);
		if (repo == null) {
			throw new ResourceException("Didn't find any repository of the type: " + resourceType);
		}
		List<IResource> resources = repo.listResources();
		if (resources.isEmpty()) {
			throw new ResourceException("Didn't find any resource in this engine repository: " + resourceType);
		}
		for (IResource resource : resources) {
			if ((resource.getResourceDescriptor().getInformation().getName()).equals(resourceName)) {
				identifier = resource.getResourceIdentifier();
			}
		}
		if (identifier == null) {
			throw new ResourceException("Didn't find a resource with name: " + resourceName + " in the repository.");
		}
		return identifier;
	}

	/**
	 * Get the Name for a given resource ID (Resource.descriptor.id) of resource
	 * 
	 * @return the Name. If null didn't find a resource with this ID
	 */
	public String getNameFromResourceID(String ID) throws ResourceException {
		String name = null;
		IResourceRepository repo = null;

		for (String repoId : resourceRepositories.keySet()) {
			repo = resourceRepositories.get(repoId);
			List<IResource> resources = repo.listResources();
			for (IResource resource : resources) {
				if ((resource.getResourceIdentifier().getId()).equals(ID)) {
					name = resource.getResourceDescriptor().getInformation().getName();
					return name;
				}
			}
		}

		return name;
	}
}