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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.OpenflowForwardingActionSet;
import org.opennaas.extensions.openflowswitch.driver.ryu.offorwarding.actionssets.RyuAction;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.IRyuStatsClient;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.model.RyuOFFlow;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.wrappers.RyuOFFlowListWrapper;
import org.opennaas.extensions.openflowswitch.helpers.ForwardingRuleUtils;
import org.opennaas.extensions.openflowswitch.model.OFFlow;
import org.opennaas.extensions.openflowswitch.model.OpenflowSwitchModel;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class GetOFForwardingAction extends RyuAction {

	private Log	log	= LogFactory.getLog(GetOFForwardingAction.class);

	public GetOFForwardingAction() {
		this.actionID = OpenflowForwardingActionSet.GETFLOWS;
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (params != null)
			log.warn("Params ignored in action " + this.actionID);

		return true;
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {

		List<OFFlow> flows = new ArrayList<OFFlow>();

		try {
			String switchId = getSwitchIdFromSession(protocolSessionManager);
			IRyuStatsClient client = getRyuProtocolSession(protocolSessionManager).getRyuClientForUse();
			RyuOFFlowListWrapper ryuFlows = client.getFlows(switchId);

			for (RyuOFFlow ryuOFFlow : ryuFlows) {

				OFFlow modelFlow = getForwardingRuleFromModel(ryuOFFlow);
				if (modelFlow == null) {
					ryuOFFlow.setName(UUID.randomUUID().toString());
					ryuOFFlow.setDpid(switchId);
					flows.add(ryuOFFlow);
				} else {
					flows.add(modelFlow);
				}

			}

		} catch (ProtocolException e) {
			log.error("Error obtaining Ryu protocol session.", e);
			throw new ActionException("Error obtaining ryu protocol session.", e);
		} catch (Exception e) {
			log.error("Error getting flows from Ryu.", e);
			throw new ActionException("Error getting flows from Ryu.", e);
		}

		ActionResponse actionResponse = new ActionResponse();
		actionResponse.setActionID(this.actionID);
		actionResponse.setStatus(STATUS.OK);
		actionResponse.setResult(flows);

		return actionResponse;

	}

	private OFFlow getForwardingRuleFromModel(RyuOFFlow ryuOFFlow) {
		for (OFFlow modelFlow : ((OpenflowSwitchModel) getModel()).getOfTables().get(0).getOfForwardingRules())
			if (ForwardingRuleUtils.rulesWithSameMatch(ryuOFFlow, modelFlow))
				return modelFlow;

		return null;
	}
}
