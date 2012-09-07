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

	/**
	 * Get the actions of the Queue
	 */
	public String[] getActions() {
		String path = "router/lolaM20/queue/getActionsId";
		String[] actionList = null;
		ClientResponse response = opennaasRest.get(getURL(path));
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
		String path = "router/lolaM20/queue/execute";
		opennaasRest.post(getURL(path), QueueResponse.class);
	}
}
