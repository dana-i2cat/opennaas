package net.i2cat.nexus.resources.queue;

import java.util.List;
import java.util.Vector;

import net.i2cat.nexus.resources.action.ActionResponse;
import net.i2cat.nexus.resources.action.IAction;

public class QueueResponse {

	public ActionResponse	prepareResponse	= new ActionResponse();

	public ActionResponse	confirmResponse	= new ActionResponse();

	public ActionResponse	restoreResponse	= new ActionResponse();

	Vector<ActionResponse>	responses		= new Vector<ActionResponse>();

	public String			user;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public int	timestamp;

	public ActionResponse getPrepareResponse() {
		return prepareResponse;
	}

	public void setPrepareResponse(ActionResponse prepareResponse) {
		this.prepareResponse = prepareResponse;
	}

	public ActionResponse getConfirmResponse() {
		return confirmResponse;
	}

	public void setConfirmResponse(ActionResponse confirmResponse) {
		this.confirmResponse = confirmResponse;
	}

	public Vector<ActionResponse> getResponses() {
		return responses;
	}

	public void setResponses(Vector<ActionResponse> responses) {
		this.responses = responses;
	}

	public static QueueResponse newQueueResponse(List<IAction> actions) {
		QueueResponse queueResponse = new QueueResponse();
		queueResponse.setPrepareResponse(ActionResponse.newPendingAction(QueueConstants.PREPARE));
		queueResponse.setConfirmResponse(ActionResponse.newPendingAction(QueueConstants.CONFIRM));
		queueResponse.setRestoreResponse(ActionResponse.newPendingAction(QueueConstants.RESTORE));

		Vector<ActionResponse> responses = new Vector<ActionResponse>();
		for (IAction action : actions)
			responses.add(ActionResponse.newPendingAction(action.getActionID()));
		queueResponse.setResponses(responses);

		return queueResponse;

	}

	public ActionResponse getRestoreResponse() {
		return restoreResponse;
	}

	public void setRestoreResponse(ActionResponse restoreResponse) {
		this.restoreResponse = restoreResponse;
	}

	/* status connection */
	// TODO ADD necessary parametes which we will need

}
