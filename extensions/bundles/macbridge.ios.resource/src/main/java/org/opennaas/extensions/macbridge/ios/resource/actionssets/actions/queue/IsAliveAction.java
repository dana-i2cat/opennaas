package org.opennaas.extensions.macbridge.ios.resource.actionssets.actions.queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.queue.QueueConstants;

public class IsAliveAction extends Action {

	static Log	log	= LogFactory.getLog(IsAliveAction.class);

	public IsAliveAction() {
		super();
		this.setActionID(QueueConstants.ISALIVE);

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
