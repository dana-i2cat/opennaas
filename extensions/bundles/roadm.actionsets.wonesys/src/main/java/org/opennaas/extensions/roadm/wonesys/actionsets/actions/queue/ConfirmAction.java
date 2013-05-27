package org.opennaas.extensions.roadm.wonesys.actionsets.actions.queue;

import org.opennaas.extensions.roadm.wonesys.actionsets.actions.WonesysAction;
import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.extensions.roadm.wonesys.commandsets.commands.UnlockNodeCommand;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.QueueConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfirmAction extends WonesysAction {

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

		try {
			/* get protocol */
			IProtocolSession protocol = protocolSessionManager.obtainSessionByProtocol("wonesys", false);

			WonesysCommand c = new UnlockNodeCommand();
			c.initialize();
			String response = (String) protocol.sendReceive(c.message());
			Response resp = checkResponse(c.message(), response);


			ActionResponse actionResponse = ActionResponse.okResponse(actionID);
			actionResponse.addResponse(resp);
			//Even if command fails, ActionResponse is OK (command may fail because of lock timeout)
			//updateStatusFromResponses(actionResponse);

			return actionResponse;

		} catch (ProtocolException e) {
			throw new ActionException(e);
		}
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		log.warn("Given params are ignored");
		return true;
	}

}
