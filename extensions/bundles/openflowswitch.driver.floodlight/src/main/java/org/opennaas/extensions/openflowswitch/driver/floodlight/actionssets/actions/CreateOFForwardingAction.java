package org.opennaas.extensions.openflowswitch.driver.floodlight.actionssets.actions;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.openflowswitch.driver.floodlight.actionssets.FloodlightAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;

public class CreateOFForwardingAction extends FloodlightAction {

	private static final String	FORWARDING_ACTION	= "output";

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {

		if (params == null || !(params instanceof FloodlightOFFlow))
			throw new ActionException("Invalid parameters for action " + this.actionID);

		FloodlightOFFlow flowRule = (FloodlightOFFlow) params;

		if (flowRule.getName().isEmpty())
			throw new ActionException("No flow id given to params in action " + this.actionID);

		for (FloodlightOFAction action : flowRule.getActions()) {

			if (action.getType().isEmpty())
				throw new ActionException("No OFAction type given to params in action " + this.actionID);

			if (action.getValue().isEmpty())
				throw new ActionException("No OFAction value given to params in action " + this.actionID);

			if (!(action.getType().equals(FORWARDING_ACTION)))
				throw new ActionException(
						"Wrong action type given to params in " + this.actionID + ". Expected was \"" + FORWARDING_ACTION + "\", but \"" + action
								.getType() + "\" was given.");

		}

		return true;

	}
}
