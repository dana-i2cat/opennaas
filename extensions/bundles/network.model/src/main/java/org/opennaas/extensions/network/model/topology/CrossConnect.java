package org.opennaas.extensions.network.model.topology;


/**
 * A (collection of) network element(s) that can be represented as a subnetwork connection (ITU-T G.805 terminology). A cross connect is an internal data transport within a device or domain, unlike Links which transport data between two devices or domains.
 *
 * @author isart
 *
 */
public class CrossConnect extends NetworkConnection {

	Interface source;
	Interface sink;

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
