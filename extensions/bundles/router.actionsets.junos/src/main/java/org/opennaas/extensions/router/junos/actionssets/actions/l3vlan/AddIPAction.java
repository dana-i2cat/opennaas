package org.opennaas.extensions.router.junos.actionssets.actions.l3vlan;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.JunosAction;
import org.opennaas.extensions.router.junos.commandsets.commands.EditNetconfCommand;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;
import org.opennaas.extensions.router.model.utils.ModelHelper;

/**
 * Add IP to L3 VLAN action
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class AddIPAction extends JunosAction {

	Log	log	= LogFactory.getLog(AddIPAction.class);

	public AddIPAction() {
		super();
		initialize();
	}

	protected void initialize() {
		this.setActionID(ActionConstants.L3VLAN_ADD_IP_TO_DOMAIN);
		setTemplate("/VM_files/l3vlan/l3vlanAddIP.vm");
		this.protocolName = "netconf";

	}

	@Override
	public void executeListCommand(ActionResponse actionResponse, IProtocolSession protocol) throws ActionException {
		try {
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
		// empty
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (params == null || !(params instanceof String[]))
			throw new ActionException("Invalid parameters for action " + getActionID());

		if (((String[]) params)[0] == null || ((String[]) params)[0].isEmpty() || ((String[]) params)[1] == null || ((String[]) params)[1].isEmpty() || !IPUtilsHelper
				.isIPValidAddress(((String[]) params)[1]))
			throw new ActionException("Invalid parameters for action " + getActionID());

		return true;
	}

	@Override
	public void prepareMessage() throws ActionException {
		if (template == null || template.equals(""))
			throw new ActionException("The path to Velocity template in Action " + getActionID() + " is null");

		try {

			// check if it is a logicalRouter
			String elementName = "";
			if (((ComputerSystem) modelToUpdate).getElementName() != null) {
				elementName = ((ComputerSystem) modelToUpdate).getElementName();
			}

			// mark if it is an IPv6 address
			boolean isIPv6 = false;
			// IPUtilsHelper.isIPv6ValidAddress(...) not worrking properly
			if (!IPUtilsHelper.isIPv4ValidAddress(((String[]) params)[1])) {
				isIPv6 = true;
			}

			// assume BridgeDomain is in the model and obtain the vlan-id to be used as vlan interface unit
			int unit = ModelHelper.getBridgeDomainByName(ModelHelper.getBridgeDomains((ComputerSystem) modelToUpdate), ((String[]) params)[0])
					.getVlanId();

			// craft extraParams
			Map<String, Object> extraParams = new HashMap<String, Object>();
			extraParams.put("elementName", elementName);
			extraParams.put("isIPv6", isIPv6);
			extraParams.put("unit", unit);

			setVelocityMessage(prepareVelocityCommand(params, template, extraParams));
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}
}
