package org.opennaas.extensions.router.junos.actionssets.actions.ip;

import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.JunosAction;
import org.opennaas.extensions.router.junos.commandsets.commands.EditNetconfCommand;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.ManagedElement;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSession;

public class SetInterfaceDescriptionAction extends JunosAction {

	public SetInterfaceDescriptionAction() {
		super();
		initialize();
	}

	protected void initialize() {
		this.setActionID(ActionConstants.SETINTERFACEDESCRIPTION);
		this.protocolName = "netconf";
	}

	/**
	 * Params must be a LogicalPort. Action template is chosen depending of params being a physical interface or a subinterface (logical)
	 * 
	 * @param params
	 *            : LogicalPort whose description to set
	 * @throws ActionException
	 *             if params is null or is not a LogicalPort
	 */
	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (params == null || !(params instanceof LogicalPort))
			throw new ActionException("Invalid parameters for action " + getActionID());
		setTemplate((params instanceof EthernetPort) ? "/VM_files/setLogicalInterfaceDescription.vm" : "/VM_files/setInterfaceDescription.vm");
		return true;
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

		checkParams(params);
		if (template == null || template.equals(""))
			throw new ActionException("The path to Velocity template in Action " + getActionID() + " is null");
		try {
			if (((ComputerSystem) modelToUpdate).getElementName() != null) {
				// is logicalRouter, add LRName param
				((ManagedElement) params).setElementName(((ComputerSystem) modelToUpdate).getElementName());
				// TODO If we don't have a ManagedElement initialized
			} else if (params != null && params instanceof ManagedElement && ((ManagedElement) params).getElementName() == null) {
				((ManagedElement) params).setElementName("");

			}
			setVelocityMessage(prepareVelocityCommand(params, template));
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

}
