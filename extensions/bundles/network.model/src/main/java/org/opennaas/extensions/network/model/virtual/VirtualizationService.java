package org.opennaas.extensions.network.model.virtual;

import java.util.List;

public class VirtualizationService {

	private int					virtualDevicesCapacity;

	private List<VirtualDevice>	virtualDevices;

	public VirtualizationService() {
	}

	public int getVirtualDevicesCapacity() {
		return virtualDevicesCapacity;
	}

	public void setVirtualDevicesCapacity(int virtualDevicesCapacity) {
		this.virtualDevicesCapacity = virtualDevicesCapacity;
	}

	public List<VirtualDevice> getVirtualDevices() {
		return virtualDevices;
	}

	public void setVirtualDevices(List<VirtualDevice> virtualDevices) {
		this.virtualDevices = virtualDevices;
	}

	public void addVirtualDevice(VirtualDevice device) throws VirtualizationException {

		if (virtualDevices.contains(device))
			throw new VirtualizationException("Virtual device " + device.getName() + " already exists in virtual devices list.");

		if (virtualDevices.size() >= virtualDevicesCapacity)
			throw new VirtualizationException("Can't create more virtual devices - limit exceeded.");

		virtualDevices.add(device);
	}

	public void removeVirtualDevice(VirtualDevice device) throws VirtualizationException {

		if (!virtualDevices.contains(device))
			throw new VirtualizationException("Virtual device " + device.getName() + " does not exist in virtual devices list.");

		virtualDevices.remove(device);

	}
}
