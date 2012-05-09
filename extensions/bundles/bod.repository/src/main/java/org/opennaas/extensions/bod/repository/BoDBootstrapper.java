package org.opennaas.extensions.bod.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.protocol.ProtocolException;
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

		IQueueManagerCapability queueCapab = null;
		try {
			queueCapab = (IQueueManagerCapability) resource.getCapabilityByInterface(IQueueManagerCapability.class);
		} catch (ResourceException e) {
			// ignored
		}
		if (queueCapab != null) {
			try {
				QueueResponse response = queueCapab.execute();
				if (!response.isOk()) {
					throw new ResourceException("Error during capabilities startup. Failed to execute startUp actions.");
				}
			} catch (ProtocolException e) {
				throw new ResourceException("Error during capabilities startup. Failed to execute startUp actions.", e);
			} catch (ActionException e) {
				throw new ResourceException("Error during capabilities startup. Failed to execute startUp actions.", e);
			} catch (CapabilityException e) {
				throw new ResourceException("Error during capabilities startup. Failed to execute startUp actions.", e);
			}
		}

		if (resource.getProfile() != null) {
			log.debug("Executing initModel from profile...");
			resource.getProfile().initModel(resource.getModel());
		}
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
