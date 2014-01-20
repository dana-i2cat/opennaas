package org.opennaas.extensions.router.junos.actionssets.actions.logicalrouters;

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

import java.io.ByteArrayInputStream;

import net.i2cat.netconf.rpc.Reply;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.CommandException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.extensions.router.junos.actionssets.ActionConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.JunosAction;
import org.opennaas.extensions.router.junos.commandsets.commands.GetNetconfCommand;
import org.opennaas.extensions.router.junos.commandsets.digester.DigesterEngine;
import org.opennaas.extensions.router.junos.commandsets.digester.ListLogicalRoutersParser;
import org.opennaas.extensions.router.model.ComputerSystem;

public class ListLogicalRoutersAction extends JunosAction {
	Log	logger	= LogFactory.getLog(ListLogicalRoutersAction.class);

	public ListLogicalRoutersAction() {
		super();
		initialize();

	}

	protected void initialize() {
		this.setActionID(ActionConstants.GETLOGICALROUTERS);
		setTemplate("/VM_files/getLogicalRouters.vm");
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
		String message;
		try {
			org.opennaas.extensions.router.model.System routerModel = (org.opennaas.extensions.router.model.System) model;
			DigesterEngine listLogicalRoutersParser = new ListLogicalRoutersParser();
			// DigesterEngine logicalInterfParser = new IPConfigurationInterfaceParser();
			listLogicalRoutersParser.init();

			/* getting interface information */
			if (responseMessage instanceof Reply) {
				Reply rpcReply = (Reply) responseMessage;
				message = rpcReply.getContain();
			} else {
				throw new CommandException("Error parsing response: the response is not a Reply message");
			}

			listLogicalRoutersParser.configurableParse(new ByteArrayInputStream(message.getBytes()));

			for (Object idLogicalRouter : listLogicalRoutersParser.getMapElements().values()) {
				ComputerSystem system = new ComputerSystem();
				system.setName((String) idLogicalRouter);
				routerModel.addSystem(system);
			}

		} catch (Exception e) {
			throw new ActionException(e);
		}

	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		// For this Action params are not allowed
		if (params == null)
			return true;
		else {
			throw new ActionException("The Action " + getActionID() + " don't accept params");
		}
	}

	public void prepareMessage() throws ActionException {
		if (template == null || template.equals(""))
			throw new ActionException("The path to Velocity template in Action " + getActionID() + " is null");
		checkParams(params);
		try {
			setVelocityMessage(prepareVelocityCommand(params, template));
		} catch (Exception e) {
			throw new ActionException();
		}

	}

}
