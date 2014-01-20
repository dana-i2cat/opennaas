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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.persistence.GenericRepository;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorRepository;
import org.opennaas.core.resources.profile.IProfile;
import org.opennaas.core.resources.profile.IProfileManager;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

/**
 * Base class for all the resource repository implementations.
 * 
 * @author Mathieu Leman (ITI)
 * 
 */
public class ResourceRepository implements IResourceRepository {
	/** logger */
	private Log												logger					= LogFactory.getLog(ResourceRepository.class);

	/** The map of running resource instances **/
	protected Map<String, IResource>						resourceRepository		= null;

	/** The resource descriptor repository **/
	protected GenericRepository<ResourceDescriptor, String>	descriptorRepository	= null;

	/** The capability factories by capability id **/
	protected Map<String, ICapabilityFactory>				capabilityFactories		= null;

	/** This Repository's resource Type **/
	protected String										resourceType			= null;

	private IResourceBootstrapperFactory					bootstrapperFactory		= null;

	// @Inject
	// private IProtocolManager;

	/**
	 * Construct a new resource repository for resources of the given type
	 * 
	 * @throws ResourceException
	 */
	public ResourceRepository(String resourceType) {

		if (resourceType == null)
			throw new IllegalArgumentException("ResourceType can not be null");

		logger.debug("Creating a new resource repository of type " + resourceType);
		this.resourceType = resourceType;
		this.capabilityFactories = new Hashtable<String, ICapabilityFactory>();
		this.resourceRepository = new Hashtable<String, IResource>();
	}

	/**
	 * Construct a new resource repository for resources of the given type with the given list of CapabiltyFactories FIXME JUST FOR TESTING!!!
	 */
	public ResourceRepository(String resourceType,
			Map<String, ICapabilityFactory> capabilityFactories) {

		this(resourceType);
		this.capabilityFactories = capabilityFactories;
	}

	/* SETTERS AND GETTERS */
	public void setResourceDescriptorRepository(GenericRepository<ResourceDescriptor, String> descriptorRepository) {
		// TODO REMOVE
		if (descriptorRepository == null)
			throw new IllegalArgumentException();

		this.descriptorRepository = descriptorRepository;
	}

	/**
	 * Specialized setter for resourceDescriptorRepository.
	 * 
	 * The only purpose of this setter is to work around issues with Blueprint not being able to cast to the generic type of the real setter.
	 */
	public void setResourceDescriptorRepository(ResourceDescriptorRepository descriptorRepository) {
		setResourceDescriptorRepository((GenericRepository<ResourceDescriptor, String>) descriptorRepository);
	}

	public void setResourceBootstrapperFactory(IResourceBootstrapperFactory bootstrapperFactory) {
		this.bootstrapperFactory = bootstrapperFactory;
	}

	@Override
	public Map<String, ICapabilityFactory> getCapabilityFactories() {
		return capabilityFactories;
	}

	/** Set the capability factories **/
	public void setCapabilityFactories(
			Map<String, ICapabilityFactory> capabilityFactories) {
		this.capabilityFactories = capabilityFactories;
	}

	/**
	 * @return the resourceType
	 */
	@Override
	public String getResourceType() {
		return resourceType;
	}

	/**
	 * Set the type of resource for the repository
	 * 
	 * @param resourceType
	 *            the resourceType to set
	 */
	// public void setResourceType(String resourceType) {
	// this.resourceType = resourceType;
	// }

	@Override
	public List<IResource> listResources() {
		Iterator<String> ids = resourceRepository.keySet().iterator();
		List<IResource> result = new ArrayList<IResource>();

		while (ids.hasNext()) {
			result.add(resourceRepository.get(ids.next()));
		}
		return result;
	}

	@Override
	public IResource getResource(String identifier) throws ResourceException {
		IResource result = resourceRepository.get(identifier);
		if (result == null) {
			throw new ResourceException("No resource with ID " + identifier
					+ " was found.");
		}
		return result;
	}

