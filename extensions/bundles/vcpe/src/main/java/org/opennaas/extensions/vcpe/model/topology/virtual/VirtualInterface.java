package org.opennaas.extensions.vcpe.model.topology.virtual;

import org.opennaas.extensions.network.model.topology.ConnectionPoint;
import org.opennaas.extensions.network.model.topology.Interface;

public class VirtualInterface extends Interface {

	private ConnectionPoint	implementedBy;

	public ConnectionPoint getImplementedBy() {
		return implementedBy;
	}

	public void setImplementedBy(ConnectionPoint implementedBy) {
		this.implementedBy = implementedBy;
	}

}
