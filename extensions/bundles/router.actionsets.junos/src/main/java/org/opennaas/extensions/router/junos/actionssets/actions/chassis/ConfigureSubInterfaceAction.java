package org.opennaas.extensions.router.junos.actionssets.actions.chassis;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.JunosAction;
import org.opennaas.extensions.router.junos.commandsets.commands.EditNetconfCommand;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.LogicalTunnelPort;
import org.opennaas.extensions.router.model.ManagedElement;

/**
 * Configures a subinterface with given params. If given subinterface doesn't exist it is created.If it already exists, overrides it with given data.
 * 
 * @param params
 *            : EthernetPort or LogicalTunnelPort identifying the subinterface to configure and containing all data to configure it.
 */
public class ConfigureSubInterfaceAction extends JunosAction {

	public ConfigureSubInterfaceAction() {
		super();
		initialize();
	}

	protected void initialize() {
		this.setActionID(ActionConstants.CONFIGURESUBINTERFACE);
		setTemplate("");
		this.protocolName = "netconf";
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (params instanceof EthernetPort) {

			EthernetPort eth = (EthernetPort) params;

			checkEthernetParams(eth);

		} else if (params instanceof LogicalTunnelPort) {
			LogicalTunnelPort lt = (LogicalTunnelPort) params;

			if (lt.getName() == null || lt.getName().isEmpty() || !lt.getName().startsWith("lt"))
				throw new ActionException("Not valid name for the interface");

			setTemplate("/VM_files/configureLogicalTunnelVLAN.vm");

		} else
			throw new ActionException("Not valid object param " + params.getClass().getCanonicalName() + " for this action");

		return true;
	}

	private void checkEthernetParams(EthernetPort eth) throws ActionException {

		if (eth.getName() == null || eth.getName().isEmpty())
			throw new ActionException("Not valid name for the interface");

		if (eth.getName().startsWith("gr-"))
			setTemplate("/VM_files/configureGRELogicalInterface.vm");

		else if (eth.getProtocolEndpoint().isEmpty())
			setTemplate("/VM_files/configureEthWithoutVLAN.vm");

		else
			setTemplate("/VM_files/configureEthVLAN.vm");

	}

	@Override
	public void executeListCommand(ActionResponse actionResponse, IProtocolSession protocol) throws ActionException {

		try {
			// not only check the params also it change the velocity template according to the interface

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

		if (getParams() == null)
			throw new ActionException("Params in " + getActionID() + "are null.");
		checkParams(getParams());
		if (template == null || template.equals(""))
			throw new ActionException("The path to Velocity template in Action " + getActionID() + " is null.");

		try {

			// fill logical router id
			if (((ComputerSystem) modelToUpdate).getElementName() != null) {
				// is logicalRouter, add LRName param
				((ManagedElement) params).setElementName(((ComputerSystem) modelToUpdate).getElementName());
				// TODO If we don't have a ManagedElement initialized
			} else if (params != null && params instanceof ManagedElement && ((ManagedElement) params).getElementName() == null) {
				((ManagedElement) params).setElementName("");

			}

			// fill description param
			if (params instanceof ManagedElement
					&& (((ManagedElement) params).getDescription() == null || ((ManagedElement) params).getDescription().equals(""))) {
				((ManagedElement) params).setDescription("");
			}

			setVelocityMessage(prepareVelocityCommand(params, template));
		} catch (Exception e) {
			throw new ActionException(e);
		}

	}

}