	private void addResourceToRepository(IResource resource) {
		resourceRepository.put(resource.getResourceIdentifier().getId(), resource);
		logger.debug("New resource with id "
				+ resource.getResourceIdentifier().getId()
				+ " created an added to the repository");
	}

	private IResource removeResourceFromRepository(String resourceId) {
		IResource removed = resourceRepository.remove(resourceId);
		logger.debug("Removing resource with id "
				+ resourceId
				+ " from the repository");
		return removed;
	}

	public void init() throws ResourceException {
		logger.debug("Initializing Resource Repository " + resourceType);
		// try {
		loadExistingResources();
		// } catch (ResourceException e) {
		// logger.warn("Failed to load some resources from database");
		// }
	}

	/* RESOURCE METHODS */

	/**
	 * Uses a new resourceIdentifier to identify the resource
	 */
	@Override
	public IResource createResource(ResourceDescriptor resourceDescriptor) throws ResourceException {

		logger.debug("Creating resource from configuration");

		// Each repository can override this method to add new conditions to create a resource
		checkResourceCanBeCreated(resourceDescriptor);

		logger.debug("Resource Checked");

		IResource resource;

		try {
			resource = initResource(new Resource(), resourceDescriptor, new ResourceIdentifier(resourceType));
		} catch (CorruptStateException e) {
			throw new ResourceException(e);
		}

		if (protocolManagerIsAvailable()) {
			try {
				createProtocolSessionManagerForResource(resource.getResourceIdentifier().getId());
			} catch (ActivatorException e) {
				// ignored, protocolManager availability is already checked in protocolManagerIsAvailable()
				logger.warn("Ignoring fail to retrieve protocolManager during createProtocolSessionManagerForResource");
			}
		} else {
			logger.warn("Unable to create protocolSessionManager for resource " + resourceDescriptor.getInformation().getName() + ". ProtocolManager is not available.");
		}

		logger.debug("Resource Initialized");
		resourceDescriptor = persistResourceDescriptor(resourceDescriptor);

		logger.debug("Resource Persisted");

		return resource;
	}

	@Override
	public void removeResource(String identifier) throws ResourceException {
		logger.info("Removing resource with ID #" + identifier);

		IResource resource = getResource(identifier);
		shutdownResource(identifier);
		if (protocolManagerIsAvailable()) {
			try {
				removeProtocolSessionManagerForResource(identifier);
			} catch (ActivatorException e) {
				// ignored, protocolManager availability is already checked in protocolManagerIsAvailable()
				logger.warn("Ignoring fail to retrieve protocolManager during removeProtocolSessionManagerForResource");
			}
		}
		unpersistResourceDescriptor(resource.getResourceDescriptor());
	}

	@Override
	public IResource modifyResource(String identifier, ResourceDescriptor descriptor) throws ResourceException {

		try {

			// Get the old resource
			Resource resource = (Resource) getResource(identifier);
			ResourceDescriptor oldConfig = resource.getResourceDescriptor();

			IResourceIdentifier resourceIdentifier = resource.getResourceIdentifier();

			validateResourceDescriptor(descriptor);

			try {
				shutdownResource(identifier);
				unpersistResourceDescriptor(oldConfig);

				// Keep old resourceIdentifier, but use new descriptor
				initResource(resource, descriptor, resourceIdentifier);
				persistResourceDescriptor(descriptor);
			} catch (ResourceException e) {
				logger.info("Could not modify configuration for resource ID #"
						+ resource.getResourceDescriptor().getId());
				try {
					// restore old config
					initResource(resource, oldConfig, resourceIdentifier);
					persistResourceDescriptor(oldConfig);
				} catch (ResourceException e1) {
					logger.error("Impossible to restore old configuration for resource ID #"
							+ resource.getResourceDescriptor().getId());
					throw e1;
				}
				throw e;
			}

			return resource;

		} catch (CorruptStateException e) {
			throw new ResourceException(e);
		}
	}

