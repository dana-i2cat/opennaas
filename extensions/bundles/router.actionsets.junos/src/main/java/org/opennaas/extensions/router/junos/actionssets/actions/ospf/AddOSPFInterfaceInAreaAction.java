package org.opennaas.extensions.router.junos.actionssets.actions.ospf;

import java.util.HashMap;
import java.util.Map;

import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.JunosAction;
import org.opennaas.extensions.router.junos.commandsets.commands.EditNetconfCommand;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EnabledLogicalElement.EnabledState;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;
import org.opennaas.extensions.router.model.OSPFArea;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSession;

/**
 * @author Jordi Puig
 */
public class AddOSPFInterfaceInAreaAction extends JunosAction {

	private static final String	VELOCITY_TEMPLATE	= "/VM_files/ospfAddInterfaceInArea.vm";

	private static final String	PROTOCOL_NAME		= "netconf";

	/**
	 * 
	 */
	public AddOSPFInterfaceInAreaAction() {
		setActionID(ActionConstants.OSPF_ADD_INTERFACE_IN_AREA);
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
			extraParams.put("disabledState", EnabledState.DISABLED.toString());
			extraParams.put("enableState", EnabledState.ENABLED.toString());
			extraParams.put("ipUtilsHelper", IPUtilsHelper.class);
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
	 * Params must be a OSPFArea
	 * 
	 * @param params
	 *            it should be a OSPFArea
	 * @return false if params is null or is not a OSPFArea
	 */
	@Override
	public boolean checkParams(Object params) {

		boolean paramsOK = true;
		// First we check the params object
		if (params == null || !(params instanceof OSPFArea)) {
			paramsOK = false;
		}

		return paramsOK;
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
	 * @throws ActionException
	 */
	private void validate() throws ActionException {

		if (!checkTemplate(template)) {
			throw new ActionException("The path to Velocity template in Action " + getActionID() + " is null");
		}

		// Check the params
		if (!checkParams(params)) {
			throw new ActionException("Invalid parameters for action " + getActionID());
		}
	}
}