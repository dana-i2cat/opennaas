package org.opennaas.extensions.openflowswitch.driver.floodlight.actionssets.actions;

import java.util.List;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.openflowswitch.driver.floodlight.actionssets.FloodlightAction;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.IFloodlightStaticFlowPusherClient;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;

/**
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class GetOFForwardingAction extends FloodlightAction {

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
		List<FloodlightOFFlow> flows;

		try {
			// obtain the switch ID from the protocol session
			String switchId = getSwitchIdFromSession(protocolSessionManager);
			IFloodlightStaticFlowPusherClient client = getFloodlightProtocolSession(protocolSessionManager).getFloodlightClientForUse();
			flows = client.getFlows(switchId);

		} catch (ProtocolException e) {
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
