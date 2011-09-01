package net.i2cat.luminis.actionsets.wonesys.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.i2cat.nexus.resources.action.Action;
import net.i2cat.nexus.resources.action.ActionException;
import net.i2cat.nexus.resources.action.ActionResponse;
import net.i2cat.nexus.resources.command.Response;
import net.i2cat.nexus.resources.protocol.IProtocolSessionManager;
import net.i2cat.nexus.resources.queue.QueueConstants;

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
