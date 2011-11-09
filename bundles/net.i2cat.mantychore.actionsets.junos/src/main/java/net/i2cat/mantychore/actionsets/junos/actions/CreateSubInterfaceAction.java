package net.i2cat.mantychore.actionsets.junos.actions;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.commandsets.junos.commands.EditNetconfCommand;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.ManagedElement;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSession;

/**
 * 
 * This class doesn't understand what subinterface type uses
 * @author Carlos Báez
 *
 */
@Deprecated
public class CreateSubInterfaceAction extends JunosAction {

	public CreateSubInterfaceAction() {
		super();
		initialize();
	}

	protected void initialize() {
		this.setActionID(ActionConstants.CREATESUBINTERFACE);
		setTemplate("/VM_files/createSubinterface.vm");
		this.protocolName = "netconf";
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		// TODO Auto-generated method stub
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
