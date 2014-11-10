package org.opennaas.extensions.openflowswitch.driver.ryu.offorwarding.actionssets.actions;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.OpenflowForwardingActionSet;
import org.opennaas.extensions.openflowswitch.driver.ryu.offorwarding.actionssets.RyuAction;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.IRyuStatsClient;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.model.RyuOFFlow;

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
		String switchId;
		try {
			client = getRyuProtocolSession(protocolSessionManager).getRyuClientForUse();
			switchId = getSwitchIdFromSession(protocolSessionManager);

			RyuOFFlow flow = getFlowByName((String) params, switchId, client);

			client.deleteFlowEntryStrictly(flow);

		} catch (Exception e) {
			log.error("Error removing forwarding rule " + params, e);
			throw new ActionException(e);
		}

		ActionResponse response = new ActionResponse();
		response.setStatus(STATUS.OK);

		return response;
	}

	private RyuOFFlow getFlowByName(String flowName, String switchId, IRyuStatsClient client) throws ProtocolException, Exception {

		for (RyuOFFlow flow : client.getFlows(switchId)) {
			if (flow.getName().equals(flowName))
				return flow;
		}

		throw new ActionException("There's no forwarding rule with id " + switchId);
	}
}
