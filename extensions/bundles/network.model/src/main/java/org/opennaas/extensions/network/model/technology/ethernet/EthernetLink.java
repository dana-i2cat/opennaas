package org.opennaas.extensions.network.model.technology.ethernet;

import org.opennaas.extensions.network.model.NetworkModelHelper;
import org.opennaas.extensions.network.model.topology.Link;

public class EthernetLink extends Link {

	/**
	 * Link bandwidth in bits per second.
	 */
	private long	bandwidth;

	public long getBandwidth() {
		return bandwidth;
	}

	public void setBandwidth(long bandwidth) {
		this.bandwidth = bandwidth;
	}

	/**
	 * 
	 * @return this link allocated bandwidth. Calculated from the aggregation of this link sublinks bandwidth.
	 */
	public long getAllocatedBandwidth() {
		long allocatedBandwidth = 0;
		for (Link sublink : NetworkModelHelper.getClientLinks(this)) {
			if (sublink instanceof EthernetLink) {
				allocatedBandwidth += ((EthernetLink) sublink).getBandwidth();
			}
		}
		return allocatedBandwidth;
	}

	/**
	 * 
	 * @return available bandwidth.
	 */
	public long getAvailableBandwidth() {
		return getBandwidth() - getAllocatedBandwidth();
	}

}
