package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.model;

public class FloodlightOFMatch {
	
	protected String wildcards;
	/**
	 * switch port on which the packet is received. <number> Can be hexadecimal (with leading 0x) or decimal
	 */
	protected String ingressPort;
	/**
	 * <mac address> 	xx:xx:xx:xx:xx:xx
	 */
	protected String srcMac;
	/**
	 * <mac address> 	xx:xx:xx:xx:xx:xx
	 */
	protected String dstMac;
	/**
	 * <number> 	Can be hexadecimal (with leading 0x) or decimal
	 */
	protected String vlanId;
	/**
	 * <number> 	Can be hexadecimal (with leading 0x) or decimal
	 */
	protected String vlanPriority;
	/**
	 * <number> 	Can be hexadecimal (with leading 0x) or decimal
	 */
	protected String etherType;
	/**
	 * <number> 	Can be hexadecimal (with leading 0x) or decimal
	 */
	protected String tosBits;
	/**
	 * <number> 	Can be hexadecimal (with leading 0x) or decimal
	 */
	protected String protocol;
	/**
	 * <ip address> 	xx.xx.xx.xx
	 */
	protected String srcIp;
	/**
	 * <ip address> 	xx.xx.xx.xx
	 */
	protected String dstIp;
	/**
	 * <number> 	Can be hexadecimal (with leading 0x) or decimal
	 */
	protected String srcPort;
	/**
	 * <number> 	Can be hexadecimal (with leading 0x) or decimal 
	 */
	protected String dstPort;
	
	/**
	 * @return the wildcards
	 */
	public String getWildcards() {
		return wildcards;
	}
	
	/**
	 * @param wildcards the wildcards to set
	 */
	public void setWildcards(String wildcards) {
		this.wildcards = wildcards;
	}
	
	/**
	 * @return the ingressPort
	 */
	public String getIngressPort() {
		return ingressPort;
	}
	
	/**
	 * @param ingressPort the ingressPort to set
	 */
	public void setIngressPort(String ingressPort) {
		this.ingressPort = ingressPort;
	}
	
	/**
	 * @return the srcMac
	 */
	public String getSrcMac() {
		return srcMac;
	}
	
	/**
	 * @param srcMac the srcMac to set
	 */
	public void setSrcMac(String srcMac) {
		this.srcMac = srcMac;
	}
	
	/**
	 * @return the dstMac
	 */
	public String getDstMac() {
		return dstMac;
	}
	
	/**
	 * @param dstMac the dstMac to set
	 */
	public void setDstMac(String dstMac) {
		this.dstMac = dstMac;
	}
	/**
	 * @return the vlanId
	 */
	public String getVlanId() {
		return vlanId;
	}
	
	/**
	 * @param vlanId the vlanId to set
	 */
	public void setVlanId(String vlanId) {
		this.vlanId = vlanId;
	}
	/**
	 * @return the vlanPriority
	 */
	public String getVlanPriority() {
		return vlanPriority;
	}
	
	/**
	 * @param vlanPriority the vlanPriority to set
	 */
	public void setVlanPriority(String vlanPriority) {
		this.vlanPriority = vlanPriority;
	}
	
	/**
	 * @return the etherType
	 */
	public String getEtherType() {
		return etherType;
	}
	
	/**
	 * @param etherType the etherType to set
	 */
	public void setEtherType(String etherType) {
		this.etherType = etherType;
	}
	
	/**
	 * @return the tosBits
	 */
	public String getTosBits() {
		return tosBits;
	}
	
	/**
	 * @param tosBits the tosBits to set
	 */
	public void setTosBits(String tosBits) {
		this.tosBits = tosBits;
	}
	
	/**
	 * @return the protocol
	 */
	public String getProtocol() {
		return protocol;
	}
	
	/**
	 * @param protocol the protocol to set
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
	/**
	 * @return the srcIp
	 */
	public String getSrcIp() {
		return srcIp;
	}
	
