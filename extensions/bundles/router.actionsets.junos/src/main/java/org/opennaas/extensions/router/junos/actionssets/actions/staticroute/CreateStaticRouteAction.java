package org.opennaas.extensions.router.junos.actionssets.actions.staticroute;

/*
 * #%L
 * OpenNaaS :: Router :: Junos ActionSet
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.HashMap;
import java.util.Map;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.extensions.router.capabilities.api.model.staticroute.StaticRoute;
import org.opennaas.extensions.router.capability.staticroute.StaticRouteCapability;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.JunosAction;
import org.opennaas.extensions.router.junos.commandsets.commands.EditNetconfCommand;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EnabledLogicalElement.EnabledState;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;

/**
 * @author Jordi Puig
 * @author Julio Carlos Barrera
 */
public class CreateStaticRouteAction extends JunosAction {

	private static final String	VELOCITY_TEMPLATE_v4	= "/VM_files/createStaticRoute.vm";
	private static final String	VELOCITY_TEMPLATE_v6	= "/VM_files/createStaticRoutev6.vm";

	private static final String	PROTOCOL_NAME			= "netconf";

	/**
	 * 
	 */
	public CreateStaticRouteAction() {
		setActionID(ActionConstants.STATIC_ROUTE_CREATE);
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
		setTemplate();
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
			extraParams.put("elementName", elementName);

			// set extra params to notice values set
			StaticRoute staticRoute = (StaticRoute) params;
			extraParams.put("nextHopSet", staticRoute.getNextHopIpAddress() != null && !staticRoute.getNextHopIpAddress().isEmpty());
			extraParams.put("discardSet", staticRoute.isDiscard());
			extraParams.put("preferenceSet",
					staticRoute.getPreference() != null && staticRoute.getPreference() != StaticRouteCapability.PREFERENCE_DEFAULT_VALUE);

			setVelocityMessage(prepareVelocityCommand(params, template, extraParams));
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	private void setTemplate() {
		StaticRoute staticRoute = (StaticRoute) params;
		if (IPUtilsHelper.isIPv4ValidAddress(staticRoute.getNetIdIpAdress()))
			this.template = VELOCITY_TEMPLATE_v4;
		else
			this.template = VELOCITY_TEMPLATE_v6;

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
	 * Params must be an StaticRoute
	 * 
	 * @param params
	 *            it should be a StaticRoute
	 * @return false if params is null, is not an StaticRoute
	 * @throws ActionException
	 */
	@Override
	public boolean checkParams(Object params) throws ActionException {
		// First we check the params object
		if (params == null || !(params instanceof StaticRoute)) {
			throw new ActionException(this.actionID + ":  Invalid action, it should contain an object of instance StaticRoute");
		} else {
			StaticRoute staticRoute = (StaticRoute) params;

			String netIdIpAdress = staticRoute.getNetIdIpAdress();
			String nextHopIpAddress = staticRoute.getNextHopIpAddress();
			Boolean discard = staticRoute.isDiscard();

			if (netIdIpAdress == null || netIdIpAdress.isEmpty() || !IPUtilsHelper.isIPValidAddress(netIdIpAdress)) {
				throw new ActionException(this.actionID + ":  Invalid StaticRoute instance, it should contain a valid NetIdIpAdress field (CIDR)");
			} else if ((!validNextHopIpAddress(nextHopIpAddress) && !validDiscard(discard)) || (validNextHopIpAddress(nextHopIpAddress) && validDiscard(discard))) {
				throw new ActionException(
						this.actionID + ":  Invalid StaticRoute instance, it should contain either valid NextHopIpAddress (IP address) field or Discard flag set to true");
			}
		}
		return true;
	}

	private boolean validNextHopIpAddress(String nextHopIpAddress) {
		return (nextHopIpAddress != null && !nextHopIpAddress.isEmpty() && IPUtilsHelper.isIPWithoutMaskValidAddress(nextHopIpAddress));
	}

	private boolean validDiscard(Boolean discard) {
		return (discard != null && discard);
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