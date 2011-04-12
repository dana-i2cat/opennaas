package net.i2cat.nexus.resources;

import java.io.File;
import java.util.*;

import javax.xml.bind.JAXBContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;

/**
 * This class is the entry point to the resource for resource clients.
 * @author Eduard Grasa
 *
 */
public class ResourceManager implements IResourceManager{
	
	Logger logger = LoggerFactory.getLogger(ResourceManager.class);
    /**The map of engine repository services, stored by type*/
	private Map<String, IResourceRepository> resourceRepositories = null;
	
	/**
	 * Default constructor
	 */
	public ResourceManager(){
		logger.debug("Resource Manager started, waiting for Resource repositories");
		resourceRepositories = new Hashtable<String, IResourceRepository>();
	}
	
	/**
	 * Called by blueprint every time a resource repository is registered
	 * @param serviceInstance
	 * @param serviceProperties
	 */
	public void resourceRepositoryAdded(IResourceRepository serviceInstance, Map serviceProperties){
		if (serviceInstance != null && serviceProperties != null){
			logger.debug("New resource repository added for resources of type: "+serviceProperties.get("type"));
			//Add it to the map
			resourceRepositories.put((String)serviceProperties.get("type"), serviceInstance);
		}
	}
	
	/**
	 * Called by blueprint every time a resource repository is unregistered
	 * @param serviceInstance
	 * @param serviceProperties
	 */
	public void resourceRepositoryRemoved(IResourceRepository serviceInstance, Map serviceProperties){
		if (serviceInstance != null && serviceProperties != null){
			logger.debug("Existing resource repository removed :"+serviceInstance.toString()+" "+serviceProperties.get("type"));
			//Remove it from the map
			resourceRepositories.remove((String)serviceProperties.get("type"));
		}
	}

	/**
	 * Create a new resource with a given resourceDescriptor
	 * @param resourceDescriptor
	 * @returns the new resource
	 * @throws ResourceException
	 */
	public synchronized IResource createResource(ResourceDescriptor resourceDescriptor) throws ResourceException {
		IResourceRepository repo = getResourceRepository(resourceDescriptor.getInformation().getType());
		IResource resource = repo.createResource(resourceDescriptor);
		return resource;
	}

	/**
	 * List all the existing resources of a given type. If type is null, list all the resources of all types
	 * @return
	 */
	public synchronized List<IResource> listResourcesByType(String type) {
		List<IResource> result = new ArrayList<IResource>();
		
		if (type == null){
			//return all resources
			return this.listResources();
		}else{
			//return resources of a given type
			IResourceRepository repo = resourceRepositories.get(type);
			if (repo != null){
				result.addAll(repo.listResources());
			}
		}
		
		return result;
	}
	
	/**
	 * List all the existing resources
	 * @return
	 */
	public synchronized List<IResource> listResources() {
		List<IResource> result = new ArrayList<IResource>();
		Iterator<String> iterator = resourceRepositories.keySet().iterator();
		while(iterator.hasNext()){
			result.addAll(resourceRepositories.get(iterator.next()).listResources());
		}		
		return result;
	}
		
	/**
	 * Modify the existing resource that matches the id
	 * @param resourceDescriptor
	 * @return the modified resource
	 * @throws ResourceException
	 */
	public synchronized IResource modifyResource(ResourceDescriptor resourceDescriptor) throws ResourceException {
		IResourceRepository repo = getResourceRepository(resourceDescriptor.getInformation().getType());
		IResource resource = repo.modifyResource(resourceDescriptor);
		return resource;
	}

	/**
	 * Remove the existing resource that matches the id
	 * @param resourceIdentifier
	 * @throws ResourceException
	 */
	public synchronized void removeResource(IResourceIdentifier resourceIdentifier) throws ResourceException {
		IResourceRepository repo = getResourceRepository(resourceIdentifier.getType());
		repo.removeResource(resourceIdentifier.getId());
	}
	
	/**
	 * Start the existing resource that matches the id
	 * @param resourceIdentifier
	 * @throws ResourceException
	 */
	public synchronized void startResource(IResourceIdentifier resourceIdentifier) throws ResourceException {
		IResourceRepository repo = getResourceRepository(resourceIdentifier.getType());
		repo.startResource(resourceIdentifier.getId());
	}
	
	/**
	 * Stop the existing resource that matches the id
	 * @param resourceIdentifier
	 * @throws ResourceException
	 */
	public synchronized void stopResource(IResourceIdentifier resourceIdentifier) throws ResourceException {
		IResourceRepository repo = getResourceRepository(resourceIdentifier.getType());
		repo.stopResource(resourceIdentifier.getId());
	}
	
	public synchronized void exportResourceDescriptor(IResourceIdentifier resourceIdentifier, String fileName) throws ResourceException {
		IResourceRepository repo = getResourceRepository(resourceIdentifier.getType());
		ResourceDescriptor resourceDescriptor = repo.getResource(resourceIdentifier.getId()).getResourceDescriptor();
		try{
			File file = new File(fileName);
			JAXBContext context = JAXBContext.newInstance(ResourceDescriptor.class);
			context.createMarshaller().marshal(resourceDescriptor, file);
			logger.info("ResourceDescriptor of resource "+resourceDescriptor.getId()+" exported to file "+fileName);
		}catch(Exception ex){
			throw new ResourceException(ex.getMessage(), ex);
		}
	}
	
	/**
	 * Get an existing resource
	 * @param engineIdentifier the id of the resource to get
	 * @return the resource
	 * @throws ResourceException
	 */
	public synchronized IResource getResource(IResourceIdentifier resourceIdentifier) throws ResourceException {
		IResourceRepository repo = getResourceRepository(resourceIdentifier.getType());
		return repo.getResource(resourceIdentifier.getId());
	}
	
	private IResourceRepository getResourceRepository(String resourceType) throws ResourceException{
		IResourceRepository repo = resourceRepositories.get(resourceType);
		if (repo == null){
			throw new ResourceException("Didn't find an engine repository for engine type: "+ resourceType);
		}
		return repo;
	}

	/**
	 * @return the resourceRepositories
	 */
	public Map<String, IResourceRepository> getResourceRepositories() {
		return resourceRepositories;
	}
}