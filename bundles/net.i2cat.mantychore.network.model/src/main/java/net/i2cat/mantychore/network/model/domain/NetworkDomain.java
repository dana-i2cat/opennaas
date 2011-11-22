package net.i2cat.mantychore.network.model.domain;

import java.util.List;

import net.i2cat.mantychore.network.model.topology.ConnectionPoint;
import net.i2cat.mantychore.network.model.topology.Device;
import net.i2cat.mantychore.network.model.topology.NetworkElement;

public class NetworkDomain extends NetworkElement {
	
	List<Device> devices;
	
	List<ConnectionPoint> outboundInterfaces;
	List<ConnectionPoint> inboundInterfaces;

	public List<Device> getDevices() {
		return devices;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}
	
	public void addDevice(Device device) {
		devices.add(device);
	}

	public List<ConnectionPoint> getOutboundInterfaces() {
		return outboundInterfaces;
	}

	public void setOutboundInterfaces(List<ConnectionPoint> outboundInterfaces) {
		this.outboundInterfaces = outboundInterfaces;
	}

	public List<ConnectionPoint> getInboundInterfaces() {
		return inboundInterfaces;
	}

	public void setInboundInterfaces(List<ConnectionPoint> inboundInterfaces) {
		this.inboundInterfaces = inboundInterfaces;
	}
}
