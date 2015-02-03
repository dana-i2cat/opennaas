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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.OpenflowForwardingActionSet;
import org.opennaas.extensions.openflowswitch.driver.ryu.offorwarding.actionssets.RyuAction;
import org.opennaas.extensions.openflowswitch.driver.ryu.offorwarding.actionssets.RyuConstants;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.IRyuStatsClient;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.model.RyuOFFlow;
import org.opennaas.extensions.openflowswitch.helpers.ForwardingRuleUtils;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;
import org.opennaas.extensions.openflowswitch.model.OFFlow;
import org.opennaas.extensions.openflowswitch.model.OFFlowTable;
import org.opennaas.extensions.openflowswitch.model.OpenflowSwitchModel;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class CreateOFForwardingAction extends RyuAction {

	private Log					log					= LogFactory.getLog(CreateOFForwardingAction.class);

	public static final String	FORWARDING_ACTION	= "output";

	public CreateOFForwardingAction() {
		this.actionID = OpenflowForwardingActionSet.CREATEOFFORWARDINGRULE;
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (params == null || !(params instanceof OFFlow))
			throw new ActionException("Invalid parameters for action " + this.actionID);

		OFFlow flowRule = (OFFlow) params;

		if (flowRule.getName() == null || flowRule.getName().isEmpty())
			throw new ActionException("No flow id given to params in action " + this.actionID);

		for (FloodlightOFAction action : flowRule.getActions()) {

			if (action.getType() == null || action.getType().isEmpty())
				throw new ActionException("No OFAction type given to params in action " + this.actionID);

			if (action.getValue() == null || action.getValue().isEmpty())
				throw new ActionException("No OFAction value given to params in action " + this.actionID);

			if (!(action.getType().equals(FORWARDING_ACTION)))
				throw new ActionException(
						"Wrong action type given to params in " + this.actionID + ". Expected was \"" + FORWARDING_ACTION + "\", but \"" + action
								.getType() + "\" was given.");

		}

		// if flowRule has priority
		if (flowRule.getPriority() != null && !flowRule.getPriority().isEmpty()) {
			// check priority is a number
			int priority;
			try {
				priority = Integer.parseInt(flowRule.getPriority());
			} catch (NumberFormatException e) {
				throw new ActionException("Invalid priority in action " + this.actionID, e);
			}

			// check priority is in valid range
			int max = Integer.parseInt(RyuConstants.PRIORITY_MAX);
			int min = Integer.parseInt(RyuConstants.PRIORITY_MIN);
			if (priority > max || priority < min) {
				throw new ActionException("Invalid priority in action " + this.actionID + ". Valid range is [" + min + "," + max + "]");
			}
		}

		return true;
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {

		updateFlowWithControllerRequiredValues((OFFlow) params);

		// FIXME we assume forwarding rules are set in table 0
		OFFlowTable flowTable = ((OpenflowSwitchModel) getModel()).getOfTables().get(0);
		List<OFFlow> forwardingRules = flowTable.getOfForwardingRules();
		for (OFFlow ofFlow : forwardingRules) {
			compareForwardingRuleIsUnique(ofFlow, (OFFlow) params);
			if (ofFlow.getName().equals(((OFFlow) params).getName())) {
				log.debug("Removing existing flow with same id : " + ofFlow.getName());
				RemoveOFForwardingAction action = new RemoveOFForwardingAction();
				action.setParams(ofFlow);
				action.execute(protocolSessionManager);
			}

		}

		try {
			RyuOFFlow flowToPush;

			if (params instanceof RyuOFFlow) {
				flowToPush = new RyuOFFlow((RyuOFFlow) params);
				flowToPush.setDpid(getSwitchIdFromSession(protocolSessionManager));
			}
			else
				flowToPush = new RyuOFFlow((OFFlow) params, getSwitchIdFromSession(protocolSessionManager));

			// TODO we have to find another place where we could put switchId in model.
			setSwitchIdInModel(protocolSessionManager);

			IRyuStatsClient client = getRyuProtocolSession(protocolSessionManager).getRyuClientForUse();

			log.debug("Sending forwarding rule to Ryu controller.");
			client.addFlowEntry(flowToPush);

			((OpenflowSwitchModel) getModel()).getOfTables().get(0).getOfForwardingRules().add(flowToPush);

		} catch (Exception e) {
			log.error("Error pushing forwarding rule to Ryu. ", e);
			throw new ActionException(e);

		}

		ActionResponse response = new ActionResponse();
		response.setStatus(STATUS.OK);

		return response;
	}

	private void updateFlowWithControllerRequiredValues(OFFlow flowToPush) {
		FloodlightOFMatch match = flowToPush.getMatch();
		if (match != null)
			if (!StringUtils.isEmpty(match.getSrcIp()) || !StringUtils.isEmpty(match.getDstIp()) || !StringUtils.isEmpty(match.getTosBits()))
				match.setEtherType("2048");

	}

	private void setSwitchIdInModel(IProtocolSessionManager protocolSessionManager) throws ProtocolException {
		OpenflowSwitchModel model = (OpenflowSwitchModel) getModel();
		model.setSwitchId(getSwitchIdFromSession(protocolSessionManager));
	}

	private void compareForwardingRuleIsUnique(OFFlow firstFlow, OFFlow secondFlow) throws ActionException {
		if (ForwardingRuleUtils.rulesWithSameMatch(firstFlow, secondFlow))
			throw new ActionException("A forwarding rule with same information already exists in flow model.");
	}

}
