package org.opennaas.extensions.vcpe.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class Interface extends VCPENetworkElement {

	private long	vlanId;

	private String	ipAddress;

	/**
	 * Logical interfaces must hold a reference to the physical interface they belong to. This field provides this reference by holding the physical
	 * interface name.
	 */
	private String	physicalInterfaceName;

	private int		portNumber;

	public long getVlanId() {
		return vlanId;
	}

	public void setVlanId(long vlanId) {
		this.vlanId = vlanId;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getPhysicalInterfaceName() {
		return physicalInterfaceName;
	}

	public void setPhysicalInterfaceName(String physicalInterfaceName) {
		this.physicalInterfaceName = physicalInterfaceName;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
		result = prime * result + ((physicalInterfaceName == null) ? 0 : physicalInterfaceName.hashCode());
		result = prime * result + portNumber;
		result = prime * result + (int) (vlanId ^ (vlanId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Interface other = (Interface) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nameInTemplate == null) {
			if (other.nameInTemplate != null)
				return false;
		} else if (!nameInTemplate.equals(other.nameInTemplate))
			return false;
		if (ipAddress == null) {
			if (other.ipAddress != null)
				return false;
		} else if (!ipAddress.equals(other.ipAddress))
			return false;
		if (physicalInterfaceName == null) {
			if (other.physicalInterfaceName != null)
				return false;
		} else if (!physicalInterfaceName.equals(other.physicalInterfaceName))
			return false;
		if (portNumber != other.portNumber)
			return false;
		if (vlanId != other.vlanId)
			return false;
		return true;
	}
}
