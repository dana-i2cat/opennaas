package org.opennaas.extensions.bod.capability.l2bod.ws.wrapper;

import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.bod.capability.l2bod.RequestConnectionParameters;

@XmlRootElement
public class RequestConnectionRequest {

	private RequestConnectionParameters	parameters;

	/**
	 * @return the parameters
	 */
	public RequestConnectionParameters getParameters() {
		return parameters;
	}

	/**
	 * @param parameters
	 *            the parameters to set
	 */
	public void setParameters(RequestConnectionParameters parameters) {
		this.parameters = parameters;
	}

}
