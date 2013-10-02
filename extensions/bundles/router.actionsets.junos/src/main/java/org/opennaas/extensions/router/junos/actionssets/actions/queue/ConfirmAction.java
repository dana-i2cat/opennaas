package org.opennaas.extensions.router.junos.actionssets.actions.queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.queue.QueueConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.JunosAction;
import org.opennaas.extensions.router.junos.commandsets.commands.CommitNetconfCommand;
import org.opennaas.extensions.router.junos.commandsets.commands.UnlockNetconfCommand;

public class ConfirmAction extends JunosAction {

	Log	logger	= LogFactory.getLog(ConfirmAction.class);

	public ConfirmAction() {
		super();
		initialize();
	}

	private void initialize() {
		this.setActionID(QueueConstants.CONFIRM);
		this.protocolName = "netconf";

	}

	@Override
	public void executeListCommand(ActionResponse actionResponse, IProtocolSession protocol) throws ActionException {
		try {
			CommitNetconfCommand command = new CommitNetconfCommand();
			command.initialize();
			actionResponse.addResponse(sendCommandToProtocol(command, protocol));

			// TODO test unlock command
			UnlockNetconfCommand unlockCommand = new UnlockNetconfCommand("candidate");
			unlockCommand.initialize();
			Response responseUnlock = sendCommandToProtocol(unlockCommand, protocol);
			actionResponse.addResponse(responseUnlock);

		} catch (Exception e) {
			throw new ActionException(this.actionID + "\n" + e.getMessage());
		}
		validateAction(actionResponse);

	}

	@Override
	public void parseResponse(Object responseMessage, Object model) {
		// TODO Auto-generated method stub

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
		// TODO implement the preparation of the message
		// Check the template is there
		// create the velocity message
	}

}