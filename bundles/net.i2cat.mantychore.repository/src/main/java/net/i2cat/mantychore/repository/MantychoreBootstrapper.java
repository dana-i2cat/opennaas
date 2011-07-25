package net.i2cat.mantychore.repository;

import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceBootstrapper;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.capability.AbstractCapability;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.command.Response;
import net.i2cat.nexus.resources.command.Response.Status;
import net.i2cat.nexus.resources.queue.QueueConstants;
import net.i2cat.nexus.resources.queue.QueueResponse;

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
