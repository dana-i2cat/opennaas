package org.opennaas.extensions.quantum.model;

import java.util.Set;

/**
 * Represents a v2 quantum network
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class Network implements HasId, HasTenant {

	private String		id;
	private String		tenant_id;

	private String		name;
	private Set<Port>	ports;
	private Set<Subnet>	subnets;
	private String		status;
	private boolean		admin_state_up;
	private boolean		shared;

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

	public Set<Port> getPorts() {
		return ports;
	}

	public void setPorts(Set<Port> ports) {
		this.ports = ports;
	}

	public Set<Subnet> getSubnets() {
		return subnets;
	}

	public void setSubnets(Set<Subnet> subnets) {
		this.subnets = subnets;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isAdmin_state_up() {
		return admin_state_up;
	}

	public void setAdmin_state_up(boolean admin_state_up) {
		this.admin_state_up = admin_state_up;
	}

	public boolean isShared() {
		return shared;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
	}

}
