package org.opennaas.extensions.router.junos.actionssets.actions.ip;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.JunosAction;
import org.opennaas.extensions.router.junos.commandsets.commands.EditNetconfCommand;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.ManagedElement;

public class SetIPv6Action extends JunosAction {

	public SetIPv6Action() {
		super();
		initialize();
	}

	private void initialize() {
		this.setActionID(ActionConstants.SETIPv6);
		setTemplate("/VM_files/configureIPv6.vm");
		this.protocolName = "netconf";
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
	public boolean checkParams(Object params) throws ActionException {
		if (params == null || !(params instanceof LogicalPort))
			throw new ActionException("Invalid parameters for action " + getActionID());
		if (((LogicalPort) params).getProtocolEndpoint().isEmpty())
			throw new ActionException("No IP address given to action " + getActionID());
		if (!(((LogicalPort) params).getProtocolEndpoint().get(0) instanceof IPProtocolEndpoint)) {
			throw new ActionException("No IP address given to action " + getActionID());
		}
		if (((IPProtocolEndpoint) (((LogicalPort) params).getProtocolEndpoint().get(0))).getIPv6Address() == null ||
				((IPProtocolEndpoint) (((LogicalPort) params).getProtocolEndpoint().get(0))).getIPv6Address().isEmpty()) {
			throw new ActionException("No IP address given to action " + getActionID());
		}

		return true;
	}

	@Override
	public void prepareMessage() throws ActionException {

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
