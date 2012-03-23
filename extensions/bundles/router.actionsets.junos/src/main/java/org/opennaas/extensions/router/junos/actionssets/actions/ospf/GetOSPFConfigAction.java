package org.opennaas.extensions.router.junos.actionssets.actions.ospf;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.JunosAction;
import org.opennaas.extensions.router.junos.commandsets.commands.GetNetconfCommand;
import org.opennaas.extensions.router.junos.commandsets.digester.IPInterfaceParser;
import org.opennaas.extensions.router.junos.commandsets.digester.ProtocolsParser;
import org.opennaas.extensions.router.junos.commandsets.digester.RoutingOptionsParser;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.GRETunnelService;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.LogicalTunnelPort;
import org.opennaas.extensions.router.model.ManagedElement;
import org.opennaas.extensions.router.model.OSPFAreaConfiguration;
import org.opennaas.extensions.router.model.OSPFProtocolEndpointBase;
import org.opennaas.extensions.router.model.OSPFService;
import org.opennaas.extensions.router.model.Service;
import org.opennaas.extensions.router.model.System;
import net.i2cat.netconf.rpc.Reply;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.CommandException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.xml.sax.SAXException;

/**
 * This action is responsible of retrieving OSPF related information from the physical device and populate the router model with it.
 * 
 * Current implementation asks for the whole router configuration and parses only required data: </b> - interfaces </b> - protocols/ospf </b> -
 * routing-options (although not all is required (only router-id), all is parsed as we reuse existing complete parser).
 * 
 * @author isart
 */
public class GetOSPFConfigAction extends JunosAction {

	public GetOSPFConfigAction() {
		super();
		initialize();
	}

	/**
	 * Initialize protocolName, ActionId and velocity template
	 */
	protected void initialize() {
		this.setActionID(ActionConstants.OSPF_GET_CONFIGURATION);
		setTemplate("/VM_files/getconfiguration.vm"); // ask for the whole configuration
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

		// FIXME This "if" is important because it resets the model if we want to update it
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

			if (message != null) {

				/* Parse interface info */
				routerModel = parseInterfaces(routerModel, message);

				/* Parse protocols info */
				// Protocols parsing should be done before parsing routing-options:
				// Protocol parser creates classes in the model that require being updated by routing-options parser.
				// That's the case of RouteCalculationServices which routerID is set by RoutingOptionsParser
				routerModel = parseProtocols(routerModel, message);

				/* Parse routing options info */
				routerModel = parseRoutingOptions(routerModel, message);
			}
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	/**
	 * Parses interfaces data from message and puts in into routerModel.
	 * 
	 * @param routerModel
	 *            to store parsed data
	 * @param message
	 *            to parse interfaces data from
	 * @return routerModel updated with interfaces information from message.
	 * @throws IOException
	 * @throws SAXException
	 */
	private System parseInterfaces(System routerModel, String message)
			throws IOException, SAXException {

		// TODO implements a better method to merge the elements in model
		// now are deleted all the existing elements the parser creates
		// before adding new ones (calling the parser)
		routerModel.removeAllLogicalDeviceByType(EthernetPort.class);
		routerModel.removeAllLogicalDeviceByType(LogicalTunnelPort.class);
		routerModel.removeAllHostedServicesByType(GRETunnelService.class);

		IPInterfaceParser ipInterfaceParser = new IPInterfaceParser(routerModel);
		ipInterfaceParser.init();
		ipInterfaceParser.configurableParse(new ByteArrayInputStream(message.getBytes()));

		routerModel = ipInterfaceParser.getModel();
		return routerModel;
	}

	/**
	 * Parses protocols data from message and puts in into routerModel.
	 * 
	 * @param routerModel
	 *            to store parsed data
	 * @param message
	 *            to parse interfaces data from
	 * @return routerModel updated with interfaces information from message.
	 * @throws
	 * @throws IOException
	 * @throws SAXException
	 */
	private System parseProtocols(System routerModel, String message) throws IOException, SAXException {

		routerModel = removeOSPFServiceFromModel(routerModel);

		ProtocolsParser protocolsParser = new ProtocolsParser(routerModel);
		protocolsParser.init();
		protocolsParser.configurableParse(new ByteArrayInputStream(message.getBytes("UTF-8")));

		routerModel = protocolsParser.getModel();

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
	private System parseRoutingOptions(org.opennaas.extensions.router.model.System routerModel, String message)
			throws IOException, SAXException {

		RoutingOptionsParser routingOptionsParser = new RoutingOptionsParser(routerModel);
		routingOptionsParser.init();
		routingOptionsParser.configurableParse(new ByteArrayInputStream(message.getBytes("UTF-8")));

		routerModel = routingOptionsParser.getModel();

		return routerModel;
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
			Object velocityParams = params;
			if (((ComputerSystem) modelToUpdate).getElementName() != null) {
				// is logicalRouter, add LRName param
				if (velocityParams == null)
					velocityParams = new ComputerSystem();
				((ManagedElement) velocityParams).setElementName(((ComputerSystem) modelToUpdate).getElementName());

				// TODO If we don't have a ManagedElement initialized

				// check params
			} else if (params != null
					&& params instanceof ManagedElement
					&& ((ManagedElement) params).getElementName() == null) {

				((ManagedElement) velocityParams).setElementName("");

			} else if (params == null) {
				velocityParams = "null";
			}
			setVelocityMessage(prepareVelocityCommand(velocityParams, template));
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	private System removeOSPFServiceFromModel(System routerModel) {
		// get OSPFService
		OSPFService ospfService = null;
		for (Service service : routerModel.getHostedService()) {
			if (service instanceof OSPFService)
				ospfService = (OSPFService) service;
		}
		if (ospfService == null)
			return routerModel;

		// REMOVE ALL MODEL RELATED TO OSPF
		for (OSPFAreaConfiguration areaConf : ospfService.getOSPFAreaConfiguration()) {
			for (OSPFProtocolEndpointBase ospfPEP : areaConf.getOSPFArea().getEndpointsInArea()) {
				for (LogicalPort port : ospfPEP.getLogicalPorts()) {
					// unlink OSPFProtocolEndpoint with NetworkPorts.
					ospfPEP.removeLogicalPort(port);
				}
				areaConf.getOSPFArea().removeEndpointInArea(ospfPEP);
			}
			ospfService.removeOSPFAreaConfiguration(areaConf);
		}
		routerModel.removeHostedService(ospfService);
		return routerModel;
	}
}
