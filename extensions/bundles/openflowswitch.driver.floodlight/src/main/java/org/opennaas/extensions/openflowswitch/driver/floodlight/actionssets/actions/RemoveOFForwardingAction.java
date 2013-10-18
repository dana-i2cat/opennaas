package org.opennaas.extensions.openflowswitch.driver.floodlight.actionssets.actions;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.openflowswitch.capability.OpenflowForwardingActionSet;
import org.opennaas.extensions.openflowswitch.driver.floodlight.actionssets.FloodlightAction;
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
	 * @throws ProtocolException
	 */
	private FloodlightOFFlow getFlowFromSwitchByName(String flowName, String switchId, IFloodlightStaticFlowPusherClient client)
			throws ProtocolException, Exception {
		for (FloodlightOFFlow flow : client.getFlows(switchId)) {
			if (flow.getName().equals(flowName))
				return flow;
		}
		throw new ActionException("Given flow does not exist: " + flowName);
	}
}
