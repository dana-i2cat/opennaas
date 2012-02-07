package net.i2cat.mantychore.network.model.domain;

import java.util.List;

import net.i2cat.mantychore.network.model.topology.ConnectionPoint;
import net.i2cat.mantychore.network.model.topology.Device;
import net.i2cat.mantychore.network.model.topology.NetworkElement;

public class NetworkDomain extends NetworkElement {
	
	List<Device> hasDevice;
	List<ConnectionPoint> hasInterface;
	
	public List<Device> getHasDevice() {
		return hasDevice;
	}
	public void setHasDevice(List<Device> hasDevice) {
		this.hasDevice = hasDevice;
	}
	public List<ConnectionPoint> getHasInterface() {
		return hasInterface;
	}
	public void setHasInterface(List<ConnectionPoint> hasInterface) {
		this.hasInterface = hasInterface;
	}

	
}
