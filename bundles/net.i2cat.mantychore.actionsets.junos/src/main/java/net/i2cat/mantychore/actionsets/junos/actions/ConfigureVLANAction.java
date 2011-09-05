package net.i2cat.mantychore.actionsets.junos.actions;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.commandsets.junos.commands.EditNetconfCommand;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.LogicalTunnelPort;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfigureVLANAction extends JunosAction {

	Log	log	= LogFactory.getLog(ConfigureVLANAction.class);

	public ConfigureVLANAction() {
		super();
		initialize();
	}

	protected void initialize() {
		this.setActionID(ActionConstants.SETVLAN);
		setTemplate("");
		this.protocolName = "netconf";
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (params instanceof EthernetPort) {

			EthernetPort eth = (EthernetPort) params;

			if (eth.getElementName() == null || eth.getElementName().isEmpty())
				throw new ActionException("Not valid name for the interface");

			setTemplate("/VM_files/configureEthVLAN.vm");

		} else if (params instanceof LogicalTunnelPort) {
			LogicalTunnelPort lt = (LogicalTunnelPort) params;

			if (lt.getElementName() == null || lt.getElementName().isEmpty() || !lt.getElementName().startsWith("lt"))
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
			// IPUtilsHelper ipUtilsHelper = new IPUtilsHelper();
			// Map<String, Object> extraParams = new HashMap<String, Object>();
			// extraParams.put("ipUtilsHelper", ipUtilsHelper);
			setVelocityMessage(prepareVelocityCommand(params, template, null/* extraParams */));

		} catch (Exception e) {
			throw new ActionException(e);
		}

	}

}
