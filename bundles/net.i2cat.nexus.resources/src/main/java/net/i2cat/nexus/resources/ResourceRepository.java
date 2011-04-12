package net.i2cat.nexus.resources;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.i2cat.nexus.persistence.GenericRepository;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.capability.ICapabilityFactory;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptorRepository;

/**
 * Base class for all the resource repository implementations.
 * 
 * @author Mathieu Leman (ITI)
 * 
 */
public class ResourceRepository implements IResourceRepository {
	/** logger */
	private Logger logger = LoggerFactory
			.getLogger(ResourceRepository.class);

	/** The map of running resource instances **/
	protected Map<String, IResource> resourceRepository = null;

	/** The resource descriptor repository **/
	protected GenericRepository<ResourceDescriptor, String> descriptorRepository = null;

	/** The  capability factories by capability id**/
	protected Map<String, ICapabilityFactory> capabilityFactories = null;

	/** This Repository's resource Type **/
	protected String resourceType = null;
	
	private Resource resource = null;
	
	private IResourceBootstrapperFactory bootstrapperFactory = null;
	
	private String persistenceUnit = null;

	/**
	 * Construct a new resource repository for resources of the given type
	 */
	public ResourceRepository(String resourceType) {
		logger.debug("Creating a new resource repository of type "+resourceType);
		this.resourceType = resourceType;
		this.capabilityFactories = new Hashtable<String,ICapabilityFactory>();
		this.resourceRepository = new Hashtable<String, IResource>();
	}
	
	public void setPersistenceUnit(String persistenceUnit){
		this.persistenceUnit = persistenceUnit;
	}
	
	public void init() {
		logger.debug("Initializing Resource Repository " + resourceType);
		initializeResourceDescriptorRepository();
		loadExistingResources();
	}
	
