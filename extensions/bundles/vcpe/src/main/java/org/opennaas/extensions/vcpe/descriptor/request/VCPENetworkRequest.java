package org.opennaas.extensions.vcpe.descriptor.request;

import java.util.List;

public class VCPENetworkRequest {

	private List<RequestElement>	elements;

	public List<RequestElement> getElements() {
		return elements;
	}

	public void setElements(List<RequestElement> elements) {
		this.elements = elements;
	}
}
