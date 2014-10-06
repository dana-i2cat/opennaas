package org.opennaas.extensions.openflowswitch.driver.floodlight.controllerinformation.actions;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch :: Floodlight driver v0.90
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
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.openflowswitch.capability.controllerinformation.model.HealthState;
import org.opennaas.extensions.openflowswitch.driver.floodlight.offorwarding.actionssets.FloodlightAction;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.controllerinformationclient.IFloodlightControllerInformationClient;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.controllerinformationclient.model.Healthy;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class GetHealthStateAction extends FloodlightAction {

	Log	log	= LogFactory.getLog(GetHealthStateAction.class);

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {

		log.info("Executing GetHealthState action.");

		try {
			// call Floodlight client
			IFloodlightControllerInformationClient client = getFloodlightProtocolSession(protocolSessionManager)
					.getFloodlightControllerInformationClientForUse();

			Healthy controllerState = client.getHealthState();

			HealthState healthState;
			if (controllerState.isHealthy())
				healthState = HealthState.OK;
			else
				healthState = HealthState.KO;

			// create action response with an object response
			ActionResponse response = new ActionResponse();
			response.setStatus(ActionResponse.STATUS.OK);
			response.setResult(healthState);

			log.debug("GetHealthStateAction successfully executed. Floodlight response is " + healthState);

			return response;

		} catch (Exception e) {
			log.error("Error geting Controller Health State from Floodlight Controller.", e);
			throw new ActionException(e);
		}
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		log.debug("Ignoring params for action " + this.actionID);

		return true;

	}

}