	public void initializeResourceDescriptorRepository(){
		ResourceDescriptorRepository resourceDescriptorRepository = new ResourceDescriptorRepository();
		resourceDescriptorRepository.setPersistenceUnit(persistenceUnit);
		try{
			resourceDescriptorRepository.initializeEntityManager();
			this.setResourceDescriptorRepository(resourceDescriptorRepository);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Construct a new resource repository for resources of the given type with the
	 * given list of CapabiltyFactories
	 * Just for testing!!!
	 */
	public ResourceRepository(String resourceType,
			Map<String,ICapabilityFactory> capabilityFactories) {
		this.resourceType = resourceType;
		this.capabilityFactories = capabilityFactories;
		this.resourceRepository = new Hashtable<String, IResource>();
	}
	
	public void setResourceDescriptorRepository(GenericRepository<ResourceDescriptor, String> descriptorRepository){
		this.descriptorRepository = descriptorRepository;
	}
	
	public void setResourceBootstrapperFactory(IResourceBootstrapperFactory bootstrapperFactory){
		this.bootstrapperFactory = bootstrapperFactory;
	}
	
	private void loadExistingResources(){
		try{
			logger.debug("Repository " + resourceType + " loading existing resources");
			List<ResourceDescriptor> descriptors = ((ResourceDescriptorRepository)descriptorRepository).getResourceDescriptors(resourceType);
			logger.debug("Repository " + resourceType + " found " + descriptors.size() + " resources");
			for(int i=0; i<descriptors.size(); i++){
				try{
					this.loadResource(descriptors.get(i));
				}catch(Exception ex){
					ex.printStackTrace();
					//TODO do something better?
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
			//TODO do something better?
		}
	}
	
	/**
	 * Loads and starts a resource into memory
	 * @param resourceDescriptor
	 * @throws ResourceException
	 */
	private void loadResource(ResourceDescriptor resourceDescriptor) throws ResourceException {
		logger.debug("Repository " + resourceType + " loading resource "+resourceDescriptor.getId());
		resource = new Resource();
		ResourceIdentifier resourceIdentifier = new ResourceIdentifier(resourceType, resourceDescriptor.getId());
		resource.setResourceIdentifier(resourceIdentifier);
		resource.setResourceDescriptor(resourceDescriptor);
		List<ICapability> capabilities = createCapabilities(resourceDescriptor);
		resource.setCapabilities(capabilities);
		if (bootstrapperFactory != null){
			resource.setBootstrapper(bootstrapperFactory.createResourceBootstrapper());
		}
		logger.info("Initializing Resource #" + resourceDescriptor.getId());
		
		//Start the resource
		resource.start();
		addResourceToRepository(resource);
	}

	/** Set the capability factories **/
	public void setCapabilityFactories(
			Map<String,ICapabilityFactory> capabilityFactories) {
		this.capabilityFactories = capabilityFactories;
	}

	/**
	 * Set the type of resource for the repository
	 * 
	 * @param resourceType
	 *            the resourceType to set
	 */
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	
	public IResource getResource(String identifier) throws ResourceException {
		IResource result = resourceRepository.get(identifier);
		if (result == null) {
			throw new ResourceException("No resource with ID " + identifier
					+ " was found.");
		}
		return result;
	}
	
	public List<IResource> listResources() {
		Iterator<String> ids = resourceRepository.keySet().iterator();
		List<IResource> result = new ArrayList<IResource>();

		while (ids.hasNext()) {
			result.add(resourceRepository.get(ids.next()));
		}
		return result;
	}

	public IResource createResource(ResourceDescriptor resourceDescriptor) throws ResourceException {
		logger.debug("Creating resource from configuration");
		checkResourceCanBeCreated(resourceDescriptor);
		resource = new Resource();
		resource.setResourceIdentifier(new ResourceIdentifier(resourceType));
		resource.setResourceDescriptor(resourceDescriptor);
		resourceDescriptor.setId(resource.getResourceIdentifier().getId());
		List<ICapability> capabilities = createCapabilities(resourceDescriptor);
		resource.setCapabilities(capabilities);
		if (bootstrapperFactory != null){
			resource.setBootstrapper(bootstrapperFactory.createResourceBootstrapper());
		}
		logger.info("Initializing Resource #" + resourceDescriptor.getId());
		
		//Start the resource
		resource.start();
		resourceDescriptor = persistResourceDescriptor(resourceDescriptor);
		addResourceToRepository(resource);
		return resource;
	}

	/**
	 * Checks if the resource type is valid so it can be created.
	 * 
	 * @param resourceDescriptor
	 * @throws ResourceException
	 */
	protected void checkResourceCanBeCreated(ResourceDescriptor resourceDescriptor)
			throws ResourceException {

		if (resourceType == null) {
			throw new ResourceException("The resourceType field cannot be null");
		}

		if (!resourceDescriptor.getInformation().getType().equals(resourceType)) {
			throw new ResourceException(
					"This repository cannot create resources of type "
							+ resourceDescriptor.getInformation().getType() + ", only of type "
							+ resourceType);
		}
	}

	private List<ICapability> createCapabilities(ResourceDescriptor resourceDescriptor) throws ResourceException {
		List<CapabilityDescriptor> capabilityDescriptors = resourceDescriptor.getCapabilityDescriptors();
		Iterator<CapabilityDescriptor> capabilityIterator = capabilityDescriptors.iterator();
		List<ICapability> capabilities = new ArrayList<ICapability>();
		while (capabilityIterator.hasNext()) {
			CapabilityDescriptor capabilityDescriptor = capabilityIterator.next();
			ICapabilityFactory factory = capabilityFactories.get(capabilityDescriptor.getCapabilityInformation().getType());

			if(factory== null){
				throw new ResourceException("Factory not found for capabilities of type: "+capabilityDescriptor.getCapabilityInformation().getType());
			}
			ICapability capability = factory.create(capabilityDescriptor, resourceDescriptor.getId().toString());		
			capabilities.add(capability);
		}
		return capabilities;
	}

	private void addResourceToRepository(IResource resource) {
		resourceRepository.put(resource.getResourceIdentifier().getId(), resource);
		logger.debug("New resource with id "
				+ resource.getResourceIdentifier().getId()
				+ " created an added to the repository");
	}

	private ResourceDescriptor persistResourceDescriptor(ResourceDescriptor descriptor) {
		if (descriptorRepository == null) {
			return null;
		}
		return descriptorRepository.save(descriptor);
	}

	public IResource modifyResource(ResourceDescriptor descriptor) throws ResourceException {
		IResource resource = null;
		// Check if descriptor is new if i t is create a new engine
		if (descriptor.getId() == null) {
			resource = createResource(descriptor);
		} else {
			// Get the old resource
			resource = getResource(descriptor.getId());

			// Get the old configuration in case the new fails
			ResourceDescriptor oldConfig = resource.getResourceDescriptor();

			// Set the new configuration and initialize the engine
			try {
				this.removeResource(descriptor.getId());
				resource = createResource(descriptor);
			} catch (ResourceException e) {
				logger.info("Could not modify configuration for resource ID #"
						+ resource.getResourceDescriptor().getId());
				// There was an error initializing, try to roll back
				try {
					resource = createResource(oldConfig);
					descriptor = oldConfig;
				} catch (ResourceException ex) {
					logger
							.error("Impossible to restore old configuration for resource ID #"
									+ resource.getResourceDescriptor().getId());
					throw ex;
				}
			}
		}

		return resource;
	}

	public void removeResource(String identifier) throws ResourceException {
		logger.debug("Removing resource runtime object for ID #"
				+ identifier);
		stopResource(identifier);
		logger.debug("Removing resource configuration for ID #"
				+ identifier);
		ResourceDescriptor config = descriptorRepository.findById(identifier);;
		if (config != null){
			descriptorRepository.delete(config);
		}
		resourceRepository.remove(identifier);
		//TODO The activeMQ queue for the engine should be destroyed
		logger.info("Removed resource with ID #" + identifier);
	}

	public void startResource(String identifier) throws ResourceException{
		logger.debug("Starting resource runtime object for ID #"
				+ identifier);
		resourceRepository.get(identifier).initialize();
		resourceRepository.get(identifier).activate();	
		logger.info("Started resource with ID #" + identifier);
	}
	
	public void stopResource(String identifier) throws ResourceException{
		logger.debug("Stoping resource runtime object for ID #"
				+ identifier);
		resourceRepository.get(identifier).deactivate();
		resourceRepository.get(identifier).shutdown();
		logger.info("Stoped resource with ID #" + identifier);
	}

	public Map<String,ICapabilityFactory> getCapabilityFactories() {
		return capabilityFactories;
	}

	/**
	 * @return the resourceType
	 */
	public String getResourceType() {
		return resourceType;
	}
}
