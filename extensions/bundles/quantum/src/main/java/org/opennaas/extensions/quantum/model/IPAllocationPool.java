package org.opennaas.extensions.quantum.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Representation of an allocation pool in a Quantum subnet
 * 
 * @author Julio Carlos Barrera
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class IPAllocationPool implements HasId {

	private String						id;

	private String						subnet_id;
	@XmlElement(name = "start")
	private String						first_ip;
	@XmlElement(name = "end")
	private String						last_ip;
	private List<IPAvailabilityRange>	available_ranges;

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

	public List<IPAvailabilityRange> getAvailable_ranges() {
		return available_ranges;
	}

	public void setAvailable_ranges(List<IPAvailabilityRange> available_ranges) {
		this.available_ranges = available_ranges;
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
		IPAllocationPool other = (IPAllocationPool) obj;
		if (!id.equals(other.getId()))
			return false;
		if (!subnet_id.equals(other.getSubnet_id()))
			return false;
		if (!first_ip.equals(other.getFirst_ip()))
			return false;
		if (!last_ip.equals(other.getLast_ip()))
			return false;
		if (!available_ranges.equals(other.getAvailable_ranges()))
			return false;
		return true;
	}
}
