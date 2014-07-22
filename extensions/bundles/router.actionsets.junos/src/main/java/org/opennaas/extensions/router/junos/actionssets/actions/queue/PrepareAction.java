package org.opennaas.extensions.router.junos.actionssets.actions.queue;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.queue.QueueConstants;
import org.opennaas.extensions.router.junos.actionssets.actions.JunosAction;
import org.opennaas.extensions.router.junos.commandsets.commands.DiscardNetconfCommand;
import org.opennaas.extensions.router.junos.commandsets.commands.LockNetconfCommand;

public class PrepareAction extends JunosAction {
	Log	log	= LogFactory.getLog(PrepareAction.class);

	public PrepareAction() {
		super();
		initialize();
	}

	protected void initialize() {
		this.setActionID(QueueConstants.PREPARE);
		// setTemplate("/VM_files/getconfiguration.vm");
		this.protocolName = "netconf";

	}

	@Override
	public void executeListCommand(ActionResponse actionResponse, IProtocolSession protocol) throws ActionException {
		try {
			/* lock commnad */

			/* discard changes */
			DiscardNetconfCommand discardCommand = new DiscardNetconfCommand();
			discardCommand.initialize();
			Response responsePrepare = sendCommandToProtocol(discardCommand, protocol);
			actionResponse.addResponse(responsePrepare);

			LockNetconfCommand lockCommand = new LockNetconfCommand("candidate");
			lockCommand.initialize();
			Response responseLock = sendCommandToProtocol(lockCommand, protocol);
			actionResponse.addResponse(responseLock);

			/* it can't be executed this workflow */
		} catch (Exception e) {
			throw new ActionException(this.actionID, e);
		}
		validateAction(actionResponse);
	}

	@Override
	public void parseResponse(Object responseMessage, Object model) {

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

	@Override
	public void prepareMessage() throws ActionException {

	}

}
