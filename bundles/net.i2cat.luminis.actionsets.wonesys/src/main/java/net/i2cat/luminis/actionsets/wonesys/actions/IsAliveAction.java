package net.i2cat.luminis.actionsets.wonesys.actions;

import net.i2cat.nexus.resources.action.Action;
import net.i2cat.nexus.resources.action.ActionException;
import net.i2cat.nexus.resources.action.ActionResponse;
import net.i2cat.nexus.resources.action.ActionResponse.STATUS;
import net.i2cat.nexus.resources.command.Response;
import net.i2cat.nexus.resources.protocol.IProtocolSessionManager;
import net.i2cat.nexus.resources.queue.QueueConstants;

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
