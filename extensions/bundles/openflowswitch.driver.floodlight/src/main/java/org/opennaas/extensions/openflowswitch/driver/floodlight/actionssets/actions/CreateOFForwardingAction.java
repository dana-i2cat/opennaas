package org.opennaas.extensions.openflowswitch.driver.floodlight.actionssets.actions;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.openflowswitch.driver.floodlight.actionssets.FloodlightAction;

public class CreateOFForwardingAction extends FloodlightAction {

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		return false;

	}

}
