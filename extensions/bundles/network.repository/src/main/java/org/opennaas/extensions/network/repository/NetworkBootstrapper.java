package org.opennaas.extensions.network.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.extensions.network.model.NetworkModel;

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

		// Populate network model if we have a loaded network topology
		if (resource.getResourceDescriptor().getNetworkTopology() != null) {
			resource.setModel(NetworkMapperDescriptorToModel.descriptorToModel(resource.getResourceDescriptor()));
		}

		if (resource.getProfile() != null) {
			log.debug("Executing initModel from profile...");
			resource.getProfile().initModel(resource.getModel());
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
