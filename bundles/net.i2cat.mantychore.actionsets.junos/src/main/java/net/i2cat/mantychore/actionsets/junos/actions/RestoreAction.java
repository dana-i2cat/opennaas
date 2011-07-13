package net.i2cat.mantychore.actionsets.junos.actions;

import net.i2cat.mantychore.commandsets.junos.commands.DiscardNetconfCommand;
import net.i2cat.nexus.resources.action.ActionException;
import net.i2cat.nexus.resources.action.ActionResponse;
import net.i2cat.nexus.resources.command.Response;
import net.i2cat.nexus.resources.protocol.IProtocolSession;
import net.i2cat.nexus.resources.queue.QueueConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RestoreAction extends JunosAction {
	Log	log	= LogFactory.getLog(RestoreAction.class);

	public RestoreAction() {
		super();
		initialize();

	}

	@Override
	public void executeListCommand(ActionResponse actionResponse, IProtocolSession protocol) throws ActionException {
		try {
			/* discard incorrect changes */
			DiscardNetconfCommand discardCommand = new DiscardNetconfCommand();
			discardCommand.initialize();
			Response response = sendCommandToProtocol(discardCommand, protocol);
			actionResponse.addResponse(response);

		} catch (Exception e) {
			throw new ActionException(this.actionID, e);
		}
		validateAction(actionResponse);
	}

	@Override
	public void parseResponse(Object responseMessage, Object model) {
		// TODO Auto-generated method stub

	}

	protected void initialize() {
		this.actionID = QueueConstants.RESTORE;
		this.protocolName = "netconf";

	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		// For this Action params are not allowed
		if (params == null)
			return true;
		else {
			throw new ActionException("The Action " + getActionID() + " don't accept params");
		}
	}

	@Override
	public void prepareMessage() throws ActionException {
		// TODO implements if is necessary if not delete
	}

}
