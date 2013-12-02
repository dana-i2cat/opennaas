package org.opennaas.extensions.sdnnetwork.driver.internal.actionsets.actions;

import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.sdnnetwork.capability.ofprovision.OFProvisioningNetworkActionSet;
import org.opennaas.extensions.sdnnetwork.model.SDNNetworkModel;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * @author Julio Carlos Barrera
 * 
 */
public class GetAllocatedFlowsAction extends Action {

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager)
			throws ActionException {
		ActionResponse response = ActionResponse.okResponse(OFProvisioningNetworkActionSet.GETALLOCATEDFLOWS);
		response.setResult(((SDNNetworkModel) getModelToUpdate()).getFlows());
		return response;
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		// no params needed
		return true;
	}

}
