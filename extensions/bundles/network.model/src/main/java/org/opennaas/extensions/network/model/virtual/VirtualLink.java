package org.opennaas.extensions.network.model.virtual;

import org.opennaas.extensions.network.model.topology.Link;

public class VirtualLink extends Link {

	private String	implementedBy;

	public String getImplementedBy() {
		return implementedBy;
	}

	public void setImplementedBy(String linkName) {
		this.implementedBy = linkName;
	}

}
