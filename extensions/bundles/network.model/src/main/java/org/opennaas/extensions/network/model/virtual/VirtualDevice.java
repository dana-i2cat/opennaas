package org.opennaas.extensions.network.model.virtual;

import org.opennaas.extensions.network.model.topology.Device;

public class VirtualDevice extends Device {

	private Device	implementedBy;

	public VirtualDevice() {
		virtualizationService.setVirtualDevicesCapacity(0);
	}

	public Device getImplementedBy() {
		return implementedBy;
	}

	public void setImplementedBy(Device implementedBy) {
		this.implementedBy = implementedBy;
	}

}
