package net.i2cat.luminis.actionsets.wonesys.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.queue.QueueConstants;

public class ConfirmAction extends Action {

	static Log	log	= LogFactory.getLog(ConfirmAction.class);

	public ConfirmAction() {
		super();
		initialize();

	}

	protected void initialize() {
		this.setActionID(QueueConstants.CONFIRM);
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {

		// unlock node
		UnlockNodeAction unlockAction = new UnlockNodeAction();
		ActionResponse unlockResponse = unlockAction.execute(protocolSessionManager);

		// TODO stop timer refreshing lock periodically

		// ActionResponse actionResponse = new ActionResponse();
		// actionResponse.setActionID(this.actionID);
		// actionResponse.addResponse(Response.okResponse("Correct! i did nothing "));
		// actionResponse.setStatus(Response.Status.OK);

		ActionResponse actionResponse = unlockResponse;

		return actionResponse;
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		log.warn("Given params are ignored");
		return true;
	}

}
