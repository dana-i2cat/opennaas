package net.i2cat.mantychore.network.repository;

import net.i2cat.mantychore.network.model.NetworkModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.command.Response.Status;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.queue.QueueConstants;
import org.opennaas.core.resources.queue.QueueResponse;

public class NetworkBootstrapper implements IResourceBootstrapper {
	Log		log	= LogFactory.getLog(NetworkBootstrapper.class);

	IModel	oldModel;

	@Override
	public void resetModel(IResource resource) throws ResourceException {
		resource.setModel(new NetworkModel());
	}

	@Override
	public void bootstrap(IResource resource) throws ResourceException {
		log.info("Loading bootstrap to start resource...");

		oldModel = resource.getModel();
		resetModel(resource);

		/* start resource capabilities, this will load required data into model */
		for (ICapability capab : resource.getCapabilities()) {
			/* abstract capabilities have to be initialized */
			log.debug("Found a capability in the resource.");
			/* abstract capabilities have to be initialized */
			if (capab instanceof AbstractCapability
					// FIXME We can access to the capability but not to his implementation. Required to change this implementation
					&& capab.getCapabilityInformation() != null
					&& !capab.getCapabilityInformation().getType().equals("netqueue")) {
				log.debug("Executing capabilities startup...");
				Response response = ((AbstractCapability) capab).sendRefreshActions();
				if (!response.getStatus().equals(Status.OK)) {
					throw new ResourceException();
				}
			}
		}

		ICapability queueCapab = resource.getCapability(createQueueInformation());
		if (queueCapab != null) {
			QueueResponse response = (QueueResponse) queueCapab.sendMessage(QueueConstants.EXECUTE, resource.getModel());
			if (!response.isOk()) {
				// TODO IMPROVE ERROR REPORTING
				throw new ResourceException("Error during capabilities startup. Failed to execute startUp actions.");
			}
		}

		if (resource.getProfile() != null) {
			log.debug("Executing initModel from profile...");
			resource.getProfile().initModel(resource.getModel());
		}

		// If you have loaded network information
		if (resource.getResourceDescriptor().getNetworkTopology() != null) {
			resource.setModel(NetworkMapperDescriptorToModel.descriptorToModel(resource.getResourceDescriptor()));
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
