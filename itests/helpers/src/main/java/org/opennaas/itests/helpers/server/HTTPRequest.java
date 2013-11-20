package org.opennaas.itests.helpers.server;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;

/**
 * This object aims to be a simple subset of the {@link Request} class, containing only the necessary information for our http clients tests.
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
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

	/**
	 * 
	 * Returns the HTTP method of the request. Possible values are defined in {@link HttpMethod} class.
	 * 
	 * @return
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * Sets the HTTP method of the request. Valid values are defined in {@link HttpMethod} class.
	 * 
	 * @param method
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * 
	 * Returns the body message.
	 * 
	 * @return
	 */
	public String getBodyMessage() {
		return bodyMessage;
	}

	/**
	 * 
	 * Sets the request body message.
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
	 * @return
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}
