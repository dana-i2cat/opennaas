package net.i2cat.mantychore.actionsets.junos.actions;

import java.io.ByteArrayInputStream;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.commandsets.junos.commands.GetNetconfCommand;
import net.i2cat.mantychore.commandsets.junos.digester.DigesterEngine;
import net.i2cat.mantychore.commandsets.junos.digester.IPInterfaceParser;
import net.i2cat.mantychore.commandsets.junos.digester.ListLogicalRoutersParser;
import net.i2cat.mantychore.commandsets.junos.digester.RoutingOptionsParser;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.model.LogicalTunnelPort;
import net.i2cat.mantychore.model.NextHopRoute;
import net.i2cat.netconf.rpc.Reply;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.CommandException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSession;

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
		validateAction(actionResponse);

	}

	public void parseResponse(Object responseMessage, Object model) throws ActionException {
		String message;
		try {
			net.i2cat.mantychore.model.System routerModel = (net.i2cat.mantychore.model.System) model;

			/* getting interface information */
			if (responseMessage instanceof Reply) {
				Reply rpcReply = (Reply) responseMessage;
				message = rpcReply.getContain();
			} else {
				throw new CommandException("Error parsing response: the response is not a Reply message");
			}
			routerModel.removeAllremoveManagedSystemElementByType(ComputerSystem.class);

			/* Parse routing options info */
			DigesterEngine listLogicalRoutersParser = new ListLogicalRoutersParser();
			listLogicalRoutersParser.init();
			listLogicalRoutersParser.configurableParse(new ByteArrayInputStream(message.getBytes()));

			if (message != null) {

				for (String key : listLogicalRoutersParser.getMapElements().keySet()) {

					ComputerSystem system = new ComputerSystem();
					system.setName((String) listLogicalRoutersParser.getMapElements().get(key));
					routerModel.addManagedSystemElement(system);
				}
			}
			/* Parse LR info */

			/* Parse interface options info */
			DigesterEngine logicalInterfParser = new IPInterfaceParser();
			logicalInterfParser.init();
			logicalInterfParser.configurableParse(new ByteArrayInputStream(message.getBytes()));

			// /TODO implements a better method to merge the elements in model
			// now are deleted all the existing elements of the class EthernetPort
			routerModel.removeAllLogicalDeviceByType(EthernetPort.class);
			routerModel.removeAllLogicalDeviceByType(LogicalTunnelPort.class);
			for (String keyInterf : logicalInterfParser.getMapElements().keySet()) {
				routerModel.addLogicalDevice((LogicalDevice) logicalInterfParser.getMapElements().get(keyInterf));
			}
			/* Parse interface options info */

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
			/* Parse routing options info */
		} catch (Exception e) {
			throw new ActionException(e);
		}

	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		// For this Action params are not allowed
		return true;
	}

	public void prepareMessage() throws ActionException {
		if (template == null || template.equals(""))
			throw new ActionException("The path to Velocity template in Action " + getActionID() + " is null");
		checkParams(params);
		try {
			setVelocityMessage(prepareVelocityCommand(params, template));
		} catch (Exception e) {
			throw new ActionException();
		}
	}

}