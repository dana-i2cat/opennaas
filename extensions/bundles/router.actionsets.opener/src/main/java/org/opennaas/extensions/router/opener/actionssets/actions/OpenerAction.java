package org.opennaas.extensions.router.opener.actionssets.actions;

import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.router.opener.client.rpc.SetInterfaceResponse;
import org.opennaas.extensions.router.opener.protocol.OpenerProtocolSession;

public abstract class OpenerAction extends Action {

	protected OpenerProtocolSession getOpenerProtocolSession(IProtocolSessionManager protocolSessionManager) throws ProtocolException {
		return (OpenerProtocolSession) protocolSessionManager.obtainSessionByProtocol(
				OpenerProtocolSession.OPENER_PROTOCOL_TYPE, false);
	}

	protected ActionResponse actionResposeFromSetInterfaceResponse(SetInterfaceResponse openerResponse) {

		ActionResponse actionResponse = new ActionResponse();
		STATUS actionStatus;

		if (openerResponse.getError() != null) {

			actionResponse.setStatus(STATUS.ERROR);
			actionResponse.setInformation(openerResponse.getError());

		} else {

			if (openerResponse.getResponse() != null || openerResponse.getResponse().isEmpty()) {
				if (Integer.parseInt(openerResponse.getResponse()) >= 200 && Integer.parseInt(openerResponse.getResponse()) < 300) {
					actionStatus = STATUS.OK;
				} else {
					actionStatus = STATUS.ERROR;
				}
			} else {
				actionStatus = STATUS.ERROR;
			}
			actionResponse.setStatus(actionStatus);
		}

		return actionResponse;
	}

}
