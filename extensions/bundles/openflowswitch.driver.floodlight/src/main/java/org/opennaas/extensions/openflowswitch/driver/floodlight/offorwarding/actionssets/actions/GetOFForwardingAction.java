package org.opennaas.extensions.openflowswitch.driver.floodlight.offorwarding.actionssets.actions;

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

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.openflowswitch.driver.floodlight.offorwarding.actionssets.FloodlightAction;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.IFloodlightStaticFlowPusherClient;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.openflowswitch.model.OFFlow;

/**
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class GetOFForwardingAction extends FloodlightAction {

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
		List<OFFlow> flows = new ArrayList<OFFlow>();

		try {
			// obtain the switch ID from the protocol session
			String switchId = getSwitchIdFromSession(protocolSessionManager);
			IFloodlightStaticFlowPusherClient client = getFloodlightProtocolSession(protocolSessionManager).getFloodlightClientForUse();
			List<FloodlightOFFlow> floodlightFlows = client.getFlows(switchId);

			for (FloodlightOFFlow floodlightOFFlow : floodlightFlows) {
				OFFlow flow = new OFFlow(floodlightOFFlow);
				flows.add(flow);
			}

		} catch (Exception e) {
			throw new ActionException(e);
		}

		ActionResponse response = new ActionResponse();
		response.setStatus(ActionResponse.STATUS.OK);
		response.setResult(flows);

		return response;
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (params != null)
			throw new ActionException("Invalid parameters for action " + this.actionID);

		return true;
	}

}
