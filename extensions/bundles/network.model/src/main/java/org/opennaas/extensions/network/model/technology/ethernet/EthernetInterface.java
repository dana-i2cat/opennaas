package org.opennaas.extensions.network.model.technology.ethernet;

import org.opennaas.extensions.network.model.topology.Interface;

public class EthernetInterface extends Interface {

	/**
	 * The current bandwidth of the Interface in Bits per Second.
	 */
	private long	bandwidth;

	/**
	 * 
	 * @return bandwidth in bits per second
	 */
	public long getBandwidth() {
		return bandwidth;
	}

	/**
	 * 
	 * @param bandwith
	 *            in bits per second.
	 */
	public void setBandwidth(long bandwidth) {
		this.bandwidth = bandwidth;
	}

}
