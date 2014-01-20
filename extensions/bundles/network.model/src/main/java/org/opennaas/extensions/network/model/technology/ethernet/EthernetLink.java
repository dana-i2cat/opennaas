package org.opennaas.extensions.network.model.technology.ethernet;

/*
 * #%L
 * OpenNaaS :: Network :: Model
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
