package org.opennaas.itests.core.resources;

/*
 * #%L
 * OpenNaaS :: iTests :: Core
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
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;

public class DummyAction extends Action {

	static Log	log	= LogFactory.getLog(DummyAction.class);

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {

		log.info("Executing DUMMY ACTION");
		ActionResponse actionResponse = new ActionResponse();
		actionResponse.setActionID(getActionID());

		Response response = Response.okResponse("DUMMY");
		actionResponse.addResponse(response);

		return actionResponse;
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		return true;
	}

}
