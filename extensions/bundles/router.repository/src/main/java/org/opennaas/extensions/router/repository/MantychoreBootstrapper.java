package org.opennaas.extensions.router.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ResourceNotFoundException;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.utils.ModelHelper;

public class MantychoreBootstrapper implements IResourceBootstrapper {
	Log		log	= LogFactory.getLog(MantychoreBootstrapper.class);

	IModel	oldModel;

	@Override
	public void resetModel(Resource resource) throws ResourceException {
		resource.setModel(new ComputerSystem());
		((ComputerSystem) resource.getModel()).setName(resource.getResourceDescriptor().getInformation().getName());
		if (isALogicalRouter(resource))
			((ComputerSystem) resource.getModel()).setElementName(resource.getResourceDescriptor().getInformation().getName());
	}

	@Override
	public void bootstrap(Resource resource) throws ResourceException {
		log.info("Loading bootstrap to start resource...");
		oldModel = resource.getModel();
		resetModel(resource);

		/* start its capabilities */
		for (ICapability capab : resource.getCapabilities()) {
			/* abstract capabilities have to be initialized */
			log.debug("Found a capability in the resource.");
			/* abstract capabilities have to be initialized */
			if (capab instanceof AbstractCapability) {
				log.debug("Executing capabilities startup...");
				((AbstractCapability) capab).sendRefreshActions();
			}
		}

		IQueueManagerCapability queueCapab = (IQueueManagerCapability) resource.getCapabilityByInterface(IQueueManagerCapability.class);
		QueueResponse response;
		try {
			response = queueCapab.execute();
			if (!response.isOk()) {
				// TODO IMPROVE ERROR REPORTING
				throw new ResourceException("Error during capabilities startup. Failed to execute startUp actions.");
			}
		} catch (ProtocolException e) {
			throw new ResourceException("Error during capabilities startup. Failed to execute startUp actions.", e);
		} catch (ActionException e) {
			throw new ResourceException("Error during capabilities startup. Failed to execute startUp actions.", e);
		} catch (CapabilityException e) {
			throw new ResourceException("Error during capabilities startup. Failed to execute startUp actions.", e);
		}

		if (resource.getProfile() != null) {
			log.debug("Executing initModel from profile...");
			resource.getProfile().initModel(resource.getModel());
		}

		if (ModelHelper.getInterfaces(((ComputerSystem) resource.getModel())).isEmpty()) {
			log.warn("Router has no interfaces");
		}

		// FIXME This should be part of refresh action
		initVirtualResources(resource);
	}

	/**
	 * FIXME this method should be part of refresh action. Remove it when moved!!!
	 * 
	 * @param resource
	 * @throws ResourceException
	 */
	private void initVirtualResources(IResource resource) throws ResourceException {

		if (resource.getModel() != null) {

			List<String> nameLogicalRouters = resource.getModel().getChildren();

			if (!nameLogicalRouters.isEmpty()) {

				log.debug("Loading child logical routers");

				/* resource type for all child logical devices is the same as the parent (physical) one */
				String typeResource = resource.getResourceIdentifier().getType();
				String parentId = resource.getResourceIdentifier().getId();
				IResourceManager resourceManager;
				try {
					resourceManager = Activator.getResourceManagerService();
				} catch (Exception e1) {
					throw new ResourceException("Could not get Resource Manager Service.");
				}

				/* initialize each resource */
				int createdResources = 0;
				for (String nameResource : nameLogicalRouters) {
					try {
						resourceManager.getIdentifierFromResourceName(typeResource, nameResource);
						log.info("A resource with this name already exists, omitting creation");
					} catch (ResourceNotFoundException e) {
						// TODO If the resource exists what it is our decision?
						log.error(e.getMessage());
						log.info("This resource is new, it have to be created");
						ResourceDescriptor newResourceDescriptor = newResourceDescriptor(resource.getResourceDescriptor(), nameResource, parentId);

						/* create new resources */
						resourceManager.createResource(newResourceDescriptor);
						createdResources++;
					}
				}
				log.debug("Loaded " + createdResources + " new logical routers");

				// FIXME If a resource is created, we have to delete the don't used resources
			}
		}
	}

	private ResourceDescriptor newResourceDescriptor(ResourceDescriptor resourceDescriptor, String nameResource, String parentId)
			throws ResourceException {

		try {
			ResourceDescriptor newResourceDescriptor = (ResourceDescriptor) resourceDescriptor.clone();

			// the profiles will not be cloned
			newResourceDescriptor.setProfileId("");

			// Wet set the resource name
			newResourceDescriptor.getInformation().setName(nameResource);

			/* added virtual description */
			Map<String, String> properties = new HashMap<String, String>();
			properties.put(ResourceDescriptor.VIRTUAL, "true");
			properties.put(ResourceDescriptor.HOSTED_BY, parentId);
			newResourceDescriptor.setProperties(properties);

			return newResourceDescriptor;
		} catch (Exception e) {
			throw new ResourceException(e.getMessage());
		}

	}

	public void createResource(MantychoreRepository repository, ResourceDescriptor descriptor) {
		/* Profile info is not cloned */
		// descriptor.setProfileId(profileName);
		IResource resource = null;
		try {
			log.info("Creating Resource ...... ");
			resource = repository.createResource(descriptor);
		} catch (ResourceException e) {
			log.error(e.getMessage());
		}
		log.info("Resource of type " + resource.getResourceDescriptor().getInformation().getType() + " created with name: "
				+ resource.getResourceDescriptor().getInformation().getName());
	}

	private Information createQueueInformation() {
		Information information = new Information();
		information.setType("queue");
		return information;
	}

	@Override
	public void revertBootstrap(Resource resource) throws ResourceException {
		resource.setModel(oldModel);
	}

	private boolean isALogicalRouter(IResource resource) {
		ResourceDescriptor resourceDescriptor = resource.getResourceDescriptor();
		/* Check that the logical router exists */
		if (resourceDescriptor == null || resourceDescriptor.getProperties() == null)
			return false;

		return (resourceDescriptor.getProperties().get(ResourceDescriptor.VIRTUAL) != null && resourceDescriptor.getProperties()
				.get(ResourceDescriptor.VIRTUAL).equals("true"));
	}
}
