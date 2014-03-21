package org.opennaas.extensions.router.model;

import java.util.Set;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class BridgeDomain extends ManagedElement {

	private static final long	serialVersionUID	= 4383689381036117461L;

	private int					vlanId;
	private Set<NetworkPort>	networkPorts;

	public int getVlanId() {
		return vlanId;
	}

	public void setVlanId(int vlanId) {
		this.vlanId = vlanId;
	}

	public Set<NetworkPort> getNetworkPorts() {
		return networkPorts;
	}

	public void setNetworkPorts(Set<NetworkPort> networkPorts) {
		this.networkPorts = networkPorts;
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
