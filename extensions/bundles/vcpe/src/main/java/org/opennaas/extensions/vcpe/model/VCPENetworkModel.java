package org.opennaas.extensions.vcpe.model;

import org.opennaas.extensions.network.model.NetworkModel;

public class VCPENetworkModel {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1499224044797350113L;

	private NetworkModel		physicalTopology;

	private NetworkModel		vCPENetworkTopology;

	private NetworkModel		virtualTopology;

	public NetworkModel getPhysicalTopology() {
		return physicalTopology;
	}

	public void setPhysicalTopology(NetworkModel physicalTopology) {
		this.physicalTopology = physicalTopology;
	}

	public NetworkModel getvCPENetworkTopology() {
		return vCPENetworkTopology;
	}

	public void setvCPENetworkTopology(NetworkModel vCPENetworkTopology) {
		this.vCPENetworkTopology = vCPENetworkTopology;
	}

	public NetworkModel getVirtualTopology() {
		return virtualTopology;
	}

	public void setVirtualTopology(NetworkModel virtualTopology) {
		this.virtualTopology = virtualTopology;
	}

}
