package net.i2cat.mantychore.network.model.topology;

import java.util.List;

public class Device extends NetworkElement {
	
	
	List<ConnectionPoint> interfaces;

	public List<ConnectionPoint> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<ConnectionPoint> interfaces) {
		this.interfaces = interfaces;
	}
}
