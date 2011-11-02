package net.i2cat.mantychore.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.i2cat.mantychore.model.ComputerSystem;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ResourceNotFoundException;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.command.Response.Status;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.queue.QueueConstants;
import org.opennaas.core.resources.queue.QueueResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MantychoreBootstrapper implements IResourceBootstrapper {
	Log	log	= LogFactory.getLog(MantychoreRepository.class);

	public void bootstrap(IResource resource) throws ResourceException {
		log.info("Loading bootstrap to start resource...");
		resource.setModel(new ComputerSystem());
		
		if (isALogicalRouter(resource))
			((ComputerSystem)resource.getModel()).setElementName(resource.getResourceDescriptor().getInformation().getName());
		
		/* start its capabilities */
		for (ICapability capab : resource.getCapabilities()) {

			/* abstract capabilities have to be initialized */
			log.debug("Found a capability in the resource.");
			/* abstract capabilities have to be initialized */
			if (capab instanceof AbstractCapability) {
				log.debug("Executing capabilities startup...");
				Response response = ((AbstractCapability) capab).sendRefreshActions();
				if (!response.getStatus().equals(Status.OK)) {
					throw new ResourceException();
				}
			}
		}
		ICapability queueCapab = resource.getCapability(createQueueInformation());
		QueueResponse response = (QueueResponse) queueCapab.sendMessage(QueueConstants.EXECUTE, resource.getModel());
		if (!response.isOk()) {
			// TODO IMPROVE ERROR REPORTING
			throw new ResourceException("Error during capabilities startup. Failed to execute startUp actions.");
		}

		if (resource.getProfile() != null) {
			log.debug("Executing initModel from profile...");
			resource.getProfile().initModel(resource.getModel());
		}

		/* the type resource is the same for all logical devices and for the physical device */
		String typeResource = resource.getResourceIdentifier().getType();
		IResourceManager resourceManager;
		try {
			resourceManager = Activator.getResourceManagerService();
		} catch (Exception e1) {
			throw new ResourceException("It was impossible get the Resource Manager Service to do execute the bootstrapper");
		}
		List<String> nameLogicalRouters = resource.getModel().getChildren();

		/* intialize each resource */
		for (String nameResource : nameLogicalRouters) {
			try {
				resourceManager.getIdentifierFromResourceName(typeResource, nameResource);
			} catch (ResourceNotFoundException e) {
				// TODO If the resource exists what it is our decision?
				log.error(e.getMessage());
				log.info("This resource is new, it have to be created");
				ResourceDescriptor newResourceDescriptor = newResourceDescriptor(resource.getResourceDescriptor(), nameResource);

				/* create new resources */
				resourceManager.createResource(newResourceDescriptor);
			}
		}

		// FIXME If a resource is created, we have to delete the don't used resources

	}

	private ResourceDescriptor newResourceDescriptor(ResourceDescriptor resourceDescriptor, String nameResource) throws ResourceException {

		try {
			ResourceDescriptor newResourceDescriptor = (ResourceDescriptor) resourceDescriptor.clone();

			// the profiles will not be cloned
			newResourceDescriptor.setProfileId("");
			// we delete chassis capability, a logical resource can't create new logical devices or new interfaces
			newResourceDescriptor.removeCapabilityDescriptor("chassis");
			// Wet set the resource name
			newResourceDescriptor.getInformation().setName(nameResource);

			/* added virtual description */
			Map<String, String> properties = new HashMap<String, String>();
			properties.put(ResourceDescriptor.VIRTUAL, "true");
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
	public void revertBootstrap(IResource resource) throws ResourceException {
		// TODO REVERT BOOTSTRAP IN ITS CHILDREN
		resource.setModel(null);
	}
	
	private boolean isALogicalRouter(IResource resource) {
		ResourceDescriptor resourceDescriptor = resource.getResourceDescriptor();
		/* Check that the logical router exists */
		if (resourceDescriptor == null || resourceDescriptor.getProperties() == null)
			return false;

		return (resourceDescriptor.getProperties().get(ResourceDescriptor.VIRTUAL) != null
				&& resourceDescriptor.getProperties().get(ResourceDescriptor.VIRTUAL).equals("true"));
	}
}
