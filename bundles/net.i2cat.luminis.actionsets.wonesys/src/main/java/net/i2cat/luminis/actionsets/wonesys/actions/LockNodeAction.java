package net.i2cat.luminis.actionsets.wonesys.actions;

import net.i2cat.luminis.actionsets.wonesys.ActionConstants;
import net.i2cat.luminis.commandsets.wonesys.WonesysCommand;
import net.i2cat.luminis.commandsets.wonesys.commands.GetInventoryCommand;
import net.i2cat.luminis.commandsets.wonesys.commands.LockNodeCommand;
import net.i2cat.luminis.protocols.wonesys.WonesysProtocolSession;
import net.i2cat.nexus.resources.action.Action;
import net.i2cat.nexus.resources.action.ActionException;
import net.i2cat.nexus.resources.action.ActionResponse;
import net.i2cat.nexus.resources.command.Response;
import net.i2cat.nexus.resources.protocol.IProtocolSessionManager;
import net.i2cat.nexus.resources.protocol.ProtocolException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LockNodeAction extends WonesysAction {

	static Log	log	= LogFactory.getLog(LockNodeAction.class);

	public LockNodeAction() {
		super();
		initialize();
	}

	protected void initialize() {
		this.setActionID(ActionConstants.LOCKNODE);
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {

		try {
			/* get protocol */
			WonesysProtocolSession protocol = (WonesysProtocolSession) protocolSessionManager.obtainSessionByProtocol("wonesys", false);

			WonesysCommand c = new LockNodeCommand();
			c.initialize();
			String response = (String) protocol.sendReceive(c.message());
			Response resp = checkResponse(c.message(), response);

			ActionResponse actionResponse = ActionResponse.okResponse(actionID);
			actionResponse.addResponse(resp);
			updateStatusFromResponses(actionResponse);

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
