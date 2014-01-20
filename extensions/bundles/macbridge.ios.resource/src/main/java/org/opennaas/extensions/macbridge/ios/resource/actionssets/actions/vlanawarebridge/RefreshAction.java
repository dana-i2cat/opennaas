package org.opennaas.extensions.macbridge.ios.resource.actionssets.actions.vlanawarebridge;

/*
 * #%L
 * OpenNaaS :: MAC Bridge :: IOS Resource
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

import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.capability.macbridge.model.MACBridge;
import org.opennaas.extensions.macbridge.ios.resource.commandsets.commands.IOSCommand;
import org.opennaas.extensions.macbridge.ios.resource.commandsets.commands.ShowInterfacesStatusCommand;
import org.opennaas.extensions.macbridge.ios.resource.commandsets.commands.ShowVLANCommand;
import org.opennaas.extensions.macbridge.ios.resource.commandsets.commands.TerminalLengthCommand;
import org.opennaas.extensions.protocols.cli.message.CLIResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Eduard Grasa
 */
public class RefreshAction extends Action {

	public static final String	REFRESH_ACTION	= "RefreshAction";

	/** CLI Session Log */
	static private Logger		logger			= LoggerFactory.getLogger(RefreshAction.class);

	/**
	 * 
	 */
	public RefreshAction() {
		super();
		this.setActionID(REFRESH_ACTION);
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		return true;
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
		IOSCommand command = null;
		MACBridge macBridgeModel = (MACBridge) this.getModelToUpdate();

		try {
			logger.debug("Executing Refresh action");
			IProtocolSession protocol = protocolSessionManager.obtainSessionByProtocol("cli", false);

			logger.debug("Connecting to device");
			protocol.connect();

			// Login and enter enable mode
			logger.debug("Setting terminal length");
			command = new TerminalLengthCommand();
			protocol.sendReceive(command.getCommand());
			logger.debug("Retrieving interfaces status");
			command = new ShowInterfacesStatusCommand();
			CLIResponseMessage message = (CLIResponseMessage) protocol.sendReceive(command.getCommand());
			command.updateModel(message, macBridgeModel);
			command = new ShowVLANCommand();
			message = (CLIResponseMessage) protocol.sendReceive(command.getCommand());
			command.updateModel(message, macBridgeModel);

			protocol.disconnect();
		} catch (ProtocolException ex) {
			throw new ActionException(ex);
		}

		return ActionResponse.okResponse(this.getActionID());
	}

}