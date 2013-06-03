package org.opennaas.extensions.quantum.model;

/**
 * Internal representation of a DNS nameserver
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class DNSNameServer {

	private String	address;
	private String	subnet_id;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getSubnet_id() {
		return subnet_id;
	}

	public void setSubnet_id(String subnet_id) {
		this.subnet_id = subnet_id;
	}
}
