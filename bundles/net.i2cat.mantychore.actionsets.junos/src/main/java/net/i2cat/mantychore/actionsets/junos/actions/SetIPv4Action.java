package net.i2cat.mantychore.actionsets.junos.actions;

import java.util.HashMap;
import java.util.Map;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.commandsets.junos.commands.EditNetconfCommand;
import net.i2cat.mantychore.commandsets.junos.commons.IPUtilsHelper;
import net.i2cat.nexus.resources.action.ActionException;
import net.i2cat.nexus.resources.action.ActionResponse;
import net.i2cat.nexus.resources.protocol.IProtocolSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SetIPv4Action extends JunosAction {
	Log	log	= LogFactory.getLog(SetIPv4Action.class);

	public SetIPv4Action() {
		super();
		initialize();
	}

	protected void initialize() {
		this.setActionID(ActionConstants.SETIPv4);
		setTemplate("/VM_files/configureIPv4.vm");
		this.protocolName = "netconf";

	}

	@Override
	public void parseResponse(Object responseMessage, Object model) {
		// TODO it is not necessary to do response

	}

	@Override
	public boolean checkParams(Object params) {
		// TODO CHECK PARAMS
		return true;
	}

	public void prepareMessage() throws ActionException {
		// TODO implements
		if (template == null || template.equals(""))
			throw new ActionException("The path to Velocity template in Action " + getActionID() + " is null");
		try {
			IPUtilsHelper ipUtilsHelper = new IPUtilsHelper();
			Map<String, Object> extraParams = new HashMap<String, Object>();
			extraParams.put("ipUtilsHelper", ipUtilsHelper);

			setVelocityMessage(prepareVelocityCommand(params, template, extraParams));
		} catch (Exception e) {
			throw new ActionException(e);
		}
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
}
