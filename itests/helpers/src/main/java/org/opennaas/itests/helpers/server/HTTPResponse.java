package org.opennaas.itests.helpers.server;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.jetty.http.HttpStatus;

/**
 * This object aims to be a simple subset of the {@link Response} class, containing only the necessary information for our http clients tests.
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
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

	/**
	 * 
	 * Returns the body message of the response.
	 * 
	 * @return
	 */
	public String getBodyMessage() {
		return bodyMessage;
	}

	/**
	 * 
	 * Sets the return's body message.
	 * 
	 * @param bodyMessage
	 */
	public void setBodyMessage(String bodyMessage) {
		this.bodyMessage = bodyMessage;
	}

	/**
	 * 
	 * Returns the body message content type. Possible values are defined by {@link MediaType} class.
	 * 
	 * @return
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * 
	 * Sets the body message content type. Possible values are defined by {@link MediaType} class.
	 * 
	 * @param contentType
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * 
	 * Sets the response status code. Valid values are defined by {@link HttpStatus} class.
	 * 
	 * @param sc
	 */
	public void setStatus(int sc)
	{
		this.status = sc;
	}

	/**
	 * 
	 * Returns the response status code. Possible values are defined by {@link HttpStatus} class.
	 * 
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * 
	 * Returns the http error to send as response to the request. Possible values can be found in {@link HttpServletResponse} class.
	 * 
	 * @return
	 */
	public String getHttpErrorToSend() {
		return httpErrorToSend;
	}

	/**
	 * 
	 * Sets the http error to be send as response to the request. Possible values can be found in {@link HttpServletResponse} class.
	 * 
	 * @param httpErrorToSend
	 */
	public void setHttpErrorToSend(String httpErrorToSend) {
		this.httpErrorToSend = httpErrorToSend;
	}

}
