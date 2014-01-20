package org.opennaas.extensions.roadm.wonesys.actionsets.actions.queue;

/*
 * #%L
 * OpenNaaS :: ROADM :: W-Onesys Actionset
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
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.QueueConstants;
import org.opennaas.extensions.roadm.wonesys.actionsets.actions.WonesysAction;
import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.extensions.roadm.wonesys.commandsets.commands.UnlockNodeCommand;

public class RestoreAction extends WonesysAction {

	static Log	log	= LogFactory.getLog(RestoreAction.class);

	public RestoreAction() {
		super();
		initialize();

	}

	protected void initialize() {
		this.setActionID(QueueConstants.RESTORE);
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {

		// unlock node
		try {
			/* get protocol */
			IProtocolSession protocol = protocolSessionManager.obtainSessionByProtocol("wonesys", false);

			WonesysCommand c = new UnlockNodeCommand();
			c.initialize();
			String response = (String) protocol.sendReceive(c.message());
			Response resp = checkResponse(c.message(), response);

			ActionResponse actionResponse = ActionResponse.okResponse(actionID);
			actionResponse.addResponse(resp);
			updateStatusFromResponses(actionResponse);
			actionResponse.setStatus(STATUS.ERROR);
			actionResponse.setInformation("Failed to rollback to a state previous to queue execution. Rollback mechanism is not supported.");

			return actionResponse;

		} catch (ProtocolException e) {
			throw new ActionException(e);
		}
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		log.warn("Given params are ignored");
		return true;
	}

}
