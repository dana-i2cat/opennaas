package org.opennaas.extensions.quantum.model;

import java.util.Calendar;

/**
 * Internal representation of allocated IP addresses in a Quantum subnet.
 * 
 * @author Julio Carlos Barrera
 * 
 */
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
}
