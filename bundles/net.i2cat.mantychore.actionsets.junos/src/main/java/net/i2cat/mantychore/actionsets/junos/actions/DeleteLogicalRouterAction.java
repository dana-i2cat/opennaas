package net.i2cat.mantychore.actionsets.junos.actions;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.commandsets.junos.commands.EditNetconfCommand;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSession;

public class DeleteLogicalRouterAction extends JunosAction {
	public DeleteLogicalRouterAction() {
		super();
		initialize();
	}

	protected void initialize() {
		this.setActionID(ActionConstants.DELETELOGICALROUTER);
		setTemplate("/VM_files/deleteLogicalRouter.vm");
		this.protocolName = "netconf";
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		return false;
	}

	@Override
	public void executeListCommand(ActionResponse actionResponse, IProtocolSession protocol) throws ActionException {

		try {
			EditNetconfCommand command = new EditNetconfCommand(getVelocityMessage());
			command.initialize();
			actionResponse.addResponse(sendCommandToProtocol(command, protocol));
		} catch (Exception e) {
			throw new ActionException(this.actionID + ": " + e.getMessage(), e);
		}
		validateAction(actionResponse);
	}

	@Override
	public void parseResponse(Object responseMessage, Object model) throws ActionException {
	}

	@Override
	public void prepareMessage() throws ActionException {

		if (template == null || template.equals(""))
			throw new ActionException("The path to Velocity template in Action " + getActionID() + " is null");
		try {
			setVelocityMessage(prepareVelocityCommand(params, template));
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

}
