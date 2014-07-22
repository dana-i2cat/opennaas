package org.opennaas.extensions.router.junos.actionssets.actions.vlanbridge;

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

import org.apache.commons.lang.StringUtils;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.JunosAction;
import org.opennaas.extensions.router.junos.commandsets.commands.EditNetconfCommand;
import org.opennaas.extensions.router.model.BridgeDomain;
import org.opennaas.extensions.router.model.ComputerSystem;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class CreateBridgeDomainAction extends JunosAction {

	private static final String	VELOCITY_TEMPLATE	= "/VM_files/vlanBridge/bridgeDomainCreate.vm";
	private static final String	PROTOCOL_NAME		= "netconf";

	public CreateBridgeDomainAction() {
		setActionID(ActionConstants.VLAN_BRIDGE_CREATE_BRIDGE_DOMAIN);
		setTemplate(VELOCITY_TEMPLATE);
		this.protocolName = PROTOCOL_NAME;
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {

		if (params == null)
			throw new ActionException("Invalid Params in action : " + this.actionID + ". Param can not be null ");
		if (!(params instanceof BridgeDomain))
			throw new ActionException("Invalid Params in action : " + this.actionID + ". Param should be instance of BridgeDomain class");

		BridgeDomain brDomain = (BridgeDomain) params;

		if (StringUtils.isEmpty(brDomain.getElementName()))
			throw new ActionException("Invalid Params in action : " + this.actionID + ". Param should contain ElementName.");

		return true;
	}

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

	@Override
	public void parseResponse(Object responseMessage, Object model) throws ActionException {

	}

	@Override
	public void prepareMessage() throws ActionException {
		try {
			String elementName = "";
			if (((ComputerSystem) modelToUpdate).getElementName() != null) {
				// is logicalRouter, add LRName param
				elementName = ((ComputerSystem) modelToUpdate).getElementName();
			}
			Map<String, Object> extraParams = new HashMap<String, Object>();

			extraParams.put("elementName", elementName);

			setVelocityMessage(prepareVelocityCommand(params, template, extraParams));

		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

}
