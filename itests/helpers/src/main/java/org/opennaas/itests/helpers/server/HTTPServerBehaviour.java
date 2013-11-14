package org.opennaas.itests.helpers.server;


public class HTTPServerBehaviour {

	private HTTPRequest		request;
	private HTTPResponse	response;

	public HTTPServerBehaviour() {

	}

	public HTTPServerBehaviour(HTTPRequest request, HTTPResponse desiredResponse) {
		this.request = request;
		this.response = desiredResponse;
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

}
