package org.opennaas.itests.helpers.server;

import org.eclipse.jetty.http.HttpStatus;

public class HTTPResponse {

	private int		status;
	private String	bodyMessage;
	private String	contentType;
	private String	httpErrorToSend;

	public HTTPResponse() {
		status = HttpStatus.OK_200;
		contentType = "text/html";
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
