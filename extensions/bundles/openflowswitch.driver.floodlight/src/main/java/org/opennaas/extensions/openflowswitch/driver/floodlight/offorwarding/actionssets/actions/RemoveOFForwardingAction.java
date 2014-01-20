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

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.OpenflowForwardingActionSet;
import org.opennaas.extensions.openflowswitch.driver.floodlight.offorwarding.actionssets.FloodlightAction;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.IFloodlightStaticFlowPusherClient;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class RemoveOFForwardingAction extends FloodlightAction {

	@Override
	public boolean checkParams(Object params) throws ActionException {

		if (params == null || !(params instanceof String))
			throw new ActionException("Invalid parameters for action " + OpenflowForwardingActionSet.REMOVEOFFORWARDINGRULE);

		return true;
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {

		String flowId = (String) params;
		IFloodlightStaticFlowPusherClient client;
		String switchId;

		try {
			client = getFloodlightProtocolSession(protocolSessionManager).getFloodlightClientForUse();
			switchId = getSwitchIdFromSession(protocolSessionManager);
			FloodlightOFFlow flow = getFlowFromSwitchByName(flowId, switchId, client);
			client.deleteFlow(flow);
		} catch (Exception e) {
			throw new ActionException(e);
		}

		return ActionResponse.okResponse(OpenflowForwardingActionSet.REMOVEOFFORWARDINGRULE);
	}

	/**
	 * 
	 * @param flowName
	 * @param switchId
	 * @param client
	 * @return existing flow with given flowName in switch
	 * @throws Exception
	 * @throws ActionException
	 *             if there is no flow with given flowName in switch
	 * @throws ProtocolException
	 */
	private FloodlightOFFlow getFlowFromSwitchByName(String flowName, String switchId, IFloodlightStaticFlowPusherClient client)
			throws ProtocolException, ActionException, Exception {
		for (FloodlightOFFlow flow : client.getFlows(switchId)) {
			if (flow.getName().equals(flowName))
				return flow;
		}
		throw new ActionException("Given flow does not exist: " + flowName);
	}
}
