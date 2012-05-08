package org.opennaas.extensions.bod.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.queue.QueueConstants;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.domain.NetworkDomain;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;

public class BoDBootstrapper implements IResourceBootstrapper {

	Log				log	= LogFactory.getLog(BoDBootstrapper.class);

	private IModel	oldModel;

	public void bootstrap(IResource resource) throws ResourceException {

		log.info("Loading bootstrap to start resource...");
		oldModel = resource.getModel();
		resetModel(resource);

		/* start resource capabilities */
		for (ICapability capab : resource.getCapabilities()) {
			log.debug("Found a capability in the resource");
			/* abstract capabilities have to be initialized */
			if (capab instanceof AbstractCapability) {
				log.debug("Executing capabilities startup...");

				try {
					((AbstractCapability) capab).sendRefreshActions();
				} catch (CapabilityException e) {
					throw new ResourceException(
							"Failed to send refresActions of " + capab.getCapabilityInformation().getType(), e);
				}
			}
		}

		IQueueManagerCapability queueCapab = (IQueueManagerCapability) resource.getCapability(createQueueInformation());
		if (queueCapab != null) {
			QueueResponse response = (QueueResponse) queueCapab.sendMessage(QueueConstants.EXECUTE, resource.getModel());
			if (!response.isOk()) {
				throw new ResourceException("Error during capabilities startup. Failed to execute startUp actions.");
			}
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
	public void resetModel(IResource resource) throws ResourceException {

		NetworkDomain networkDomain = new NetworkDomain();
		ResourceDescriptor resourceDescriptor = resource.getResourceDescriptor();
		Information information = resourceDescriptor.getInformation();

		networkDomain.setName(information.getName());

		NetworkModel networkModel = new NetworkModel();
		networkModel.getNetworkElements().add(networkDomain);

		resource.setModel(new NetworkModel());
	}

	@Override
	public void revertBootstrap(IResource resource) throws ResourceException {

		resource.setModel(oldModel);
	}

}
