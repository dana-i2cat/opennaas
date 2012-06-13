package org.opennaas.extensions.router.junos.actionssets.actions.gretunnel;

import java.util.HashMap;
import java.util.Map;

import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.JunosAction;
import org.opennaas.extensions.router.junos.commandsets.commands.EditNetconfCommand;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.GRETunnelService;
import org.opennaas.extensions.router.model.ManagedElement;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSession;

public class CreateTunnelAction extends JunosAction {

	private static final String	NAME_PATTERN	= "gr-(\\d{1}/\\d{1}/\\d*)";
	private static final String	PORT_PATTERN	= "\\d*";
	private final Log			log				= LogFactory.getLog(CreateTunnelAction.class);

	public CreateTunnelAction() {
		super();
		initialize();
	}

	public void initialize() {
		this.setActionID(ActionConstants.CREATETUNNEL);
		setTemplate("/VM_files/createTunnel.vm");
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
		// Nothing to do
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (params == null)
			throw new ActionException("Params can't be null for the " + getActionID() + " action.");
		if (!(params instanceof GRETunnelService))
			throw new ActionException(getActionID() + " only accept GRE Tunnel Services as params.");
		if (!checkNamePattern(((GRETunnelService) params).getName()))
			throw new ActionException("The name of the GRE tunnel must have the following format -> gre.[1..n]");
		return true;
	}

	@Override
	public void prepareMessage() throws ActionException {

		checkParams(params);
		try {
			IPUtilsHelper ipUtilsHelper = new IPUtilsHelper();
			Map<String, Object> extraParams = new HashMap<String, Object>();
			extraParams.put("ipUtilsHelper", ipUtilsHelper);

			String portNumber = null;
			String name = ((GRETunnelService) params).getName();

			if (name.contains("."))
				portNumber = name.split("\\.")[1];
			else
				portNumber = "0";
			name = name.split("\\.")[0];

			((GRETunnelService) params).setName(name);
			extraParams.put("portNumber", portNumber);

			if (((ComputerSystem) modelToUpdate).getElementName() != null) {
				// is logicalRouter, add LRName param
				((ManagedElement) params).setElementName(((ComputerSystem) modelToUpdate).getElementName());
				// TODO If we don't have a ManagedElement initialized
			} else if (params != null && params instanceof ManagedElement && ((ManagedElement) params).getElementName() == null) {
				((ManagedElement) params).setElementName("");

			}
			setVelocityMessage(prepareVelocityCommand(params, template, extraParams));
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	/**
	 * Checks if the interfaceName follows the gr-/x/x/x{.x} pattern
	 * 
	 * @param interfaceName
	 * @return true if name follows the indicated pattern
	 */
	private boolean checkNamePattern(String interfaceName) {
		String name = interfaceName.split("\\.")[0];
		String portNumber = null;
		if (name.contains("."))
			portNumber = interfaceName.split("\\.")[1];
		if (portNumber != null)
			return (name.matches(NAME_PATTERN) && (portNumber.matches(PORT_PATTERN)));
		else
			return (name.matches(NAME_PATTERN));
	}
}