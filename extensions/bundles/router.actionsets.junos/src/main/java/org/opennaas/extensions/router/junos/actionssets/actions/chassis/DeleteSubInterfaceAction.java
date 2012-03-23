package org.opennaas.extensions.router.junos.actionssets.actions.chassis;

import static com.google.common.base.Strings.nullToEmpty;

import java.util.HashMap;
import java.util.Map;

import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.JunosAction;
import org.opennaas.extensions.router.junos.commandsets.commands.EditNetconfCommand;
import org.opennaas.extensions.router.model.ComputerSystem;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSession;

public class DeleteSubInterfaceAction extends JunosAction {

	public DeleteSubInterfaceAction() {
		super();
		initialize();
	}

	protected void initialize() {
		this.setActionID(ActionConstants.DELETESUBINTERFACE);
		setTemplate("/VM_files/deletesubinterface.vm");
		this.protocolName = "netconf";
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		// TODO Auto-generated method stub

		// check that there are params
		// the param must be an EthernetPort element
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
		// TODO Auto-generated method stub

	}

	@Override
	public void prepareMessage() throws ActionException {

		if (template == null || template.equals(""))
			throw new ActionException("The path to Velocity template in Action " + getActionID() + " is null");

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("elementName", nullToEmpty(((ComputerSystem) getModelToUpdate()).getElementName()));

		try {
			setVelocityMessage(prepareVelocityCommand(params, template, extraParams));
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

}
