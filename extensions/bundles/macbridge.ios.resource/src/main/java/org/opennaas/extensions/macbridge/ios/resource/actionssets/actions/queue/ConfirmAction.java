package org.opennaas.extensions.macbridge.ios.resource.actionssets.actions.queue;

import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.queue.QueueConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfirmAction extends Action {

	static Log	log	= LogFactory.getLog(ConfirmAction.class);

	public ConfirmAction() {
		super();
		this.setActionID(QueueConstants.CONFIRM);

	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
		ActionResponse actionResponse = ActionResponse.okResponse(actionID);
		return actionResponse;
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		return true;
	}

}
