package org.opennaas.extensions.openflowswitch.driver.ryu.offorwarding.actionssets.actions;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch :: Ryu driver v3.14
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
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.OpenflowForwardingActionSet;
import org.opennaas.extensions.openflowswitch.driver.ryu.offorwarding.actionssets.RyuAction;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.IRyuStatsClient;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.model.RyuOFFlow;
import org.opennaas.extensions.openflowswitch.model.OFFlow;
import org.opennaas.extensions.openflowswitch.model.OpenflowSwitchModel;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class RemoveOFForwardingAction extends RyuAction {

	public RemoveOFForwardingAction() {
		this.actionID = OpenflowForwardingActionSet.REMOVEOFFORWARDINGRULE;
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (params == null || !(params instanceof String))
			throw new ActionException("Invalid parameters for action " + this.actionID);

		return true;
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
		IRyuStatsClient client;
		try {
			client = getRyuProtocolSession(protocolSessionManager).getRyuClientForUse();

			RyuOFFlow flow = (RyuOFFlow) getFlowByName((String) params);

			client.deleteFlowEntryStrictly(flow);

			((OpenflowSwitchModel) getModel()).getOfTables().get(0).getOfForwardingRules().remove(flow);

		} catch (Exception e) {
			log.error("Error removing forwarding rule " + params, e);
			throw new ActionException(e);
		}

		ActionResponse response = new ActionResponse();
		response.setStatus(STATUS.OK);

		return response;
	}

	private OFFlow getFlowByName(String flowName) throws ProtocolException, Exception {

		for (OFFlow flow : ((OpenflowSwitchModel) getModel()).getOfTables().get(0).getOfForwardingRules()) {
			if (flow.getName().equals(flowName))
				return flow;
		}

		throw new ActionException("There's no forwarding rule with id " + flowName);
	}
}
