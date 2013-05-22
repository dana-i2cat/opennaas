package org.opennaas.extensions.network.model.virtual;

import org.opennaas.extensions.network.model.topology.Device;

public class VirtualDevice extends Device {

	private String	implementedBy;

	public VirtualDevice() {
		virtualizationService.setVirtualDevicesCapacity(0);
	}

	public String getImplementedBy() {
		return implementedBy;
	}

	public void setImplementedBy(String deviceName) {
		this.implementedBy = deviceName;
	}

}
