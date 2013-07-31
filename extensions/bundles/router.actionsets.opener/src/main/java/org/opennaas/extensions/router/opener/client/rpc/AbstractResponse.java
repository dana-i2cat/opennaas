package org.opennaas.extensions.router.opener.client.rpc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractResponse {
	
	@XmlElement(name="request-hook")
	protected String requestHook;
	
	@XmlElement(name="response")
	protected String response;
	
	@XmlElement(name="error")
	protected String error;

	public String getRequestHook() {
		return requestHook;
	}

	public void setRequestHook(String requestHook) {
		this.requestHook = requestHook;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
