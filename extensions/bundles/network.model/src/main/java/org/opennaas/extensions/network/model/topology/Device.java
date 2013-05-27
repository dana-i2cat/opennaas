package org.opennaas.extensions.network.model.topology;

import java.util.ArrayList;
import java.util.List;

public class Device extends NetworkElement {

	List<ConnectionPoint>	interfaces	= new ArrayList<ConnectionPoint>();

	public List<ConnectionPoint> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<ConnectionPoint> interfaces) {
		this.interfaces = interfaces;
	}
}
