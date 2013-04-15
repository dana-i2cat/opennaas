package org.opennaas.extensions.router.junos.actionssets.actions.gretunnel;

import java.util.HashMap;
import java.util.Map;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.JunosAction;
import org.opennaas.extensions.router.junos.commandsets.commands.EditNetconfCommand;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.GRETunnelService;
import org.opennaas.extensions.router.model.Service;

/**
 * @author Jordi Puig
 */
public class DeleteTunnelAction extends JunosAction {

	private static final String	NAME_PATTERN		= "gr-(\\d{1}/\\d{1}/\\d*)";
	private static final String	PORT_PATTERN		= "\\d*";

	private static final String	VELOCITY_TEMPLATE	= "/VM_files/deleteTunnel.vm";
	private static final String	PROTOCOL_NAME		= "netconf";

	/**
	 * 
	 */
	public DeleteTunnelAction() {
		setActionID(ActionConstants.DELETETUNNEL);
		setTemplate(VELOCITY_TEMPLATE);
		this.protocolName = PROTOCOL_NAME;
	}

	/**
	 * Send the command to the protocol session
	 * 
	 * @param actionResponse
	 * @param protocol
	 * @throws ActionException
	 */
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

	/**
	 * Create the velocity template to send info to the Junos device
	 * 
	 * @throws ActionException
	 */
	@Override
	public void prepareMessage() throws ActionException {
		// validate input parameters
		validate();

		try {
			String elementName = "";
			if (((ComputerSystem) modelToUpdate).getElementName() != null) {
				// is logicalRouter, add LRName param
				elementName = ((ComputerSystem) modelToUpdate).getElementName();
			}

			Map<String, Object> extraParams = new HashMap<String, Object>();
			extraParams.put("portNumber", getPortNumber());
			extraParams.put("name", getName());
			extraParams.put("elementName", elementName);

			setVelocityMessage(prepareVelocityCommand(params, template, extraParams));
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	/**
	 * We do not have to do anything with the response
	 * 
	 * @param responseMessage
	 * @param model
	 * @throws ActionException
	 */
	@Override
	public void parseResponse(Object responseMessage, Object model) throws ActionException {
		// Nothing to do
	}

	/**
	 * Params must be a GRETunnelService
	 * 
	 * @param params
	 *            it should be a GRETunnelService
	 * @return false if params is null, not a GRETunnelService or name != pattern gre.[1..n]
	 * @throws ActionException
	 */
	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (params == null)
			throw new ActionException("Params can't be null for the " + getActionID() + " action.");
		if (!(params instanceof GRETunnelService))
			throw new ActionException(getActionID() + " only accept GRE Tunnel Services as params.");
		if (!checkPatternName(((GRETunnelService) params).getName()))
			throw new ActionException("The name of the GRE tunnel must have the following format -> gr-x/y/z{.a}");
		// TODO: this has to be check with the dirty model.
		// if (!checkExistsName(((GRETunnelService) params).getName()))
		// throw new ActionException("The name of the GRE tunnel does not exist in this router");
		return true;
	}

	/**
	 * @param template
	 * @throws ActionException
	 *             if template is null or empty
	 */
	private boolean checkTemplate(String template) throws ActionException {
		boolean templateOK = true;
		// The template can not be null or empty
		if (template == null || template.equals("")) {
			templateOK = false;
		}
		return templateOK;
	}

	/**
	 * Validate the template and the input parameters
	 * 
	 * @throws ActionException
	 */
	private void validate() throws ActionException {
		if (!checkTemplate(template))
			throw new ActionException("The path to Velocity template in Action " + getActionID() + " is null");
		if (!checkParams(params))
			throw new ActionException("Invalid parameters for action " + getActionID());
	}

	/**
	 * @return
	 */
	private String getPortNumber() {
		String[] name = ((GRETunnelService) params).getName().split("\\.");
		return name.length > 1 ? name[1] : "0";
	}

	/**
	 * @return
	 */
	private String getName() {
		String[] name = ((GRETunnelService) params).getName().split("\\.");
		return name.length > 1 ? name[0] : null;
	}

	/**
	 * Checks if the interfaceName follows the gr-/x/x/x{.x} pattern
	 * 
	 * @param interfaceName
	 * @return true if name follows the indicated pattern
	 */
	private boolean checkPatternName(String interfaceName) {
		String name = interfaceName.split("\\.")[0];
		String portNumber = null;
		if (name.contains("."))
			portNumber = interfaceName.split("\\.")[1];
		if (portNumber != null)
			return (name.matches(NAME_PATTERN) && (portNumber.matches(PORT_PATTERN)));
		else
			return (name.matches(NAME_PATTERN));
	}

	/**
	 * Check if name of the GRE Tunnel to delete exists
	 * 
	 * @param name
	 * @return true if exists, false otherwise
	 */
	private boolean checkExistsName(String name) {
		for (Service service : ((ComputerSystem) modelToUpdate).getHostedService()) {
			if (service instanceof GRETunnelService) {
				if (((GRETunnelService) service).getName().equals(name))
					return true;
			}
		}
		return false;
	}
}