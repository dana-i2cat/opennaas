package org.opennaas.itests.helpers.server;

/**
 * 
 * Set of request and response pair for a server.
 * 
 * HTTP server will contain a list of behaviors. The behaviors define which requests the server is expecting, and which answers will be send for the
 * given request. Behaviours can be either consumable or not, which means, they can be removed when they match a user request.
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class HTTPServerBehaviour {

	private HTTPRequest		request;
	private HTTPResponse	response;
	private boolean			consumable;

	public HTTPServerBehaviour() {

	}

	public HTTPServerBehaviour(HTTPRequest request, HTTPResponse desiredResponse, boolean consumable) {
		this.request = request;
		this.response = desiredResponse;
		this.consumable = consumable;
	}

	/**
	 * Returns the request that identifies the behavior.
	 * 
	 * @return
	 */
	public HTTPRequest getRequest() {
		return request;
	}

	/**
	 * Sets the request that will identify the behavior, in order to return the defined response.
	 * 
	 * @param request
	 */
	public void setRequest(HTTPRequest request) {
		this.request = request;
	}

	/**
	 * 
	 * Returns the response associated to the request of this behavior.
	 * 
	 * @return
	 */
	public HTTPResponse getResponse() {
		return response;
	}

	/**
	 * 
	 * Sets the response associated to the request of this behavior.
	 * 
	 * @param response
	 */
	public void setResponse(HTTPResponse response) {
		this.response = response;
	}

	/**
	 * 
	 * Checks either if the behavior can be removed after a request matches it or not.
	 * 
	 * @return
	 */
	public boolean isConsumable() {
		return consumable;
	}

	/**
	 * 
	 * Sets either if the behavior can be removed after a request matches it or not.
	 * 
	 * @return
	 */
	public void setConsumable(boolean consumable) {
		this.consumable = consumable;
	}

}
