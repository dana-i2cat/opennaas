package org.opennaas.extensions.quantum.extensions.l3.model;

import org.opennaas.extensions.quantum.model.HasId;
import org.opennaas.extensions.quantum.model.HasTenant;
import org.opennaas.extensions.quantum.model.Port;

/**
 * Represents a v2 quantum router
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class Router implements HasId, HasTenant {

	private String	id;
	private String	tenant_id;

	private String	name;
	private String	status;
	private Boolean	admin_state_up;
	private String	gw_port_id;
	private Port	gw_port;

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getAdmin_state_up() {
		return admin_state_up;
	}

	public void setAdmin_state_up(Boolean admin_state_up) {
		this.admin_state_up = admin_state_up;
	}

	public String getGw_port_id() {
		return gw_port_id;
	}

	public void setGw_port_id(String gw_port_id) {
		this.gw_port_id = gw_port_id;
	}

	public Port getGw_port() {
		return gw_port;
	}

	public void setGw_port(Port gw_port) {
		this.gw_port = gw_port;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Router other = (Router) obj;
		if (!id.equals(other.getId()))
			return false;
		if (!tenant_id.equals(other.getTenant_id()))
			return false;
		if (!name.equals(other.getName()))
			return false;
		if (!status.equals(other.getStatus()))
			return false;
		if (!admin_state_up.equals(other.getAdmin_state_up()))
			return false;
		if (!gw_port_id.equals(other.getGw_port_id()))
			return false;
		if (!gw_port.equals(other.getGw_port()))
			return false;
		return true;
	}

}
