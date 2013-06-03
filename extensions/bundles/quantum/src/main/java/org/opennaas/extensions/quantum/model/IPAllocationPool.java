package org.opennaas.extensions.quantum.model;

/**
 * Representation of an allocation pool in a Quantum subnet
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class IPAllocationPool implements HasId {

	private String				id;

	private String				subnet_id;
	private String				first_ip;
	private String				last_ip;
	private IPAvailabilityRange	available_ranges;

	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSubnet_id() {
		return subnet_id;
	}

	public void setSubnet_id(String subnet_id) {
		this.subnet_id = subnet_id;
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

	public IPAvailabilityRange getAvailable_ranges() {
		return available_ranges;
	}

	public void setAvailable_ranges(IPAvailabilityRange available_ranges) {
		this.available_ranges = available_ranges;
	}

	@Override
	public String toString() {
		return first_ip + " - " + last_ip;
	}
}
