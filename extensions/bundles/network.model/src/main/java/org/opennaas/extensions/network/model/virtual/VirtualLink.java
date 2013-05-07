package org.opennaas.extensions.network.model.virtual;

import org.opennaas.extensions.network.model.topology.Link;

public class VirtualLink extends Link {

	private Link	implementedBy;

	public Link getImplementedBy() {
		return implementedBy;
	}

	public void setImplementedBy(Link link) {
		this.implementedBy = link;
	}

}
