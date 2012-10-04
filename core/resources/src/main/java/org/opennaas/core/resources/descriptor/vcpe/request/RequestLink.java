package org.opennaas.core.resources.descriptor.vcpe.request;

import javax.xml.bind.annotation.XmlIDREF;

public class RequestLink extends RequestElement {

	@XmlIDREF
	private RequestInterface	source;

	@XmlIDREF
	private RequestInterface	sink;

	public RequestInterface getSource() {
		return source;
	}

	public void setSource(RequestInterface source) {
		this.source = source;
	}

	public RequestInterface getSink() {
		return sink;
	}

	public void setSink(RequestInterface sink) {
		this.sink = sink;
	}

}
