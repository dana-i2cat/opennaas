package org.opennaas.extensions.quantum.model;

import java.util.Set;

/**
 * Represents a port on a quantum v2 network
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class Port implements HasId, HasTenant {

	private String				id;
	private String				tenant_id;

	private String				name;
	private String				network_id;
	private Set<IPAllocation>	fixed_ips;
	private String				mac_address;
	private boolean				admin_state_up;
	private String				status;
	private String				device_id;
	private String				device_owner;

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

	public Set<IPAllocation> getFixed_ips() {
		return fixed_ips;
	}

	public void setFixed_ips(Set<IPAllocation> fixed_ips) {
		this.fixed_ips = fixed_ips;
	}

	public String getMac_address() {
		return mac_address;
	}

	public void setMac_address(String mac_address) {
		this.mac_address = mac_address;
	}

	public boolean isAdmin_state_up() {
		return admin_state_up;
	}

	public void setAdmin_state_up(boolean admin_state_up) {
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
}
