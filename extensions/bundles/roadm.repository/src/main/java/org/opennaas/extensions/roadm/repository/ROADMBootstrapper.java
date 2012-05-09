package org.opennaas.extensions.roadm.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;

public class ROADMBootstrapper implements IResourceBootstrapper {
	Log				log	= LogFactory.getLog(ROADMBootstrapper.class);

	private IModel	oldModel;

	@Override
	public void bootstrap(IResource resource) throws ResourceException {
		log.info("Loading bootstrap to start resource..");
		oldModel = resource.getModel();
		resource.setModel(new ProteusOpticalSwitch()); // TODO LUMINIS WORK WITH OPTICAL SWITCHES OR WITH PROTEUS OPTICAL SWITCHES

		// set name in model
		// FIXME do this in profile.initModel
		((ProteusOpticalSwitch) resource.getModel()).setName(resource.getResourceDescriptor().getInformation().getName());

		log.debug("Executing capabilities startup...");
		for (ICapability capab : resource.getCapabilities()) {
			if (capab instanceof AbstractCapability) {
				try {
					((AbstractCapability) capab).sendRefreshActions();
				} catch (CapabilityException e) {
					throw new ResourceException(
							"Failed to send refreshActions of " + capab.getCapabilityInformation().getType() + " capability. ", e);
				}
			}
		}

		IQueueManagerCapability queueCapab = (IQueueManagerCapability) resource.getCapabilityByInterface(IQueueManagerCapability.class);
		try {
			QueueResponse response = (QueueResponse) queueCapab.execute();
			if (!response.isOk()) {
				// TODO IMPROVE ERROR REPORTING
				String errorString = prepareErrorMessage(response);
				throw new ResourceException("Error during capabilities startup. Failed to execute startUp actions. " + errorString);
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

	}

	private String prepareErrorMessage(QueueResponse response) {
		String errorString = "";
		for (ActionResponse response1 : response.getResponses()) {
			if (response1.getStatus().equals(ActionResponse.STATUS.ERROR)) {
				errorString += "Action " + response1.getActionID() + " failed: " + response1.getInformation() + "\n";
			}
		}
		if (response.getPrepareResponse().getStatus().equals(ActionResponse.STATUS.ERROR)) {
			errorString += "Action " + response.getPrepareResponse().getActionID() + " failed: " + response.getPrepareResponse().getInformation() + "\n";
		}
		if (response.getConfirmResponse().getStatus().equals(ActionResponse.STATUS.ERROR)) {
			errorString += "Action " + response.getConfirmResponse().getActionID() + " failed: " + response.getConfirmResponse().getInformation() + "\n";
		}
		if (response.getRestoreResponse().getStatus().equals(ActionResponse.STATUS.ERROR)) {
			errorString += "Action " + response.getRestoreResponse().getActionID() + " failed: " + response.getRestoreResponse().getInformation() + "\n";
		}
		return errorString;
	}

	@Override
	public void revertBootstrap(IResource resource) throws ResourceException {
		resource.setModel(oldModel);
	}

	public void resetModel(IResource resource) throws ResourceException {

	}

}
