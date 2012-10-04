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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RequestElement other = (RequestElement) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
