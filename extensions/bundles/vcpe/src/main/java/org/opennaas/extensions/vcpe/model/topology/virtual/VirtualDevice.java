package org.opennaas.extensions.vcpe.model.topology.virtual;

import org.opennaas.extensions.network.model.topology.Device;

public class VirtualDevice extends Device {

	private Device	implementedBy;

	public Device getImplementedBy() {
		return implementedBy;
	}

	public void setImplementedBy(Device implementedBy) {
		this.implementedBy = implementedBy;
	}

}
