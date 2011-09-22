package net.i2cat.mantychore.repository;

import net.i2cat.mantychore.model.ComputerSystem;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.command.Response.Status;
import org.opennaas.core.resources.queue.QueueConstants;
import org.opennaas.core.resources.queue.QueueResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MantychoreBootstrapper implements IResourceBootstrapper {
	Log	log	= LogFactory.getLog(MantychoreRepository.class);

	@Override
	public void bootstrap(IResource resource) throws ResourceException {
		log.info("Loading bootstrap to start resource...");
		resource.setModel(new ComputerSystem());

		log.debug("Executing capabilities startup...");
		ICapability queueCapab = null;
		for (ICapability capab : resource.getCapabilities()) {
			log.debug("Found a capability in the resource.");
			if (capab instanceof AbstractCapability) {
				if (capab.getCapabilityInformation().getType().equalsIgnoreCase("queue")) {
					queueCapab = capab;
				} else {
					Response response = ((AbstractCapability) capab).sendStartUpActions();
					if (!response.getStatus().equals(Status.OK)) {
						throw new ResourceException();
					}
				}
			}
		}
		QueueResponse response = (QueueResponse) queueCapab.sendMessage(QueueConstants.EXECUTE, resource.getModel());
		if (!response.isOk()) {
			// TODO IMPROVE ERROR REPORTING
			throw new ResourceException("Error during capabilities startup. Failed to execute startUp actions.");
		}

		if (resource.getProfile() != null) {
			log.debug("Executing initModel from profile...");
			resource.getProfile().initModel(resource.getModel());
		}

	}

	@Override
	public void revertBootstrap(IResource resource) throws ResourceException {
		resource.setModel(null);
	}
}
