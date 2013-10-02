package org.opennaas.extensions.router.opener.actionssets.actions;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;

/**
 * An Action returning OK. It does nothing.
 * 
 * @author isart
 * 
 */
public class DummyAction extends OpenerAction {

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager)
			throws ActionException {

		ActionResponse response = new ActionResponse();
		response.setActionID("DUMMY");
		response.setStatus(ActionResponse.STATUS.OK);

		return response;
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		return true;
	}

}
