package net.i2cat.mantychore.network.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.i2cat.mantychore.network.model.NetworkModel;

import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.command.Response.Status;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.queue.QueueConstants;
import org.opennaas.core.resources.queue.QueueResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NetworkBootstrapper implements IResourceBootstrapper {
	Log	log	= LogFactory.getLog(NetworkBootstrapper.class);

	IModel oldModel;
	
	public void resetModel (IResource resource) throws ResourceException {
		resource.setModel(new NetworkModel());		
	}

	public void bootstrap(IResource resource) throws ResourceException {
		log.info("Loading bootstrap to start resource...");
		
		oldModel = resource.getModel();
		resetModel(resource);
		
		/* start resource capabilities, this will load required data into model */
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

		//no children right now
//		manageChildren(resource);
	}
	
	private void manageChildren(IResource resource) throws ResourceException {
		
		/* the type resource is the same for all logical devices and for the physical device */
		String resourceType = resource.getResourceIdentifier().getType();
		IResourceManager resourceManager;
		try {
			resourceManager = Activator.getResourceManagerService();
		} catch (Exception e1) {
			throw new ResourceException("It was impossible get the Resource Manager Service to do execute the bootstrapper");
		}
		List<String> childrenNames = resource.getModel().getChildren();

		/* initialize each resource */
		for (String resourceName : childrenNames) {
				resourceManager.getIdentifierFromResourceName(resourceType, resourceName);
				// TODO If the resource exists, what is our decision?				

		}
	}
	

	private Information createQueueInformation() {
		Information information = new Information();
		information.setType("queue");
		return information;
	}

	@Override
	public void revertBootstrap(IResource resource) throws ResourceException {
		resource.setModel(oldModel);
	}
}
