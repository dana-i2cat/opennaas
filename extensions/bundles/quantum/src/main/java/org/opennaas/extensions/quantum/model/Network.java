package org.opennaas.extensions.quantum.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a v2 quantum network
 * 
 * @author Julio Carlos Barrera
 * 
 */
@XmlRootElement(name = "network")
@XmlAccessorType(XmlAccessType.FIELD)
public class Network implements HasId, HasTenant {

	private String			id;
	private String			tenant_id;

	private String			name;
	private List<Port>		ports;
	private List<Subnet>	subnets;
	private String			status;
	private Boolean			admin_state_up;
	private Boolean			shared;

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

	public List<Port> getPorts() {
		return ports;
	}

	public void setPorts(List<Port> ports) {
		this.ports = ports;
	}

	public List<Subnet> getSubnets() {
		return subnets;
	}

	public void setSubnets(List<Subnet> subnets) {
		this.subnets = subnets;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean isAdmin_state_up() {
		return admin_state_up;
	}

	public void setAdmin_state_up(Boolean admin_state_up) {
		this.admin_state_up = admin_state_up;
	}

	public Boolean isShared() {
		return shared;
	}

	public void setShared(Boolean shared) {
		this.shared = shared;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Network other = (Network) obj;
		if (!id.equals(other.getId()))
			return false;
		if (!tenant_id.equals(other.getTenant_id()))
			return false;
		if (!name.equals(other.getName()))
			return false;
		if (!ports.equals(other.getPorts()))
			return false;
		if (!subnets.equals(other.getSubnets()))
			return false;
		if (!status.equals(other.getStatus()))
			return false;
		if (!admin_state_up.equals(other.isAdmin_state_up()))
			return false;
		if (!shared.equals(other.isShared()))
			return false;
		return true;
	}

}
