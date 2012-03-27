package org.opennaas.extensions.router.junos.actionssets.actions.chassis;

import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.JunosAction;
import org.opennaas.extensions.router.junos.commandsets.commands.EditNetconfCommand;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.LogicalTunnelPort;
import org.opennaas.extensions.router.model.ManagedElement;
import org.opennaas.extensions.router.model.NetworkPort;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSession;

public class ConfigureEncapsulationAction extends JunosAction {

	Log	log	= LogFactory.getLog(ConfigureEncapsulationAction.class);

	public ConfigureEncapsulationAction() {
		super();
		initialize();
	}

	protected void initialize() {
		this.setActionID(ActionConstants.SETENCAPSULATION);
		setTemplate("");
		this.protocolName = "netconf";
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {

		if (! (params instanceof NetworkPort)) {
			throw new ActionException("Not valid object param " + params.getClass().getCanonicalName() + " for this action");
		}

		//TODO In this moment is not supported ethernet encapsulation
//		if (params instanceof EthernetPort) {
//			return checkParamsForEth((EthernetPort) params);
//		} else
		if (params instanceof LogicalTunnelPort) {
			return checkParamsForLT((LogicalTunnelPort) params);
		} else
			throw new ActionException("Not valid object param " + params.getClass().getCanonicalName() + " for this action");
	}

//	private boolean checkParamsForEth(EthernetPort eth) throws ActionException {
//		if (eth.getName() == null || eth.getName().isEmpty())
//			throw new ActionException("Not valid name for the interface");
//
//		setTemplate("/VM_files/configureEthVLAN.vm");
//		return true;
//	}

	private boolean checkParamsForLT(LogicalTunnelPort lt) throws ActionException {
		if (lt.getName() == null || lt.getName().isEmpty() || !lt.getName().startsWith("lt"))
			throw new ActionException("Not valid name for the interface");

		setTemplate("/VM_files/setEncapsulationLTVLAN.vm");
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

			//fill logical router id
			if (((ComputerSystem)modelToUpdate).getElementName() != null) {
				//is logicalRouter, add LRName param
				((ManagedElement)params).setElementName(((ComputerSystem)modelToUpdate).getElementName());
			//TODO If we don't have a ManagedElement initialized
			} else if (params!= null && params instanceof ManagedElement && ((ManagedElement)params).getElementName()==null){
				((ManagedElement)params).setElementName("");
			}

			//fill description param
			if (params instanceof ManagedElement
					&& (((ManagedElement)params).getDescription()==null || ((ManagedElement)params).getDescription().equals(""))) {
				((ManagedElement)params).setDescription("");
			}

			setVelocityMessage(prepareVelocityCommand(params, template));

		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

}
