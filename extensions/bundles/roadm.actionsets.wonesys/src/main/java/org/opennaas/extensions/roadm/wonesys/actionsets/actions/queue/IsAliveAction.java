package org.opennaas.extensions.roadm.wonesys.actionsets.actions.queue;

import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.queue.QueueConstants;

public class IsAliveAction extends Action {

	public IsAliveAction() {
		super();
		initialize();

	}

	protected void initialize() {
		this.setActionID(QueueConstants.ISALIVE);
	}

	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
		ActionResponse actionResponse = new ActionResponse();
		actionResponse.setActionID(this.actionID);
		actionResponse.addResponse(Response.okResponse("Correct! i did nothing "));
		actionResponse.setStatus(STATUS.OK);
		return actionResponse;
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		// TODO Auto-generated method stub
		return false;
	}

}
