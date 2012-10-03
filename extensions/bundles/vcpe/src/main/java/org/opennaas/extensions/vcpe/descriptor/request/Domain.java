package org.opennaas.extensions.vcpe.descriptor.request;

import java.util.List;

public class Domain extends RequestElement {

	private List<Device>	devices;

	private List<Interface>	interfaces;

	public List<Device> getDevices() {
		return devices;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}

	public List<Interface> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<Interface> interfaces) {
		this.interfaces = interfaces;
	}
}
