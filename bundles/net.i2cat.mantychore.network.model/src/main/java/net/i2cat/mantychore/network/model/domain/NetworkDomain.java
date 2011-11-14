package net.i2cat.mantychore.network.model.domain;

import java.util.List;

import net.i2cat.mantychore.network.model.topology.ConnectionPoint;
import net.i2cat.mantychore.network.model.topology.Device;
import net.i2cat.mantychore.network.model.topology.NetworkElement;

public class NetworkDomain extends NetworkElement {
	
	List<Device> devices;
	List<ConnectionPoint> interfaces;

	public List<Device> getDevices() {
		return devices;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}
	
	public void addDevice(Device device) {
		devices.add(device);
	}

	public List<ConnectionPoint> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<ConnectionPoint> interfaces) {
		this.interfaces = interfaces;
	}
}
