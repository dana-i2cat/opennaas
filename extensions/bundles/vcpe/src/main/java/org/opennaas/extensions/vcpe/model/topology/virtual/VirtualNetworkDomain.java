package org.opennaas.extensions.vcpe.model.topology.virtual;

import org.opennaas.extensions.network.model.domain.NetworkDomain;

public class VirtualNetworkDomain extends NetworkDomain {

	private NetworkDomain	implementedBy;

	public NetworkDomain getImplementedBy() {
		return implementedBy;
	}

	public void setImplementedBy(NetworkDomain implementedBy) {
		this.implementedBy = implementedBy;
	}

}
