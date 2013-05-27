package org.opennaas.itests.core.resources;

import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DummyAction extends Action {

	static Log	log	= LogFactory.getLog(DummyAction.class);

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {

		log.info("Executing DUMMY ACTION");
		ActionResponse actionResponse = new ActionResponse();
		actionResponse.setActionID(getActionID());

		Response response = Response.okResponse("DUMMY");
		actionResponse.addResponse(response);

		return actionResponse;
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		return true;
	}

}
