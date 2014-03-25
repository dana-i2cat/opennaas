package org.opennaas.extensions.router.model;

/*
 * #%L
 * OpenNaaS :: CIM Model
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

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class BridgeDomain extends SystemSpecificCollection {

	// FIXME this value is used to distinguish between the int default value (0) and the vlanId 0.
	public static final int		VLAN_ID_DEFAULT_VALUE	= -1;

	private static final long	serialVersionUID		= 4383689381036117461L;

	private int					vlanId;
	private Set<String>			networkPorts;

	public BridgeDomain() {
		networkPorts = new HashSet<String>();
		vlanId = VLAN_ID_DEFAULT_VALUE;
	}

	public int getVlanId() {
		return vlanId;
	}

	public void setVlanId(int vlanId) {
		this.vlanId = vlanId;
	}

	public Set<String> getNetworkPorts() {
		return networkPorts;
	}

	public void setNetworkPorts(Set<String> networkPorts) {
		this.networkPorts = networkPorts;
	}

	public void addNetworkPort(String netPort) {
		this.networkPorts.add(netPort);
	}

	public void removeNetworkPort(String netPort) {
		this.networkPorts.remove(netPort);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((networkPorts == null) ? 0 : networkPorts.hashCode());
		result = prime * result + ((getElementName() == null) ? 0 : getElementName().hashCode());
		result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
		result = prime * result + vlanId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BridgeDomain other = (BridgeDomain) obj;
		if (networkPorts == null) {
			if (other.networkPorts != null)
				return false;
		} else if (!networkPorts.equals(other.networkPorts))
			return false;
		if (getElementName() == null) {
			if (other.getElementName() != null)
				return false;
		} else if (!getElementName().equals(other.getElementName()))
			return false;
		if (getDescription() == null) {
			if (other.getDescription() != null)
				return false;
		} else if (!getDescription().equals(other.getDescription()))
			return false;

		if (vlanId != other.vlanId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BridgeDomain [elementName=" + getElementName() + ", vlanId=" + vlanId + ", networkPorts=" + networkPorts + ", description=" + getDescription() + "]";
	}
}
