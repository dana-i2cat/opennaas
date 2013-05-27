package org.opennaas.extensions.router.junos.actionssets.actions.bgp;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.JunosAction;
import org.opennaas.extensions.router.junos.commandsets.commands.CommandNetconfConstants;
import org.opennaas.extensions.router.junos.commandsets.commands.EditNetconfCommand;
import org.opennaas.extensions.router.model.BGPService;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.ManagedElement;

public class UnconfigureBGPAction extends JunosAction {

	public UnconfigureBGPAction() {
		super();
		initialize();
	}

	protected void initialize() {
		this.setActionID(ActionConstants.CONFIGURE_BGP);
		this.protocolName = "netconf";
		setTemplate("/VM_files/unconfigureBGP.vm");
	}

	@Override
	public void executeListCommand(ActionResponse actionResponse, IProtocolSession protocol) throws ActionException {

		try {
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

	/**
	 * Params must be a BGPService.
	 * 
	 * @param param
	 *            : BGPService to configure
	 * @throws ActionException
	 *             if params is null or is not a BGPService
	 */
	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (params == null || !(params instanceof BGPService))
			throw new ActionException("Invalid parameters for action " + getActionID());

		return true;
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