	/**
	 * @param srcIp the srcIp to set
	 */
	public void setSrcIp(String srcIp) {
		this.srcIp = srcIp;
	}
	
	/**
	 * @return the dstIp
	 */
	public String getDstIp() {
		return dstIp;
	}
	
	/**
	 * @param dstIp the dstIp to set
	 */
	public void setDstIp(String dstIp) {
		this.dstIp = dstIp;
	}
	
	/**
	 * @return the srcPort
	 */
	public String getSrcPort() {
		return srcPort;
	}
	
	/**
	 * @param srcPort the srcPort to set
	 */
	public void setSrcPort(String srcPort) {
		this.srcPort = srcPort;
	}
	
	/**
	 * @return the dstPort
	 */
	public String getDstPort() {
		return dstPort;
	}
	
	/**
	 * @param dstPort the dstPort to set
	 */
	public void setDstPort(String dstPort) {
		this.dstPort = dstPort;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dstIp == null) ? 0 : dstIp.hashCode());
		result = prime * result + ((dstMac == null) ? 0 : dstMac.hashCode());
		result = prime * result + ((dstPort == null) ? 0 : dstPort.hashCode());
		result = prime * result
				+ ((etherType == null) ? 0 : etherType.hashCode());
		result = prime * result
				+ ((ingressPort == null) ? 0 : ingressPort.hashCode());
		result = prime * result
				+ ((protocol == null) ? 0 : protocol.hashCode());
		result = prime * result + ((srcIp == null) ? 0 : srcIp.hashCode());
		result = prime * result + ((srcMac == null) ? 0 : srcMac.hashCode());
		result = prime * result + ((srcPort == null) ? 0 : srcPort.hashCode());
		result = prime * result + ((tosBits == null) ? 0 : tosBits.hashCode());
		result = prime * result + ((vlanId == null) ? 0 : vlanId.hashCode());
		result = prime * result
				+ ((vlanPriority == null) ? 0 : vlanPriority.hashCode());
		result = prime * result
				+ ((wildcards == null) ? 0 : wildcards.hashCode());
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FloodlightOFMatch other = (FloodlightOFMatch) obj;
		if (dstIp == null) {
			if (other.dstIp != null)
				return false;
		} else if (!dstIp.equals(other.dstIp))
			return false;
		if (dstMac == null) {
			if (other.dstMac != null)
				return false;
		} else if (!dstMac.equals(other.dstMac))
			return false;
		if (dstPort == null) {
			if (other.dstPort != null)
				return false;
		} else if (!dstPort.equals(other.dstPort))
			return false;
		if (etherType == null) {
			if (other.etherType != null)
				return false;
		} else if (!etherType.equals(other.etherType))
			return false;
		if (ingressPort == null) {
			if (other.ingressPort != null)
				return false;
		} else if (!ingressPort.equals(other.ingressPort))
			return false;
		if (protocol == null) {
			if (other.protocol != null)
				return false;
		} else if (!protocol.equals(other.protocol))
			return false;
		if (srcIp == null) {
			if (other.srcIp != null)
				return false;
		} else if (!srcIp.equals(other.srcIp))
			return false;
		if (srcMac == null) {
			if (other.srcMac != null)
				return false;
		} else if (!srcMac.equals(other.srcMac))
			return false;
		if (srcPort == null) {
			if (other.srcPort != null)
				return false;
		} else if (!srcPort.equals(other.srcPort))
			return false;
		if (tosBits == null) {
			if (other.tosBits != null)
				return false;
		} else if (!tosBits.equals(other.tosBits))
			return false;
		if (vlanId == null) {
			if (other.vlanId != null)
				return false;
		} else if (!vlanId.equals(other.vlanId))
			return false;
		if (vlanPriority == null) {
			if (other.vlanPriority != null)
				return false;
		} else if (!vlanPriority.equals(other.vlanPriority))
			return false;
		if (wildcards == null) {
			if (other.wildcards != null)
				return false;
		} else if (!wildcards.equals(other.wildcards))
			return false;
		return true;
	}

}
