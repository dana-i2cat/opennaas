package net.i2cat.mantychore.actionsets.junos.actions;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.commandsets.junos.commands.EditNetconfCommand;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.LogicalTunnelPort;
import net.i2cat.mantychore.model.ManagedElement;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSession;

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

			if (eth.getName() == null || eth.getName().isEmpty())
				throw new ActionException("Not valid name for the interface");

			setTemplate("/VM_files/configureEthVLAN.vm");

		} else if (params instanceof LogicalTunnelPort) {
			LogicalTunnelPort lt = (LogicalTunnelPort) params;

			if (lt.getName() == null || lt.getName().isEmpty() || !lt.getName().startsWith("lt"))
				throw new ActionException("Not valid name for the interface");

			setTemplate("/VM_files/configureLogicalTunnelVLAN.vm");

		} else
			throw new ActionException("Not valid object param " + params.getClass().getCanonicalName() + " for this action");

		return true;
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
			if (((ComputerSystem)modelToUpdate).getElementName() != null) { 
				//is logicalRouter, add LRName param
				((ManagedElement)params).setElementName(((ComputerSystem)modelToUpdate).getElementName()); 
			}
			setVelocityMessage(prepareVelocityCommand(params, template));
		} catch (Exception e) {
			throw new ActionException(e);
		}

	}

}