	@Override
	public void startResource(String identifier) throws ResourceException {
		logger.debug("Starting resource runtime object for ID #"
				+ identifier);
		try {
			activateResource(identifier);
		} catch (CorruptStateException e) {
			throw new ResourceException(e);
		}
		logger.info("Started resource with ID #" + identifier);
	}

	@Override
	public void stopResource(String identifier) throws ResourceException {
		logger.debug("Stopping resource runtime object for ID #"
				+ identifier);
		try {
			deactivateResource(identifier);
		} catch (CorruptStateException e) {
			throw new ResourceException(e);
		}
		logger.info("Stopped resource with ID #" + identifier);
	}

	@Override
	public void forceStopResource(String identifier) throws ResourceException {
		logger.debug("Stopping resource runtime object for ID #"
				+ identifier);
		forceDeactivateResource(identifier);
		logger.info("Stopped resource with ID #" + identifier);
	}

	public void loadExistingResources() throws ResourceException {
		try {
			logger.debug("Repository " + resourceType + " loading existing resources");
			List<ResourceDescriptor> descriptors = ((ResourceDescriptorRepository) descriptorRepository).getResourceDescriptors(resourceType);
			logger.debug("Repository " + resourceType + " found " + descriptors.size() + " resources");

			for (int i = 0; i < descriptors.size(); i++) {
				this.loadResource(descriptors.get(i));
			}
		} catch (Exception ex) {
			// it should fail if a single resource fail to load.
			throw new ResourceException(ex);
		}
	}

	/**
	 * Loads and starts a resource into memory. Uses resourceDescriptor id to identify the resource
	 * 
	 * @param resourceDescriptor
	 * @throws ResourceException
	 */
	private void loadResource(ResourceDescriptor resourceDescriptor) throws ResourceException {
		logger.debug("Repository " + resourceType + " loading resource " + resourceDescriptor.getId());

		ResourceIdentifier resourceIdentifier = new ResourceIdentifier(resourceType, resourceDescriptor.getId());

		try {
			initResource(new Resource(), resourceDescriptor, resourceIdentifier);
		} catch (CorruptStateException e) {
			throw new ResourceException(e);
		}
		if (protocolManagerIsAvailable()) {
			try {
				createProtocolSessionManagerForResource(resourceIdentifier.getId());
			} catch (ActivatorException e) {
				// ignored, protocolManager availability is already checked in protocolManagerIsAvailable()
				logger.warn("Ignoring fail to retrieve protocolManager during createProtocolSessionManagerForResource");
			}
		} else {
			logger.warn("Unable to create protocolSessionManager for resource " + resourceDescriptor.getInformation().getName() + ". ProtocolManager is not available.");
		}
	}

	private IResource initResource(Resource resource, ResourceDescriptor resourceDescriptor, IResourceIdentifier resourceIdentifier)
			throws ResourceException, CorruptStateException {

		logger.debug("Initializing resource " + resourceIdentifier.getId() + " ...");
		// keeping variables to make rollback
		IResourceIdentifier oldIdentifier = resource.getResourceIdentifier();
		ResourceDescriptor oldDescriptor = resource.getResourceDescriptor();
		String oldId = resourceDescriptor.getId();

		// Setting the variables for the resource
		resource.setResourceIdentifier(resourceIdentifier);
		resourceDescriptor.setId(resource.getResourceIdentifier().getId());
		resource.setResourceDescriptor(resourceDescriptor);

		addResourceToRepository(resource);

		try {
			try {
				resource.initialize();
			} catch (IncorrectLifecycleStateException e) {
				throw new ResourceException(e);
			}
		} catch (ResourceException re) {
			// roll back
			resource.setResourceIdentifier(oldIdentifier);
			resource.setResourceDescriptor(oldDescriptor);
			resourceDescriptor.setId(oldId);
			throw re;
		}

		logger.debug("Resource initialized");
		return resource;
	}

