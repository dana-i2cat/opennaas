/**
 * 
 */
package org.opennaas.web.bos;

import org.opennaas.core.resources.queue.QueueResponse;

import com.sun.jersey.api.client.ClientResponse;

/**
 * @author Jordi
 */
public class QueueBO extends GenericBO {

	private static final String	GET_ACTIONS_ID	= "router/lolaM20/queue/getActionsId";
	private static final String	EXECUTE			= "router/lolaM20/queue/execute";

	/**
	 * Get the actions of the Queue
	 */
	public String[] getActions() {
		String[] actionList = null;
		ClientResponse response = opennaasRest.get(getURL(GET_ACTIONS_ID));
		String retValue = response.getEntity(String.class);
		retValue = retValue.replace("[", "");
		retValue = retValue.replace("]", "");
		actionList = retValue.equals("") ? null : retValue.split(",");
		return actionList;
	}

	/**
	 * Execute the queue
	 */
	public void execute() {
		opennaasRest.post(getURL(EXECUTE), QueueResponse.class);
	}
}
