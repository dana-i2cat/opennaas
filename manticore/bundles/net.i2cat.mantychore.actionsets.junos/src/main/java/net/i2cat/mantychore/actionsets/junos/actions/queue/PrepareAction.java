package net.i2cat.mantychore.actionsets.junos.actions.queue;

import net.i2cat.mantychore.actionsets.junos.actions.JunosAction;
import net.i2cat.mantychore.commandsets.junos.commands.DiscardNetconfCommand;
import net.i2cat.mantychore.commandsets.junos.commands.LockNetconfCommand;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.queue.QueueConstants;

public class PrepareAction extends JunosAction {
	Log	log	= LogFactory.getLog(PrepareAction.class);

	public PrepareAction() {
		super();
		initialize();
	}

	protected void initialize() {
		this.setActionID(QueueConstants.PREPARE);
		// setTemplate("/VM_files/getconfiguration.vm");
		this.protocolName = "netconf";

	}

	@Override
	public void executeListCommand(ActionResponse actionResponse, IProtocolSession protocol) throws ActionException {
		try {
			/* lock commnad */



			/* discard changes */
			DiscardNetconfCommand discardCommand = new DiscardNetconfCommand();
			discardCommand.initialize();
			Response responsePrepare = sendCommandToProtocol(discardCommand, protocol);
			actionResponse.addResponse(responsePrepare);

			LockNetconfCommand lockCommand = new LockNetconfCommand("candidate");
			lockCommand.initialize();
			Response responseLock = sendCommandToProtocol(lockCommand, protocol);
			actionResponse.addResponse(responseLock);

			/* it can't be executed this workflow */
		} catch (Exception e) {
			throw new ActionException(this.actionID, e);
		}
		validateAction(actionResponse);
	}

	@Override
	public void parseResponse(Object responseMessage, Object model) {

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

	}

}
