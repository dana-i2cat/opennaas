package org.opennaas.extensions.router.opener.actionssets.actions;

/*
 * #%L
 * OpenNaaS :: Router :: Opener ActionSet
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
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.router.opener.client.rpc.SetInterfaceResponse;
import org.opennaas.extensions.router.opener.protocol.OpenerProtocolSession;

public abstract class OpenerAction extends Action {

	protected OpenerProtocolSession getOpenerProtocolSession(IProtocolSessionManager protocolSessionManager) throws ProtocolException {
		return (OpenerProtocolSession) protocolSessionManager.obtainSessionByProtocol(
				OpenerProtocolSession.OPENER_PROTOCOL_TYPE, false);
	}

	protected ActionResponse actionResposeFromSetInterfaceResponse(SetInterfaceResponse openerResponse) {

		ActionResponse actionResponse = new ActionResponse();
		STATUS actionStatus;

		if (openerResponse.getError() != null) {

			actionResponse.setStatus(STATUS.ERROR);
			actionResponse.setInformation(openerResponse.getError());

		} else {

			if (openerResponse.getResponse() != null || openerResponse.getResponse().isEmpty()) {
				if (Integer.parseInt(openerResponse.getResponse()) >= 200 && Integer.parseInt(openerResponse.getResponse()) < 300) {
					actionStatus = STATUS.OK;
				} else {
					actionStatus = STATUS.ERROR;
				}
			} else {
				actionStatus = STATUS.ERROR;
			}
			actionResponse.setStatus(actionStatus);
		}

		return actionResponse;
	}

}
