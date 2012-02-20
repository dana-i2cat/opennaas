package net.i2cat.mantychore.actionsets.junos.actions.ospf;

import java.util.HashMap;
import java.util.Map;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.actionsets.junos.actions.JunosAction;
import net.i2cat.mantychore.commandsets.junos.commands.EditNetconfCommand;
import net.i2cat.mantychore.model.ManagedSystemElement.OperationalStatus;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSession;

public class EnableOSPFInInterfaceAction extends JunosAction {

	/**
	 * 
	 */
	public EnableOSPFInInterfaceAction() {
		super();
		initialize();
	}

	/**
	 * Initialize protocolName, ActionId and velocity template
	 */
	protected void initialize() {

		setActionID(ActionConstants.OSPF_ENABLE_INTERFACE);
		setTemplate("/VM_files/enableOSPFInterface.vm");
		this.protocolName = "netconf";
	}

	@Override
	public void executeListCommand(ActionResponse actionResponse, IProtocolSession protocol) throws ActionException {

		try {
			EditNetconfCommand command = new EditNetconfCommand(getVelocityMessage());
			command.initialize();
			Response response = sendCommandToProtocol(command, protocol);
			actionResponse.addResponse(response);
		} catch (Exception e) {
			throw new ActionException(this.actionID, e);
		}
		validateAction(actionResponse);
	}

	@Override
	public void prepareMessage() throws ActionException {

		if (getTemplate() == null || getTemplate().equals(""))
			throw new ActionException("The path to Velocity template in Action " + getActionID() + " is null");
		try {
			Map<String, Object> extraParams = new HashMap<String, Object>();
			extraParams.put("statusDown", OperationalStatus.STOPPED.toString());
			extraParams.put("statusUp", OperationalStatus.OK.toString());

			setVelocityMessage(prepareVelocityCommand(params, getTemplate(), extraParams));
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	@Override
	public void parseResponse(Object responseMessage, Object model) throws ActionException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		// TODO Auto-generated method stub
		return false;
	}

}
