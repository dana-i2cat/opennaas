package net.i2cat.nexus.resources.action;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.nexus.resources.command.Response;

public class ActionResponse {
	public enum STATUS {
		ERROR, OK, PENDING
	};

	private STATUS			status		= STATUS.PENDING;
	private String			actionID;
	private List<Response>	responses	= new ArrayList<Response>();

	public String getActionID() {
		return actionID;
	}

	public void setActionID(String actionID) {
		this.actionID = actionID;
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

	public static ActionResponse newOkAction(String actionID) {
		ActionResponse actionResponse = new ActionResponse();
		actionResponse.setActionID(actionID);
		actionResponse.setStatus(STATUS.OK);
		return actionResponse;
	}

	// public static ActionResponse configureAction(List<Response> responses) {
	// ActionResponse actionResponse = new ActionResponse();
	// for (Response response: responses) {
	// response.getErrors()
	//
	// }
	// }

}
