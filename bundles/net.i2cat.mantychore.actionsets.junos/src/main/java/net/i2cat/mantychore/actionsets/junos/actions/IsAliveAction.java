package net.i2cat.mantychore.actionsets.junos.actions;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.commandsets.junos.commands.KeepAliveNetconfCommand;
import net.i2cat.nexus.resources.action.ActionException;
import net.i2cat.nexus.resources.action.ActionResponse;
import net.i2cat.nexus.resources.protocol.IProtocolSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IsAliveAction extends JunosAction {

	Log	log	= LogFactory.getLog(IsAliveAction.class);

	public IsAliveAction() {
		super();
		initialize();
	}

	protected void initialize() {
		this.setActionID(ActionConstants.ISALIVE);
		this.protocolName = "netconf";

	}

	@Override
	public void executeListCommand(ActionResponse actionResponse, IProtocolSession protocol) throws ActionException {
		try {
			KeepAliveNetconfCommand command = new KeepAliveNetconfCommand();
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

	@Override
	public void prepareMessage() throws ActionException {
		// TODO implements if is necessary if not delete

	}

}