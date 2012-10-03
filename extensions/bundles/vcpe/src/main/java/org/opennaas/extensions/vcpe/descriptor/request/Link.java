package org.opennaas.extensions.vcpe.descriptor.request;

public class Link extends RequestElement {

	private Interface	source;
	private Interface	sink;

	public Interface getSource() {
		return source;
	}

	public void setSource(Interface source) {
		this.source = source;
	}

	public Interface getSink() {
		return sink;
	}

	public void setSink(Interface sink) {
		this.sink = sink;
	}
}