	private void activateResource(String resourceId) throws ResourceException, CorruptStateException {
		logger.debug("Activating resource " + resourceId + " ...");

		Resource resource = (Resource) getResource(resourceId);

		// Each repository can override this method to add new conditions to start a resource
		checkResourceCanBeStarted(resource);

		if (protocolManagerIsAvailable()) {
			try {
				createProtocolSessions(resourceId);
			} catch (Exception e) {
				try {
					destroyProtocolSessions(resourceId);
				} catch (Exception e1) {
					logger.warn("Error destroying protocol sessions", e1);
				}
				throw new ResourceException(e);
			}
		}

		/* prepare capabilities */
		logger.debug("  Obtaining capabilities...");
		List<? extends ICapability> oldCapabilities = resource.getCapabilities();
		List<ICapability> capabilities = createCapabilities(resource);
		resource.setCapabilities(capabilities);
		logger.debug("  Capabilities obtained. Loading bootstrapper...");

		/* prepare bootstrapper */
		IResourceBootstrapper oldBootstrapper = resource.getBootstrapper();
		if (bootstrapperFactory != null) {
			resource.setBootstrapper(bootstrapperFactory.createResourceBootstrapper());
		}
		logger.debug("  Bootstrapper loaded");

		/* get profile id and load profile from its bundle */
		IProfile oldProfile = resource.getProfile();
		if (resource.getResourceDescriptor().getProfileId() != null &&
				!resource.getResourceDescriptor().getProfileId().isEmpty()) {
			logger.debug("  Loading profile...");
			loadProfileInResource(resource, resource.getResourceDescriptor().getProfileId());
			logger.debug("  Profile loaded");
		}

		try {

			try {
				resource.activate();
				logger.debug("Resource activated");
			} catch (IncorrectLifecycleStateException e) {

				throw new ResourceException(e);

			}

		} catch (ResourceException re) {
			// roll back
			logger.debug("Rolling back activation...");

			resource.setCapabilities(oldCapabilities);
			resource.setBootstrapper(oldBootstrapper);
			resource.setProfile(oldProfile);

			if (protocolManagerIsAvailable()) {
				try {
					destroyProtocolSessions(resourceId);
				} catch (Exception e) {
					logger.warn("Error destroying protocol sessions", e);
				}
			}

			logger.debug("Rolling back done");
			throw re;
		}
	}

	private void deactivateResource(String resourceId) throws ResourceException, CorruptStateException {
		logger.debug("Deactivating resource " + resourceId + " ...");

		Resource resource = (Resource) getResource(resourceId);

		try {

			resource.deactivate();

		} catch (IncorrectLifecycleStateException e) {

			throw new ResourceException(e);
		}

		try {

			if (resource.getResourceDescriptor().getProfileId() != null && !resource.getResourceDescriptor().getProfileId().isEmpty()) {
				unregisterProfileInResource(resource);
				logger.debug("  Profile removed from resource");
			}

			resource.setBootstrapper(null);
			logger.debug("  Bootstrapper removed from resource");

			resource.setCapabilities(new ArrayList<ICapability>());
			logger.debug("  Capabilities removed from resource");

			if (protocolManagerIsAvailable()) {
				try {
					destroyProtocolSessions(resourceId);
				} catch (Exception e) {
					logger.warn("Error destorying protocolSessions", e);
				}
			}

			logger.debug("Resource deactivated");

		} catch (ResourceException e) {
			// roll back
			try {
				resource.activate();
			} catch (IncorrectLifecycleStateException e1) {
				throw new CorruptStateException("Failed to roll back deactivateResource.", e1);
			} catch (ResourceException e1) {
				throw new CorruptStateException("Failed to roll back deactivateResource.", e1);
			}
		}
	}

	private void shutdownResource(String resourceId) throws ResourceException {

		logger.debug("Shutting down resource " + resourceId + " ...");
		Resource resource = (Resource) getResource(resourceId);

		try {
			resource.shutdown();
		} catch (IncorrectLifecycleStateException e) {
			throw new ResourceException(e);
		}

		removeResourceFromRepository(resourceId);

		logger.debug("Resource shut down");
	}

