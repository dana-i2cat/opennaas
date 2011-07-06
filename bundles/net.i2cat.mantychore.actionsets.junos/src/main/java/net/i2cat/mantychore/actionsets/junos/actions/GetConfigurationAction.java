package net.i2cat.mantychore.actionsets.junos.actions;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.commandsets.junos.commands.GetNetconfCommand;
import net.i2cat.mantychore.commandsets.junos.digester.DigesterEngine;
import net.i2cat.mantychore.commandsets.junos.digester.IPConfigurationInterfaceParser;
import net.i2cat.mantychore.commandsets.junos.digester.RoutingOptionsParser;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.model.NextHopRoute;
import net.i2cat.netconf.rpc.Reply;
import net.i2cat.nexus.resources.action.ActionException;
import net.i2cat.nexus.resources.action.ActionResponse;
import net.i2cat.nexus.resources.command.CommandException;
import net.i2cat.nexus.resources.command.Response;
import net.i2cat.nexus.resources.protocol.IProtocolSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GetConfigurationAction extends JunosAction {
	Log	logger	= LogFactory.getLog(GetConfigurationAction.class);

	public GetConfigurationAction() {
		super();
		initialize();

	}

	protected void initialize() {
		this.setActionID(ActionConstants.GETCONFIG);
		// setTemplate("/VM_files/getInterfaceInformation.vm");
		setTemplate("/VM_files/getconfiguration.vm");
		// setTemplate("/VM_files/getInterfaces.vm");
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

	}

	public void parseResponse(Object responseMessage, Object model) throws ActionException {
		String message;
		try {
			net.i2cat.mantychore.model.System routerModel = (net.i2cat.mantychore.model.System) model;
			DigesterEngine logicalInterfParser = new IPConfigurationInterfaceParser();
			logicalInterfParser.init();

			/* getting interface information */
			if (responseMessage instanceof Reply) {
				Reply rpcReply = (Reply) responseMessage;
				message = rpcReply.getContain();
			} else {
				throw new CommandException("Error parsing response: the response is not a Reply message");
			}

			logicalInterfParser.configurableParse(new ByteArrayInputStream(message.getBytes()));

			FileWriter f = new FileWriter("C:/Dev/configuration.txt");
			f.write(message.getBytes().toString());

			// /TODO implements a better method to merge the elements in model
			// now are deleted all the existing elements of the class EthernetPort
			routerModel.removeAllLogicalDeviceByType(EthernetPort.class);
			for (String keyInterf : logicalInterfParser.getMapElements().keySet()) {
				routerModel.addLogicalDevice((LogicalDevice) logicalInterfParser.getMapElements().get(keyInterf));
			}

			f.write("\n");
			f.write("Key set size " + logicalInterfParser.getMapElements().keySet().size());
			/* Parse routing options info */
			DigesterEngine routingOptionsParser = new RoutingOptionsParser();
			routingOptionsParser.init();

			if (message != null) {

				routingOptionsParser.configurableParse(new ByteArrayInputStream(message.getBytes("UTF-8")));
				// add to the router model
				for (String keyInterf : routingOptionsParser.getMapElements().keySet()) {
					NextHopRoute nh = (NextHopRoute) routingOptionsParser.getMapElements().get(keyInterf);
					routerModel.addNextHopRoute(nh);
				}
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