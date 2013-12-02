package org.opennaas.extensions.network.model.topology;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.network.model.virtual.VirtualizationService;

public class Device extends NetworkElement {

	List<ConnectionPoint>			interfaces	= new ArrayList<ConnectionPoint>();

	protected VirtualizationService	virtualizationService;

	public Device() {
		virtualizationService = new VirtualizationService();
		virtualizationService.setVirtualDevicesCapacity(16);
	}

	public List<ConnectionPoint> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<ConnectionPoint> interfaces) {
		this.interfaces = interfaces;
	}

	public VirtualizationService getVirtualizationService() {
		return virtualizationService;
	}

	public void setVirtualizationService(VirtualizationService virtualizationService) {
		this.virtualizationService = virtualizationService;
	}
}
