package net.i2cat.luminis.actionsets.wonesys.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.command.Response.Status;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.queue.QueueConstants;

public class RestoreAction extends Action {

	static Log	log	= LogFactory.getLog(RestoreAction.class);

	public RestoreAction() {
		super();
		initialize();

	}

	protected void initialize() {
		this.setActionID(QueueConstants.RESTORE);
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {

		// unlock node
		UnlockNodeAction unlockAction = new UnlockNodeAction();
		ActionResponse unlockResponse = unlockAction.execute(protocolSessionManager);

		// TODO stop timer refreshing lock periodically

		// TODO EXEC ROLLBACK

		ActionResponse actionResponse = new ActionResponse();
		actionResponse.setActionID(this.actionID);
		actionResponse.addResponse(unlockResponse.getResponses().get(0));
		actionResponse.setStatus(STATUS.ERROR);
		actionResponse.setInformation("Failed to rollback to a state previous to queue execution. Rollback mechanism is not supported.");

		return actionResponse;
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		log.warn("Given params are ignored");
		return true;
	}

}
