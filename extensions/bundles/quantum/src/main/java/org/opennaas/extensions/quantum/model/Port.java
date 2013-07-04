package org.opennaas.extensions.quantum.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a port on a quantum v2 network
 * 
 * @author Julio Carlos Barrera
 * 
 */
@XmlRootElement(name = "port")
@XmlAccessorType(XmlAccessType.FIELD)
public class Port implements HasId, HasTenant {

	private String				id;
	private String				tenant_id;

	private String				name;
	private String				network_id;
	private List<IPAllocation>	fixed_ips;
	private String				mac_address;
	private Boolean				admin_state_up;
	private String				status;
	private String				device_id;
	private String				device_owner;

	public Port() {

		fixed_ips = new ArrayList<IPAllocation>();

	}

	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getTenant_id() {
		return tenant_id;
	}

	public void setTenant_id(String tenant_id) {
		this.tenant_id = tenant_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNetwork_id() {
		return network_id;
	}

	public void setNetwork_id(String network_id) {
		this.network_id = network_id;
	}

	public List<IPAllocation> getFixed_ips() {
		return fixed_ips;
	}

	public void setFixed_ips(List<IPAllocation> fixed_ips) {
		this.fixed_ips = fixed_ips;
	}

	public String getMac_address() {
		return mac_address;
	}

	public void setMac_address(String mac_address) {
		this.mac_address = mac_address;
	}

	public Boolean isAdmin_state_up() {
		return admin_state_up;
	}

	public void setAdmin_state_up(Boolean admin_state_up) {
		this.admin_state_up = admin_state_up;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDevice_id() {
		return device_id;
	}

	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}

	public String getDevice_owner() {
		return device_owner;
	}

	public void setDevice_owner(String device_owner) {
		this.device_owner = device_owner;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Port other = (Port) obj;
		if (!id.equals(other.getId()))
			return false;
		if (!tenant_id.equals(other.getTenant_id()))
			return false;
		if (!name.equals(other.getName()))
			return false;
		if (!network_id.equals(other.getNetwork_id()))
			return false;
		if (!fixed_ips.equals(other.getFixed_ips()))
			return false;
		if (!mac_address.equals(other.getMac_address()))
			return false;
		if (!admin_state_up.equals(other.isAdmin_state_up()))
			return false;
		if (!status.equals(other.getStatus()))
			return false;
		if (!device_id.equals(other.getDevice_id()))
			return false;
		if (!device_owner.equals(other.getDevice_owner()))
			return false;
		return true;
	}
}
