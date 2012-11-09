package org.opennaas.extensions.bod.capability.l2bod;

import org.opennaas.extensions.network.model.topology.Link;

public class BoDLink extends Link {

	private RequestConnectionParameters	requestParameters;

	public RequestConnectionParameters getRequestParameters() {
		return requestParameters;
	}

	public void setRequestParameters(RequestConnectionParameters requestParameters) {
		this.requestParameters = requestParameters;
	}
}
