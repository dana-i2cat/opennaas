package org.opennaas.core.resources.mock;

/*
 * #%L
 * OpenNaaS :: Core :: Resources
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;

public class MockAction extends Action {
	private Log				log				= LogFactory.getLog(MockAction.class);
	private ActionResponse	actionResponse	= new ActionResponse();

	boolean					executed		= false;

	@Override
	public void setActionID(String actionID) {
		this.actionID = actionID;
	}

	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
		log.info("----> Executing action: MOCK ACTION: " + actionID);
		actionResponse.setActionID(actionID);
		executed = true;
		return actionResponse;
	}

	public boolean checkParams(Object arg0) throws ActionException {
		// TODO Auto-generated method stub
		return false;
	}

	public void setActionResponse(ActionResponse actionResponse) {
		this.actionResponse = actionResponse;
	}

	// TODO probably promote to Action
	public boolean isExecuted()
	{
		return executed;
	}

	public ActionResponse getActionResponse() {
		return actionResponse;
	}

}
