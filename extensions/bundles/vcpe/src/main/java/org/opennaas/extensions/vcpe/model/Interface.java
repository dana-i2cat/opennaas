package org.opennaas.extensions.vcpe.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class Interface extends VCPENetworkElement {

	private String	ipAddress;
	private int		port;
	private long	vlan;

	/**
	 * Logical interfaces must hold a reference to the physical interface they belong to. This field provides this reference by holding the physical
	 * interface name.
	 */
	private String	physicalInterfaceName;

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param ipAddress
	 *            the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the vlan
	 */
	public long getVlan() {
		return vlan;
	}

	/**
	 * @param vlan
	 *            the vlan to set
	 */
	public void setVlan(long vlan) {
		this.vlan = vlan;
	}

	/**
	 * @return the physicalInterfaceName
	 */
	public String getPhysicalInterfaceName() {
		return physicalInterfaceName;
	}

	/**
	 * @param physicalInterfaceName
	 *            the physicalInterfaceName to set
	 */
	public void setPhysicalInterfaceName(String physicalInterfaceName) {
		this.physicalInterfaceName = physicalInterfaceName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
		result = prime * result + ((physicalInterfaceName == null) ? 0 : physicalInterfaceName.hashCode());
		result = prime * result + port;
		result = prime * result + (int) (vlan ^ (vlan >>> 32));
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Interface other = (Interface) obj;
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
		if (port != other.port)
			return false;
		if (vlan != other.vlan)
			return false;
		return true;
	}

}
