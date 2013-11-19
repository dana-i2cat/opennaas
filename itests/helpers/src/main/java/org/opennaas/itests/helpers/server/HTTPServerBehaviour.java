package org.opennaas.itests.helpers.server;

public class HTTPServerBehaviour {

	private HTTPRequest		request;
	private HTTPResponse	response;
	private boolean			consumible;

	public HTTPServerBehaviour() {

	}

	public HTTPServerBehaviour(HTTPRequest request, HTTPResponse desiredResponse, boolean consumible) {
		this.request = request;
		this.response = desiredResponse;
		this.consumible = consumible;
	}

	public HTTPRequest getRequest() {
		return request;
	}

	public void setRequest(HTTPRequest request) {
		this.request = request;
	}

	public HTTPResponse getResponse() {
		return response;
	}

	public void setResponse(HTTPResponse response) {
		this.response = response;
	}

	public boolean isConsumible() {
		return consumible;
	}

	public void setConsumible(boolean consumible) {
		this.consumible = consumible;
	}

}
