package org.opennaas.core.resources.action;

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

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.command.Response;

public class ActionResponse {

	public enum STATUS {
		ERROR, OK, PENDING
	};

	private STATUS			status		= STATUS.PENDING;
	private String			actionID;
	private String			information;
	private List<Response>	responses	= new ArrayList<Response>();
	private Object			result;

	public String getActionID() {
		return actionID;
	}

	public void setActionID(String actionID) {
		this.actionID = actionID;
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

	public List<Response> getResponses() {
		return responses;
	}

	public void setResponses(List<Response> responses) {
		this.responses = responses;
	}

	public void addResponse(Response response) {
		responses.add(response);
	}

	public STATUS getStatus() {
		return status;
	}

	public void setStatus(STATUS status) {
		this.status = status;
	}

	/**
	 * 
	 * @return the result of the action. It should be null when getStatus() is not OK. It may be null even when getStatus() is OK.
	 */
	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public static ActionResponse newPendingAction(String actionID) {
		ActionResponse actionResponse = new ActionResponse();
		actionResponse.setActionID(actionID);
		actionResponse.setStatus(STATUS.PENDING);
		return actionResponse;
	}

	@Deprecated
	public static ActionResponse newOkAction(String actionID) {
		ActionResponse actionResponse = new ActionResponse();
		actionResponse.setActionID(actionID);
		actionResponse.setStatus(STATUS.OK);
		return actionResponse;
	}

	public static ActionResponse okResponse(String actionID) {
		ActionResponse actionResponse = new ActionResponse();
		actionResponse.setActionID(actionID);
		actionResponse.setStatus(STATUS.OK);
		return actionResponse;
	}

	public static ActionResponse okResponse(String actionID, String information) {
		ActionResponse response = okResponse(actionID);
		response.setInformation(information);
		return response;
	}

	public static ActionResponse okResponse(String actionID, String information, List<Response> responses) {
		ActionResponse response = okResponse(actionID);
		response.setInformation(information);
		response.setResponses(responses);
		return response;
	}

	public static ActionResponse errorResponse(String actionID) {
		ActionResponse response = new ActionResponse();
		response.setActionID(actionID);
		response.setStatus(STATUS.ERROR);
		return response;
	}

	public static ActionResponse errorResponse(String actionID, String information) {
		ActionResponse response = errorResponse(actionID);
		response.setInformation(information);
		return response;
	}

	public static ActionResponse errorResponse(String actionID, String information, List<Response> responses) {
		ActionResponse response = errorResponse(actionID);
		response.setInformation(information);
		response.setResponses(responses);
		return response;
	}

}
