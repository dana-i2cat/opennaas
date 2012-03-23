package org.opennaas.extensions.router.junos.actionssets.actions.ospf;

import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.JunosAction;
import org.opennaas.extensions.router.junos.commandsets.commands.CommandNetconfConstants;
import org.opennaas.extensions.router.junos.commandsets.commands.EditNetconfCommand;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.ManagedElement;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSession;

public class ClearOSPFAction extends JunosAction {

	public ClearOSPFAction() {
		super();
		initialize();
	}

	/**
	 * Initialize protocolName, ActionId and velocity template
	 */
	protected void initialize() {
		this.setActionID(ActionConstants.OSPF_CLEAR);
		setTemplate("/VM_files/ospfClear.vm");
		this.protocolName = "netconf";
	}

	@Override
	public void executeListCommand(ActionResponse actionResponse, IProtocolSession protocol) throws ActionException {
		try {
			// when removing tags, none operation should be used as default
			EditNetconfCommand command = new EditNetconfCommand(getVelocityMessage(), CommandNetconfConstants.NONE_OPERATION);
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
	public boolean checkParams(Object params) throws ActionException {
		if (params != null)
			log.warn("Ingoring parameters in action " + getActionID());

		return true;
	}

	@Override
	public void prepareMessage() throws ActionException {
		if (template == null || template.equals(""))
			throw new ActionException("The path to Velocity template in Action " + getActionID() + " is null");

		if (!checkParams(params)) {
			throw new ActionException("Invalid parameters in action " + getActionID() + ".");
		}

		// tell velocity if element is a logical router or a physical one
		if (((ComputerSystem) modelToUpdate).getElementName() != null) {
			// is logicalRouter, add LRName param
			((ManagedElement) params).setElementName(((ComputerSystem) modelToUpdate).getElementName());
		} else if (((ManagedElement) params).getElementName() == null) {
			// avoid accessing null value when processing velocity template.
			((ManagedElement) params).setElementName("");
		}

		try {
			setVelocityMessage(prepareVelocityCommand(params, template));
		} catch (Exception e) {
			throw new ActionException(e);
		}

	}

}
