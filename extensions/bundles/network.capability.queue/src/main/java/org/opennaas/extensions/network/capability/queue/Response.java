/**
 * 
 */
package org.opennaas.extensions.network.capability.queue;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.opennaas.core.resources.queue.QueueResponse;

/**
 * @author Jordi
 * 
 */
@XmlRootElement
@XmlType(name = "NetQueueResponse")
public class Response {

	private Map<String, QueueResponse>	response;

	/**
	 * @return the response
	 */
	public Map<String, QueueResponse> getResponse() {
		return response;
	}

	/**
	 * @param response
	 *            the response to set
	 */
	public void setResponse(Map<String, QueueResponse> response) {
		this.response = response;
	}

}