	/**
	 * Checks if the resource is valid to be started
	 * 
	 * @param resourceDescriptor
	 * @throws ResourceException
	 */
	protected void checkResourceCanBeStarted(IResource resource)
			throws ResourceException {
		// by default, any resource can be started

	}

	/**
	 * Checks if the resource type is valid so it can be created.
	 * 
	 * @param resourceDescriptor
	 * @throws ResourceException
	 */
	protected void checkResourceCanBeCreated(ResourceDescriptor resourceDescriptor)
			throws ResourceException {

		validateResourceDescriptor(resourceDescriptor);

		for (String resourceID : resourceRepository.keySet()) {
			String name = resourceRepository.get(resourceID).getResourceDescriptor().getInformation().getName();
			if (name.equals(resourceDescriptor.getInformation().getName())) {
				throw new ResourceException(
						"There is already a resource in this respository with this name: " + resourceDescriptor.getInformation().getName());
			}
		}

	}

	protected void validateResourceDescriptor(ResourceDescriptor resourceDescriptor) throws ResourceException {

		assert (resourceType != null);
		// if (resourceType == null) {
		// throw new ResourceException("The resourceType field cannot be null");
		// }

		if (resourceDescriptor.getInformation().getName() == null || resourceDescriptor.getInformation().getName().equals("")) {
			throw new ResourceException(
					"The resourceName field cannot be null");
		}
		if (!resourceDescriptor.getInformation().getType().equals(resourceType)) {
			throw new ResourceException(
					"This repository cannot create resources of type "
							+ resourceDescriptor.getInformation().getType() + ", only of type "
							+ resourceType);
		}
	}

	private List<ICapability> createCapabilities(IResource resource) throws ResourceException {
		List<CapabilityDescriptor> capabilityDescriptors;

		try {
			capabilityDescriptors = resource.getResourceDescriptor().getCapabilityDescriptors();
		} catch (Exception e) {
			throw new ResourceException("No capability descriptor found, this will create an unusable resource. Please, check the descriptor format",
					e);
		}

		List<ICapability> capabilities = new ArrayList<ICapability>();
		if (capabilityDescriptors == null) {
			logger.warn("No capability descriptor found, this will create an unusable resource. Please, check the descriptor format");
		} else {
			Iterator<CapabilityDescriptor> capabilityIterator = capabilityDescriptors.iterator();
			while (capabilityIterator.hasNext()) {
				CapabilityDescriptor capabilityDescriptor = capabilityIterator.next();
				ICapabilityFactory factory = capabilityFactories.get(capabilityDescriptor.getCapabilityInformation().getType());
				if (factory == null) {
					throw new ResourceException("Factory not found for capabilities of type: " + capabilityDescriptor.getCapabilityInformation()
							.getType());
				}
				ICapability capability = factory.create(resource);
				capabilities.add(capability);
			}
		}
		return capabilities;
	}

	private void loadProfileInResource(Resource resource, String profileId) throws ResourceException {

		try {
			IProfileManager profileManager = Activator.getProfileManagerService();
			IProfile profile = profileManager.getProfile(profileId);

			profileManager.registerResource(profileId, resource);

			resource.setProfile(profile);

		} catch (Exception e) {
			throw new ResourceException("Failed to load resource profile", e);
		}
	}

	private void unregisterProfileInResource(Resource resource) throws ResourceException {
		try {

			String profileId = resource.getResourceDescriptor().getProfileId();

			if (!profileId.isEmpty()) {

				IProfileManager profileManager = Activator.getProfileManagerService();
				profileManager.unregisterResource(profileId, resource);

				resource.setProfile(null);
			}
		} catch (ResourceException e) {
			// IGNORED: if there's no such profileId there's nothing to unregister
		} catch (Exception e) {
			// could not access ProfileManager Service
			throw new ResourceException("Failed to unregister resource profile", e);
		}
	}

