/**
 * 
 */
package org.opennaas.web.bos;

import java.util.Arrays;
import java.util.List;

import org.opennaas.core.resources.queue.QueueResponse;

/**
 * @author Jordi
 */
public class QueueBO extends GenericBO {

	/**
	 * Get the actions of the Queue
	 */
	public List<String> getActions() {
		String path = "router/lolaM20/queue/getActionsId";
		List<String> actionList = null;
		Object response = opennaasRest.get(getURL(path), String.class);
		if (response instanceof String) {
			String retValue = (String) response;
			actionList = Arrays.asList(retValue);
		}
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
