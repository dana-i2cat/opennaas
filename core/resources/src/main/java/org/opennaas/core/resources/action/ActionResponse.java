package org.opennaas.core.resources.action;

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
