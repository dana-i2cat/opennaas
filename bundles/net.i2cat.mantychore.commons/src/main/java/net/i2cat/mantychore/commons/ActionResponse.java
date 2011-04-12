package net.i2cat.mantychore.commons;

import java.util.ArrayList;
import java.util.List;

public class ActionResponse {
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

}
