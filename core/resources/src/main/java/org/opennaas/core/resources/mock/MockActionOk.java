package org.opennaas.core.resources.mock;

import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;

public class MockActionOk extends Action {

	public MockActionOk() {
		actionID = "MockAtionOk";
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager)
			throws ActionException {

		return ActionResponse.okResponse("MockAtionOk");
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		return true;
	}

}
