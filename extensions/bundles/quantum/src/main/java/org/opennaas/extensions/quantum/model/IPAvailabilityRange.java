package org.opennaas.extensions.quantum.model;

/*
 * #%L
 * OpenNaaS :: Quantum
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Internal representation of available IPs for Quantum subnets.
 * 
 * Allocation - first entry from the range will be allocated. If the first entry is equal to the last entry then this row will be deleted. Recycling
 * ips involves appending to existing ranges. This is only done if the range is contiguous. If not, the first_ip will be the same as the last_ip. When
 * adjacent ips are recycled the ranges will be merged.
 * 
 * @author Julio Carlos Barrera
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class IPAvailabilityRange {

	private String	allocation_pool_id;
	private String	first_ip;
	private String	last_ip;

	public String getAllocation_pool_id() {
		return allocation_pool_id;
	}

	public void setAllocation_pool_id(String allocation_pool_id) {
		this.allocation_pool_id = allocation_pool_id;
	}

	public String getFirst_ip() {
		return first_ip;
	}

	public void setFirst_ip(String first_ip) {
		this.first_ip = first_ip;
	}

	public String getLast_ip() {
		return last_ip;
	}

	public void setLast_ip(String last_ip) {
		this.last_ip = last_ip;
	}

	@Override
	public String toString() {
		return first_ip + " - " + last_ip;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IPAvailabilityRange other = (IPAvailabilityRange) obj;
		if (!allocation_pool_id.equals(other.getAllocation_pool_id()))
			return false;
		if (!first_ip.equals(other.getFirst_ip()))
			return false;
		if (!last_ip.equals(other.getLast_ip()))
			return false;
		return true;
	}
}
