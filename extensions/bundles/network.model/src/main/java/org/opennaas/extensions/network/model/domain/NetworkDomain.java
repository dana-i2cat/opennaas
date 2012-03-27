package org.opennaas.extensions.network.model.domain;

import java.util.List;

import org.opennaas.extensions.network.model.topology.ConnectionPoint;
import org.opennaas.extensions.network.model.topology.Device;
import org.opennaas.extensions.network.model.topology.NetworkElement;

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
