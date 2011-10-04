package net.i2cat.luminis.ROADM.repository;

import net.i2cat.mantychore.model.ManagedElement;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.ResourceException;
import net.i2cat.nexus.resources.capability.AbstractCapability;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.command.Response;
import net.i2cat.nexus.resources.command.Response.Status;
import net.i2cat.nexus.resources.queue.QueueConstants;
import net.i2cat.nexus.resources.queue.QueueResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ROADMBootstrapper implements IResourceBootstrapper {
	Log						log	= LogFactory.getLog(ROADMBootstrapper.class);

	private ManagedElement	oldModel;

	@Override
	public void bootstrap(IResource resource) throws ResourceException {
		log.info("Loading bootstrap to start resource..");
		oldModel = resource.getModel();
		resource.setModel(new ProteusOpticalSwitch()); // TODO LUMINIS WORK WITH OPTICAL SWITCHES OR WITH PROTEUS OPTICAL SWITCHES

		// set name in model
		// FIXME do this in profile.initModel
		((ProteusOpticalSwitch) resource.getModel()).setName(resource.getResourceDescriptor().getInformation().getName());

		log.debug("Executing capabilities startup...");
		ICapability queueCapab = null;
		for (ICapability capab : resource.getCapabilities()) {
			if (capab instanceof AbstractCapability) {
				if (capab.getCapabilityInformation().getType().equalsIgnoreCase("queue")) {
					queueCapab = capab;
				} else {
					Response response = ((AbstractCapability) capab).sendRefreshActions();
					if (!response.getStatus().equals(Status.OK)) {
						throw new ResourceException(
								"Failed to send refreshActions of " + capab.getCapabilityInformation().getType() + " capability. " + response
										.getErrors().toString());
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
		resource.setModel(oldModel);
	}

}
