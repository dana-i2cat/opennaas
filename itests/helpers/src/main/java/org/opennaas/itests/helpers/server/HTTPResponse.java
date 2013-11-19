package org.opennaas.itests.helpers.server;

public class HTTPResponse {

	private int		status;
	private String	bodyMessage;
	private String	contentType;
	private String	httpErrorToSend;

	public HTTPResponse() {

	}

	public HTTPResponse(int status, String contentType, String bodyMessage, String httpErrorToSend) {
		this.status = status;
		this.contentType = contentType;
		this.bodyMessage = bodyMessage;
		this.httpErrorToSend = httpErrorToSend;
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

	public void setStatus(int sc)
	{
		this.status = sc;
	}

	public int getStatus() {
		return status;
	}

	public String getHttpErrorToSend() {
		return httpErrorToSend;
	}

	public void setHttpErrorToSend(String httpErrorToSend) {
		this.httpErrorToSend = httpErrorToSend;
	}

}
