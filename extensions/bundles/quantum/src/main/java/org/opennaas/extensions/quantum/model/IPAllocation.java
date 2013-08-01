package org.opennaas.extensions.quantum.model;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Internal representation of allocated IP addresses in a Quantum subnet.
 * 
 * @author Julio Carlos Barrera
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class IPAllocation {

	private String		port_id;
	private String		ip_address;
	private String		subnet_id;
	private String		network_id;
	private Calendar	expiration;

	public String getPort_id() {
		return port_id;
	}

	public void setPort_id(String port_id) {
		this.port_id = port_id;
	}

	public String getIp_address() {
		return ip_address;
	}

	public void setIp_address(String ip_address) {
		this.ip_address = ip_address;
	}

	public String getSubnet_id() {
		return subnet_id;
	}

	public void setSubnet_id(String subnet_id) {
		this.subnet_id = subnet_id;
	}

	public String getNetwork_id() {
		return network_id;
	}

	public void setNetwork_id(String network_id) {
		this.network_id = network_id;
	}

	public Calendar getExpiration() {
		return expiration;
	}

	public void setExpiration(Calendar expiration) {
		this.expiration = expiration;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IPAllocation other = (IPAllocation) obj;
		if (!port_id.equals(other.getPort_id()))
			return false;
		if (!ip_address.equals(other.getIp_address()))
			return false;
		if (!subnet_id.equals(other.getSubnet_id()))
			return false;
		if (!network_id.equals(other.getNetwork_id()))
			return false;
		if (!expiration.equals(other.getExpiration()))
			return false;
		return true;
	}
}
