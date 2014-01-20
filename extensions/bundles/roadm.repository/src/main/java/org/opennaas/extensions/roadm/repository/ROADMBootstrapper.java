package org.opennaas.extensions.roadm.repository;

/*
 * #%L
 * OpenNaaS :: ROADM :: Repository
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.Resource;
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
	public void bootstrap(Resource resource) throws ResourceException {
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
	public void revertBootstrap(Resource resource) throws ResourceException {
		resource.setModel(oldModel);
	}

	@Override
	public void resetModel(Resource resource) throws ResourceException {

	}

}
