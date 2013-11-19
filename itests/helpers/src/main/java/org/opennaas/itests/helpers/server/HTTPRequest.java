package org.opennaas.itests.helpers.server;


public class HTTPRequest {

	private String	requestURL;

	private String	method;
	private String	bodyMessage;
	private String	contentType;

	public HTTPRequest() {

	}

	public HTTPRequest(String requestURL, String method, String contentType, String bodyMessage) {
		this.requestURL = requestURL;
		this.method = method;
		this.contentType = contentType;
		this.bodyMessage = bodyMessage;
	}

	public String getRequestURL() {
		return requestURL;
	}

	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getBodyMessage() {
		return bodyMessage;
	}

	public void setBodyMessage(String bodyMessage) {
		this.bodyMessage = bodyMessage;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}
