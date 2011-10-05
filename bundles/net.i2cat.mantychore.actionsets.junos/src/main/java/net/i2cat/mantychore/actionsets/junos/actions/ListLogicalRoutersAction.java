package net.i2cat.mantychore.actionsets.junos.actions;

import java.io.ByteArrayInputStream;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.commandsets.junos.commands.GetNetconfCommand;
import net.i2cat.mantychore.commandsets.junos.digester.DigesterEngine;
import net.i2cat.mantychore.commandsets.junos.digester.ListLogicalRoutersParser;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.netconf.rpc.Reply;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.CommandException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ListLogicalRoutersAction extends JunosAction {
	Log	logger	= LogFactory.getLog(ListLogicalRoutersAction.class);

	public ListLogicalRoutersAction() {
		super();
		initialize();

	}

	protected void initialize() {
		this.setActionID(ActionConstants.GETLOGICALROUTERS);
		setTemplate("/VM_files/getLogicalRouters.vm");
		this.protocolName = "netconf";
	}

	@Override
	public void executeListCommand(ActionResponse actionResponse, IProtocolSession protocol) throws ActionException {
		try {
			GetNetconfCommand command = new GetNetconfCommand(getVelocityMessage());
			command.initialize();
			Response response = sendCommandToProtocol(command, protocol);
			actionResponse.addResponse(response);
		} catch (Exception e) {
			throw new ActionException(this.actionID, e);
		}
		validateAction(actionResponse);

	}

	public void parseResponse(Object responseMessage, Object model) throws ActionException {
		String message;
		try {
			net.i2cat.mantychore.model.System routerModel = (net.i2cat.mantychore.model.System) model;
			DigesterEngine listLogicalRoutersParser = new ListLogicalRoutersParser();
			// DigesterEngine logicalInterfParser = new IPConfigurationInterfaceParser();
			listLogicalRoutersParser.init();

			/* getting interface information */
			if (responseMessage instanceof Reply) {
				Reply rpcReply = (Reply) responseMessage;
				message = rpcReply.getContain();
			} else {
				throw new CommandException("Error parsing response: the response is not a Reply message");
			}

			listLogicalRoutersParser.configurableParse(new ByteArrayInputStream(message.getBytes()));

			for (Object idLogicalRouter : listLogicalRoutersParser.getMapElements().values()) {
				ComputerSystem system = new ComputerSystem();
				system.setName((String) idLogicalRouter);
				routerModel.addSystem(system);
			}

		} catch (Exception e) {
			throw new ActionException(e);
		}

	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		// For this Action params are not allowed
		if (params == null)
			return true;
		else {
			throw new ActionException("The Action " + getActionID() + " don't accept params");
		}
	}

	public void prepareMessage() throws ActionException {
		if (template == null || template.equals(""))
			throw new ActionException("The path to Velocity template in Action " + getActionID() + " is null");
		checkParams(params);
		try {
			setVelocityMessage(prepareVelocityCommand(params, template, null));
		} catch (Exception e) {
			throw new ActionException();
		}

	}

}
