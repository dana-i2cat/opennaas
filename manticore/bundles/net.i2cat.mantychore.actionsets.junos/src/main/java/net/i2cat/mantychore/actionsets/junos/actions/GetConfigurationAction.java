package net.i2cat.mantychore.actionsets.junos.actions;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.commandsets.junos.commands.GetNetconfCommand;
import net.i2cat.mantychore.commandsets.junos.digester.DigesterEngine;
import net.i2cat.mantychore.commandsets.junos.digester.IPInterfaceParser;
import net.i2cat.mantychore.commandsets.junos.digester.ListLogicalRoutersParser;
import net.i2cat.mantychore.commandsets.junos.digester.RoutingOptionsParser;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.GRETunnelService;
import net.i2cat.mantychore.model.LogicalTunnelPort;
import net.i2cat.mantychore.model.ManagedElement;
import net.i2cat.mantychore.model.NetworkPort;
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

	@Override
	public void parseResponse(Object responseMessage, Object model) throws ActionException {
		/* the model have to be null and we have to initialize */

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
				/* Parse LR info */
				routerModel = parseLRs(routerModel, message);

				/* Parse interface info */
				routerModel = parseInterfaces(routerModel, message);

				/* Parse routing options info */
				// Routing options parsing should be done after parsing protocols (protocols is required):
				// Protocol parser creates classes in the model that require being updated by routing-options parser.
				// That's the case of RouteCalculationServices which routerID is set by RoutingOptionsParser
				routerModel = parseRoutingOptions(routerModel, message);
			}
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	/**
	 * Parses logical routers data from message and puts it into routerModel.
	 * 
	 * @param routerModel
	 *            to store parsed data
	 * @param message
	 *            to parse logical routers data from
	 * @return routerModel updated with logical routers information from message.
	 * @throws IOException
	 * @throws SAXException
	 */
	private System parseLRs(System routerModel, String message)
			throws IOException, SAXException {

		// remove LR from the model before parsing new configuration
		routerModel.removeAllremoveManagedSystemElementByType(ComputerSystem.class);

		DigesterEngine listLogicalRoutersParser = new ListLogicalRoutersParser();
		listLogicalRoutersParser.init();
		listLogicalRoutersParser.configurableParse(new ByteArrayInputStream(message.getBytes()));

		// put new LR in the model
		for (String key : listLogicalRoutersParser.getMapElements().keySet()) {
			ComputerSystem system = new ComputerSystem();
			system.setName((String) listLogicalRoutersParser.getMapElements().get(key));
			routerModel.addManagedSystemElement(system);
		}

		return routerModel;
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
		// routerModel.removeAllLogicalDeviceByType(EthernetPort.class);

		routerModel.removeAllLogicalDeviceByType(NetworkPort.class);
		routerModel.removeAllLogicalDeviceByType(LogicalTunnelPort.class);
		routerModel.removeAllHostedServicesByType(GRETunnelService.class);

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
	public void prepareMessage() throws ActionException {

		validate();

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

	private void validate() throws ActionException {
		if (!checkTemplate(template)) {
			throw new ActionException("The path to Velocity template in Action " + getActionID() + " is null");
		}
		checkParams(params);
	}

	private boolean checkTemplate(String template) {

		boolean templateOK = true;
		// The template can not be null or empty
		if (template == null || template.equals("")) {
			templateOK = false;
		}

		return templateOK;

	}

	public boolean checkParams(Object params) {
		log.warn("Ignoring parametrs in action " + actionID);
		return true;
	}
}