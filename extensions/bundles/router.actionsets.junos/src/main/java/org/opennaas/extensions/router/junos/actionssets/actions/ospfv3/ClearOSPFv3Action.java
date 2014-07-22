package org.opennaas.extensions.router.junos.actionssets.actions.ospfv3;

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

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.JunosAction;
import org.opennaas.extensions.router.junos.commandsets.commands.CommandNetconfConstants;
import org.opennaas.extensions.router.junos.commandsets.commands.EditNetconfCommand;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.ManagedElement;

public class ClearOSPFv3Action extends JunosAction {

	public ClearOSPFv3Action() {
		super();
		initialize();
	}

	/**
	 * Initialize protocolName, ActionId and velocity template
	 */
	protected void initialize() {
		this.setActionID(ActionConstants.OSPFv3_CLEAR);
		setTemplate("/VM_files/ospfv3Clear.vm");
		this.protocolName = "netconf";
	}

	@Override
	public void executeListCommand(ActionResponse actionResponse, IProtocolSession protocol) throws ActionException {
		try {
			// when removing tags, none operation should be used as default
			EditNetconfCommand command = new EditNetconfCommand(getVelocityMessage(), CommandNetconfConstants.NONE_OPERATION);
			command.initialize();
			actionResponse.addResponse(sendCommandToProtocol(command, protocol));
		} catch (Exception e) {
			throw new ActionException(this.actionID + ": " + e.getMessage(), e);
		}
		validateAction(actionResponse);
	}

	@Override
	public void parseResponse(Object responseMessage, Object model) throws ActionException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (params != null)
			log.warn("Ingoring parameters in action " + getActionID());

		return true;
	}

	@Override
	public void prepareMessage() throws ActionException {
		if (template == null || template.equals(""))
			throw new ActionException("The path to Velocity template in Action " + getActionID() + " is null");

		if (!checkParams(params)) {
			throw new ActionException("Invalid parameters in action " + getActionID() + ".");
		}

		// tell velocity if element is a logical router or a physical one
		if (((ComputerSystem) modelToUpdate).getElementName() != null) {
			// is logicalRouter, add LRName param
			((ManagedElement) params).setElementName(((ComputerSystem) modelToUpdate).getElementName());
		} else if (((ManagedElement) params).getElementName() == null) {
			// avoid accessing null value when processing velocity template.
			((ManagedElement) params).setElementName("");
		}

		try {
			setVelocityMessage(prepareVelocityCommand(params, template));
		} catch (Exception e) {
			throw new ActionException(e);
		}

	}

}
