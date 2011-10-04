package net.i2cat.mantychore.actionsets.junos.actions;

import java.util.HashMap;
import java.util.Map;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.commandsets.junos.commands.EditNetconfCommand;
import net.i2cat.mantychore.model.LogicalPort;
import net.i2cat.mantychore.model.ManagedSystemElement.OperationalStatus;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSession;

public class ConfigureStatusAction extends JunosAction {

	public ConfigureStatusAction() {
		super();
		initialize();
	}

	protected void initialize() {
		this.setActionID(ActionConstants.CONFIGURESTATUS);
		setTemplate("/VM_files/configureStatus.vm");
		this.protocolName = "netconf";
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
	public boolean checkParams(Object params) throws ActionException {
		if (!(params instanceof LogicalPort))
			return false;
		LogicalPort logicalPort = (LogicalPort) params;
		if (logicalPort.getOperationalStatus() == null)
			return false;
		return true;
	}

	@Override
	public void prepareMessage() throws ActionException {
		if (template == null || template.equals(""))
			throw new ActionException("The path to Velocity template in Action " + getActionID() + " is null");
		try {
			Map<String, Object> extraParams = new HashMap<String, Object>();
			extraParams.put("statusDown", OperationalStatus.STOPPED.toString());
			extraParams.put("statusUp", OperationalStatus.OK.toString());

			setVelocityMessage(prepareVelocityCommand(params, template, extraParams));
		} catch (Exception e) {
			throw new ActionException(e);
		}

	}

}
