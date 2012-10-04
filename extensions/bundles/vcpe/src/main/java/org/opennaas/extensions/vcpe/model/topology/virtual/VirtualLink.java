package org.opennaas.extensions.vcpe.model.topology.virtual;

import org.opennaas.extensions.network.model.topology.Link;
import org.opennaas.extensions.network.model.topology.NetworkConnection;

public class VirtualLink extends Link {

	private NetworkConnection	implementedBy;

	public NetworkConnection getImplementedBy() {
		return implementedBy;
	}

	public void setImplementedBy(NetworkConnection implementedBy) {
		this.implementedBy = implementedBy;
	}

}
