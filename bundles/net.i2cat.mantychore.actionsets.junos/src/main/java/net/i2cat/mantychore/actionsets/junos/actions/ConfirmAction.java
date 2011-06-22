package net.i2cat.mantychore.actionsets.junos.actions;

import net.i2cat.mantychore.commandsets.junos.commands.CommitNetconfCommand;
import net.i2cat.nexus.resources.action.ActionException;
import net.i2cat.nexus.resources.action.ActionResponse;
import net.i2cat.nexus.resources.protocol.IProtocolSession;
import net.i2cat.nexus.resources.queue.QueueConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
		} catch (Exception e) {
			throw new ActionException(this.actionID, e);
		}
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

	public void prepareMessage() throws ActionException {
		// TODO implement the preparation of the message
		// Check the template is there any
		// create the velocity message
	}

}