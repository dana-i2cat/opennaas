package org.opennaas.extensions.openflowswitch.driver.ryu.offorwarding.actionssets.actions;

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
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.OFFlow;
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
		try {
			// TODO we have to find another place where we could put switchId in model.
			setSwitchIdInModel(protocolSessionManager);

			IRyuStatsClient client = getRyuProtocolSession(protocolSessionManager).getRyuClientForUse();
			client.addFlowEntry((RyuOFFlow) params);

		} catch (Exception e) {
			log.error("Error pushig forwarding rule to Ryu. ", e);
			throw new ActionException(e);

		}

		ActionResponse response = new ActionResponse();
		response.setStatus(STATUS.OK);

		return response;
	}

	private void setSwitchIdInModel(IProtocolSessionManager protocolSessionManager) throws ProtocolException {
		OpenflowSwitchModel model = (OpenflowSwitchModel) getModelToUpdate();
		model.setSwitchId(getSwitchIdFromSession(protocolSessionManager));
	}
}
