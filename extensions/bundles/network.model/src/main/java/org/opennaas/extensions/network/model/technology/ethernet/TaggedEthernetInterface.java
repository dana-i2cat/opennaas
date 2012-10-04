package org.opennaas.extensions.network.model.technology.ethernet;

public class TaggedEthernetInterface extends EthernetInterface {

	private long	vlanID;

	public long getVlanID() {
		return vlanID;
	}

	public void setVlanID(long vlanID) {
		this.vlanID = vlanID;
	}

}
