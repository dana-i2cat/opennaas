package org.opennaas.extensions.bod.actionsets.dummy.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.extensions.bod.actionsets.dummy.ActionConstants;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;

public class RequestConnectionAction extends Action {

	static Log	log	= LogFactory.getLog(ShutDownConnectionAction.class);

	public RequestConnectionAction() {
		super();
		initialize();
	}

	protected void initialize() {
		this.setActionID(ActionConstants.REQUESTCONNECTION);
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager sessionManager) throws ActionException {

		ActionResponse actionResponse = new ActionResponse();
		actionResponse.setActionID(this.actionID);
		actionResponse.addResponse(Response.okResponse("Correct! I do nothing"));
		actionResponse.setStatus(STATUS.OK);
		return actionResponse;
	}

	@Override
	public boolean checkParams(Object arg0) throws ActionException {
		return true;
	}

}