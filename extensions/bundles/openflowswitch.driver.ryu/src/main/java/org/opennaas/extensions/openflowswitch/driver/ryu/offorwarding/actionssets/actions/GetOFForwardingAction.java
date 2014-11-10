package org.opennaas.extensions.openflowswitch.driver.ryu.offorwarding.actionssets.actions;

import java.util.ArrayList;
import java.util.List;

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
import org.opennaas.extensions.openflowswitch.model.OFFlow;

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

			for (RyuOFFlow ryuOFFlow : ryuFlows)
				flows.add(new OFFlow(ryuOFFlow));

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

}
