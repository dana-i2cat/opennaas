package net.i2cat.mantychore.actionsets.junos.actions;

import net.i2cat.mantychore.commandsets.junos.commands.DiscardNetconfCommand;
import net.i2cat.nexus.resources.action.ActionException;
import net.i2cat.nexus.resources.action.ActionResponse;
import net.i2cat.nexus.resources.command.Response;
import net.i2cat.nexus.resources.protocol.IProtocolSession;
import net.i2cat.nexus.resources.queue.QueueConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PrepareAction extends JunosAction {
	Log	log	= LogFactory.getLog(PrepareAction.class);

	public PrepareAction() {
		super();
		initialize();
	}

	protected void initialize() {
		this.setActionID(QueueConstants.PREPARE);
		setTemplate("/VM_files/getconfiguration.vm");
		this.protocolName = "netconf";

	}

	@Override
	public void executeListCommand(ActionResponse actionResponse, IProtocolSession protocol) throws ActionException {
		try {
			/* discard changes */
			DiscardNetconfCommand discardCommand = new DiscardNetconfCommand();
			discardCommand.initialize();
			Response response = sendCommandToProtocol(discardCommand, protocol);
			actionResponse.addResponse(response);

			/* it can't be executed this workflow */
			// /* get config */
			// GetNetconfCommand getcommand = new GetNetconfCommand(getVelocityMessage());
			// getcommand.initialize();
			// response = sendCommandToProtocol(getcommand, protocol);
			// actionResponse.addResponse(response);
			//
			// /* store config */
			// TempFileManager.getInstance().createFile("backup.xml", response.getInformation());

		} catch (Exception e) {
			throw new ActionException(this.actionID, e);
		}
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

	public void prepareMessage() throws ActionException {

	}

}
