package org.opennaas.core.resources.mock;

import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;

public class MockDummyAction extends Action {

	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
		ActionResponse actionResponse = new ActionResponse();
		actionResponse.setActionID(getActionID());

		Response response = Response.okResponse("DUMMY");
		actionResponse.addResponse(response);

		return actionResponse;
	}

	public boolean checkParams(Object params) throws ActionException {
		return true;
	}

}
