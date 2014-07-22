package org.opennaas.extensions.router.junos.actionssets.actions.ospf;

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
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.JunosAction;
import org.opennaas.extensions.router.junos.commandsets.commands.CommandNetconfConstants;
import org.opennaas.extensions.router.junos.commandsets.commands.EditNetconfCommand;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EnabledLogicalElement.EnabledState;
import org.opennaas.extensions.router.model.ManagedElement;
import org.opennaas.extensions.router.model.OSPFService;
import org.opennaas.extensions.router.model.RouteCalculationService.AlgorithmType;

/**
 * This action is responsible of configuring enable/disable OSPF status in a Junos 10 router.
 * 
 * It takes an OSPFService as a parameter and retrieves desired status from it. It uses netconf to change router configuration.
 * 
 * @author Isart Canyameres
 */
public class ConfigureOSPFStatusAction extends JunosAction {

	public ConfigureOSPFStatusAction() {
		super();
		initialize();
	}

	/**
	 * Initialize protocolName, ActionId and velocity template
	 */
	protected void initialize() {
		this.setActionID(ActionConstants.OSPF_ACTIVATE + "/" + ActionConstants.OSPF_DEACTIVATE);
		setTemplate("/VM_files/ospfConfigureStatus.vm");
		this.protocolName = "netconf";
	}

	@Override
	public void executeListCommand(ActionResponse actionResponse, IProtocolSession protocol) throws ActionException {
		try {
			EditNetconfCommand command;

			if (((OSPFService) params).getEnabledState().equals(EnabledState.ENABLED)) {
				// to remove disabled tag the none operation should be used as default
				command = new EditNetconfCommand(getVelocityMessage(), CommandNetconfConstants.NONE_OPERATION);
			} else {
				command = new EditNetconfCommand(getVelocityMessage());
			}
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
		// TODO Auto-generated method stub

	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (!(params instanceof OSPFService))
			return false;

		OSPFService ospfService = (OSPFService) params;

		if (ospfService.getAlgorithmType() == null || ospfService.equals(AlgorithmType.OSPFV2))
			return false;

		if (ospfService.getEnabledState().equals(EnabledState.ENABLED) ||
				(ospfService.getEnabledState().equals(EnabledState.DISABLED)))
			return true;

		return false;
	}

	@Override
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

			Map<String, Object> extraParams = new HashMap<String, Object>();
			extraParams.put("disabledState", EnabledState.DISABLED.toString());
			extraParams.put("enabledState", EnabledState.ENABLED.toString());

			setVelocityMessage(prepareVelocityCommand(velocityParams, template, extraParams));
		} catch (Exception e) {
			throw new ActionException(e);
		}

	}

}
