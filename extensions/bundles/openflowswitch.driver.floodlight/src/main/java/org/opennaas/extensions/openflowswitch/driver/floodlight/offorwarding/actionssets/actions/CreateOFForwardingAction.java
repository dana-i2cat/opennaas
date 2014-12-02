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
import org.opennaas.extensions.openflowswitch.driver.floodlight.offorwarding.actionssets.FloodlightConstants;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.IFloodlightStaticFlowPusherClient;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.openflowswitch.model.OFFlow;
import org.opennaas.extensions.openflowswitch.model.OpenflowSwitchModel;

/**
 * 
 * @author Adrian Rosello (i2CAT)
 * 
 */
public class CreateOFForwardingAction extends FloodlightAction {

	public static final String	FORWARDING_ACTION	= "output";

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {

		try {
			// TODO we have to find another place where we could put switchId in model.
			setSwitchIdInModel(protocolSessionManager);

			FloodlightOFFlow flow;

			String switchId = ((OpenflowSwitchModel) getModelToUpdate()).getSwitchId();

			if (params instanceof FloodlightOFFlow) {
				flow = (FloodlightOFFlow) params;
				flow.setSwitchId(switchId);
			}
			else
				flow = new FloodlightOFFlow((OFFlow) params, switchId);

			flow = updateFlowWithControllerRequiredValues(flow);

			IFloodlightStaticFlowPusherClient client = getFloodlightProtocolSession(protocolSessionManager).getFloodlightClientForUse();
			client.addFlow(flow);

		} catch (Exception e) {
			throw new ActionException(e);
		}

		return ActionResponse.okResponse(OpenflowForwardingActionSet.CREATEOFFORWARDINGRULE);
	}

	private FloodlightOFFlow updateFlowWithControllerRequiredValues(FloodlightOFFlow flow) {
		if (flow.getPriority() == null || flow.getPriority().isEmpty()) {
			flow.setPriority(FloodlightConstants.DEFAULT_PRIORITY);
		}

		if (flow.getMatch() != null) {
			// if (flow.getMatch().getSrcIp() != null || flow.getMatch().getDstIp() != null || flow.getMatch().getTosBits() != null) {
			if ((flow.getMatch().getEtherType() != null) && (!flow.getMatch().getEtherType().equals("2054")) && (flow.getMatch().getSrcIp() != null || flow
					.getMatch().getDstIp() != null || flow.getMatch().getTosBits() != null)) {
				// To avoid following message in floodlight controller:
				// Warning! Pushing a static flow entry that matches IP fields without matching for IP payload (ether-type 2048)
				// will cause the switch to wildcard higher level fields.
				flow.getMatch().setEtherType("2048");
			}
			else if ((flow.getMatch().getEtherType() == null) && (flow.getMatch().getSrcIp() != null || flow.getMatch().getDstIp() != null || flow
					.getMatch().getTosBits() != null))
				flow.getMatch().setEtherType("2048");

		}

		return flow;
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
			int max = Integer.parseInt(FloodlightConstants.MAX_PRIORITY);
			int min = Integer.parseInt(FloodlightConstants.MIN_PRIORITY);
			if (priority > max || priority < min) {
				throw new ActionException("Invalid priority in action " + this.actionID + ". Valid range is [" + min + "," + max + "]");
			}
		}

		return true;

	}

	private void setSwitchIdToFlow() {

		OpenflowSwitchModel model = (OpenflowSwitchModel) getModelToUpdate();
		FloodlightOFFlow flowRule = (FloodlightOFFlow) params;

		flowRule.setSwitchId(model.getSwitchId());
	}

	private void setSwitchIdInModel(IProtocolSessionManager protocolSessionManager) throws ProtocolException {
		OpenflowSwitchModel model = (OpenflowSwitchModel) getModelToUpdate();
		model.setSwitchId(getSwitchIdFromSession(protocolSessionManager));
	}

}
