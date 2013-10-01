package org.opennaas.extensions.openflowswitch.driver.floodlight.actionssets.actions;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.openflowswitch.driver.floodlight.actionssets.FloodlightAction;
import org.opennaas.extensions.openflowswitch.driver.floodlight.actionssets.FloodlightConstants;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.FloodlightProtocolSession;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.IFloodlightStaticFlowPusherClient;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.openflowswitch.model.OFFlowTable;
import org.opennaas.extensions.openflowswitch.model.OpenflowSwitchModel;

/**
 * 
 * @author Adrian Rosello (i2CAT)
 * 
 */
public class CreateOFForwardingAction extends FloodlightAction {

	private static final String	FORWARDING_ACTION	= "output";

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {

		try {
			// TODO we have to find another place where we could put switchId in model.
			setSwitchIdInModel(protocolSessionManager);

			setSwitchIdToFlow();

			FloodlightOFFlow flow = (FloodlightOFFlow) params;

			flow = updateFlowWithControllerRequiredValues(flow);

			IFloodlightStaticFlowPusherClient client = getFloodlightProtocolSession(protocolSessionManager).getFloodlightClientForUse();
			client.addFlow(flow);

			populateModelWithNewFlow(flow);

		} catch (ProtocolException e) {
			throw new ActionException(e);
		}

		ActionResponse response = new ActionResponse();
		response.setStatus(ActionResponse.STATUS.OK);

		return response;
	}

	private FloodlightOFFlow updateFlowWithControllerRequiredValues(FloodlightOFFlow flow) {
		if (flow.getPriority() == null || flow.getPriority().isEmpty()) {
			flow.setPriority(FloodlightConstants.DEFAULT_PRIORITY);
		}

		if (flow.getMatch() != null) {
			if (flow.getMatch().getSrcIp() != null || flow.getMatch().getDstIp() != null || flow.getMatch().getTosBits() != null) {
				// To avoid following message in floodlight controller:
				// Warning! Pushing a static flow entry that matches IP fields without matching for IP payload (ether-type 2048)
				// will cause the switch to wildcard higher level fields.
				flow.getMatch().setEtherType("2048");
			}
		}

		return flow;
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {

		if (params == null || !(params instanceof FloodlightOFFlow))
			throw new ActionException("Invalid parameters for action " + this.actionID);

		FloodlightOFFlow flowRule = (FloodlightOFFlow) params;

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

		return true;

	}

	private void setSwitchIdToFlow() {

		OpenflowSwitchModel model = (OpenflowSwitchModel) getModelToUpdate();
		FloodlightOFFlow flowRule = (FloodlightOFFlow) params;

		flowRule.setSwitchId(model.getSwitchId());
	}

	private void populateModelWithNewFlow(FloodlightOFFlow flow) {

		OpenflowSwitchModel model = (OpenflowSwitchModel) getModelToUpdate();
		OFFlowTable table = model.getOfTables().get(0);

		table.getOfForwardingRules().add(flow);

	}

	private void setSwitchIdInModel(IProtocolSessionManager protocolSessionManager) throws ProtocolException {

		ProtocolSessionContext sessionContext = getFloodlightProtocolSession(protocolSessionManager).getSessionContext();

		String switchId = (String) sessionContext.getSessionParameters().get(FloodlightProtocolSession.SWITCHID_CONTEXT_PARAM_NAME);

		OpenflowSwitchModel model = (OpenflowSwitchModel) getModelToUpdate();
		model.setSwitchId(switchId);

	}

}
