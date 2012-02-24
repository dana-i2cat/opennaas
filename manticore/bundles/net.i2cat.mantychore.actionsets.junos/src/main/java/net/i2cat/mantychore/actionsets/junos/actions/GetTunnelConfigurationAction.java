package net.i2cat.mantychore.actionsets.junos.actions;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.commandsets.junos.commands.GetNetconfCommand;
import net.i2cat.mantychore.commandsets.junos.digester.IPInterfaceParser;
import net.i2cat.mantychore.commandsets.junos.digester.RoutingOptionsParser;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.GRETunnelService;
import net.i2cat.mantychore.model.LogicalTunnelPort;
import net.i2cat.mantychore.model.ManagedElement;
import net.i2cat.mantychore.model.Service;
import net.i2cat.mantychore.model.System;
import net.i2cat.netconf.rpc.Reply;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.CommandException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.xml.sax.SAXException;

public class GetTunnelConfigurationAction extends JunosAction {

	private static Log	log			= LogFactory.getLog(GetTunnelConfigurationAction.class);

	private String		ifaceName	= null;

	public GetTunnelConfigurationAction() {
		super();
		initialize();

	}

	protected void initialize() {
		this.setActionID(ActionConstants.GETTUNNELCONFIG);
		setTemplate("/VM_files/getconfiguration.vm");
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

	@Override
	public void parseResponse(Object responseMessage, Object model) throws ActionException {
		if (this.modelToUpdate == null)
			this.modelToUpdate = new ComputerSystem();

		String message;
		try {
			System routerModel = (System) model;

			/* getting interface information */
			if (responseMessage instanceof Reply) {
				Reply rpcReply = (Reply) responseMessage;
				message = rpcReply.getContain();
			} else {
				throw new CommandException("Error parsing response: the response is not a Reply message");
			}
			routerModel.removeAllremoveManagedSystemElementByType(ComputerSystem.class);

			if (message != null) {

				routerModel = parseInterface(routerModel, message);

				routerModel = FilterInterface(routerModel);

				routerModel = parseRoutingOptions(routerModel, message);

			}

		} catch (Exception e) {
			throw new ActionException(e);

		}
	}

	private System FilterInterface(System routerModel) {

		GRETunnelService gre = null;
		for (Service service : routerModel.getHostedService()) {
			if (service instanceof GRETunnelService) {
				GRETunnelService greService = (GRETunnelService) service;
				if (greService.getName().equals(ifaceName)) {
					gre = greService;
				}
				routerModel.removeHostedService(service);
			}
		}
		routerModel.removeAllLogicalDeviceByType(EthernetPort.class);
		routerModel.removeAllLogicalDeviceByType(LogicalTunnelPort.class);
		if (gre != null)
			routerModel.addHostedService(gre);
		return routerModel;
	}

	private System parseInterface(System routerModel, String message)
			throws IOException, SAXException {

		IPInterfaceParser ipInterfaceParser = new IPInterfaceParser(routerModel);
		ipInterfaceParser.init();
		ipInterfaceParser.configurableParse(new ByteArrayInputStream(message.getBytes()));

		routerModel = ipInterfaceParser.getModel();
		return routerModel;
	}

	/**
	 * Parses routing options data from message and puts in into routerModel.
	 * 
	 * @param routerModel
	 *            to store parsed data
	 * @param message
	 *            to parse interfaces data from
	 * @return routerModel updated with routing options information from message.
	 * @throws IOException
	 * @throws SAXException
	 */
	private System parseRoutingOptions(net.i2cat.mantychore.model.System routerModel, String message)
			throws IOException, SAXException {

		RoutingOptionsParser routingOptionsParser = new RoutingOptionsParser(routerModel);
		routingOptionsParser.init();
		routingOptionsParser.configurableParse(new ByteArrayInputStream(message.getBytes("UTF-8")));

		routerModel = routingOptionsParser.getModel();

		return routerModel;
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {

		if (params == null)
			throw new ActionException("The Action " + getActionID() + " need a GRE param.");
		if (params instanceof GRETunnelService)
			return true;
		throw new ActionException("The Action " + getActionID() + " don't accept this type of params");

	}

	@Override
	public void prepareMessage() throws ActionException {
		if (template == null || template.equals(""))
			throw new ActionException("The path to Velocity template in Action " + getActionID() + " is null");

		try {
			if (((ComputerSystem) modelToUpdate).getElementName() != null) {
				// is logicalRouter, add LRName param
				((ManagedElement) params).setElementName(((ComputerSystem) modelToUpdate).getElementName());
				// TODO If we don't have a ManagedElement initialized
			} else if (params != null && params instanceof ManagedElement && ((ManagedElement) params).getElementName() == null) {
				((ManagedElement) params).setElementName("");
			}

			ifaceName = ((Service) params).getName();
			setVelocityMessage(prepareVelocityCommand(params, template));

		} catch (Exception e) {
			throw new ActionException(e);
		}
	}
}
