package org.opennaas.extensions.openflowswitch.driver.floodlight.actionssets.actions;

import java.util.List;

import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.openflowswitch.capability.OpenflowForwardingActionSet;
import org.opennaas.extensions.openflowswitch.helpers.OpenflowSwitchModelHelper;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.openflowswitch.model.OpenflowSwitchModel;

public class GetFlowsActionMockup extends Action {

	@Override
	public boolean checkParams(Object arg0) throws ActionException {
		return true;
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager arg0) throws ActionException {
		// return flows from model
		List<FloodlightOFFlow> flows = OpenflowSwitchModelHelper.getSwitchForwardingRules((OpenflowSwitchModel) getModelToUpdate());
		ActionResponse response = ActionResponse.okResponse(OpenflowForwardingActionSet.GETFLOWS);
		response.setResult(flows);
		return response;
	}

}