	private void forceDeactivateResource(String resourceId) throws ResourceException {
		logger.debug("Deactivating resource " + resourceId + " ...");
		// TODO Remove cast (put forceDeactivate into IResource iface)
		Resource resource = (Resource) getResource(resourceId);

		String messageErrors = "";
		try {
			resource.forceDeactivate();
		} catch (ResourceException e) {
			messageErrors = e.getLocalizedMessage() + '\n' + messageErrors;
		}

		try {
			if (!resource.getResourceDescriptor().getProfileId().isEmpty()) {
				forceUnregisterProfileInResource(resource);
				logger.debug("  Profile removed from resource");
			}
		} catch (ResourceException e) {
			messageErrors = e.getLocalizedMessage() + '\n' + messageErrors;
		}

		resource.setBootstrapper(null);
		logger.debug("  Bootstrapper removed from resource");

		resource.setCapabilities(new ArrayList<ICapability>());
		logger.debug("  Capabilities removed from resource");

		logger.debug("Resource deactivated");
	}

	private void forceUnregisterProfileInResource(Resource resource) throws ResourceException {
		try {

			String profileId = resource.getResourceDescriptor().getProfileId();

			if (!profileId.isEmpty()) {

				IProfileManager profileManager = Activator.getProfileManagerService();
				profileManager.unregisterResource(profileId, resource);

				resource.setProfile(null);
			}
		} catch (Exception e) {
			// TODO: log.error()
		}
	}

	private ResourceDescriptor persistResourceDescriptor(ResourceDescriptor descriptor) throws ResourceException {
		if (descriptorRepository == null) {
			throw new ResourceException("Failed to persist resource. No descriptorRepository found.");
		}
		return descriptorRepository.save(descriptor);
	}

	private void unpersistResourceDescriptor(ResourceDescriptor descriptor) throws ResourceException {
		if (descriptorRepository == null) {
			throw new ResourceException("Failed to unpersist resource. No descriptorRepository found.");
		}

		descriptorRepository.delete(descriptor);
	}

	private void createProtocolSessionManagerForResource(String resourceId) throws ActivatorException {
		try {
			getProtocolManager().getProtocolSessionManager(resourceId);
		} catch (ProtocolException e) {
			// ignored:
			// it can only fail for resourceId already associated to a protocolSession (nothing wrong then)
			logger.debug("Could not destroy protocolSessionManager. Given resourceId has none associated.");
		}
	}

	private void removeProtocolSessionManagerForResource(String resourceId) throws ActivatorException {
		try {
			getProtocolManager().destroyProtocolSessionManager(resourceId);
		} catch (ProtocolException e) {
			// ignored:
			// cannot happen
			logger.debug("Could not destroy protocolSessionManager for an unknown reason.", e);
		}
	}

	/**
	 * Destroys all sessions for given resourceId
	 * 
	 * @param resourceId
	 * @throws ProtocolException
	 * @throws Exception
	 */
	private void destroyProtocolSessions(String resourceId) throws ProtocolException, ActivatorException {
		IProtocolSessionManager sessionManager = getProtocolManager().getProtocolSessionManager(resourceId);
		for (String sessionId : sessionManager.getAllProtocolSessionIds()) {
			sessionManager.destroyProtocolSession(sessionId);
		}
	}

	/**
	 * Create one session per registered context
	 * 
	 * @param resourceId
	 * @throws Exception
	 */
	private void createProtocolSessions(String resourceId) throws ProtocolException, ActivatorException {
		IProtocolSessionManager sessionManager = getProtocolManager().getProtocolSessionManager(resourceId);
		for (ProtocolSessionContext context : sessionManager.getRegisteredContexts()) {
			sessionManager.obtainSession(context, false); // create session but do not lock it
		}
	}

	private IProtocolManager getProtocolManager() throws ActivatorException {
		return Activator.getProtocolManagerService();
	}

	private boolean protocolManagerIsAvailable() {
		IProtocolManager protocolManager = null;
		try {
			protocolManager = Activator.getProtocolManagerService();
		} catch (ActivatorException e) {
		}

		return protocolManager != null;
	}

}
