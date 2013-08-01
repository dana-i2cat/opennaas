package org.opennaas.extensions.quantum.extensions.l3.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.quantum.model.HasId;
import org.opennaas.extensions.quantum.model.HasTenant;

/**
 * Represents a floating IP, which may or many not be allocated to a tenant, and may or may not be associated with an internal port/ip address/router.
 * 
 * @author logoff
 * 
 */
@XmlRootElement(name = "floating_ip")
@XmlAccessorType(XmlAccessType.FIELD)
public class FloatingIP implements HasId, HasTenant {

	private String	id;
	private String	tenant_id;

	private String	floating_ip_address;
	private String	floating_network_id;
	private String	floating_port_id;
	private String	fixed_port_id;
	private String	fixed_ip_address;
	private String	router_id;

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

	public String getFloating_ip_address() {
		return floating_ip_address;
	}

	public void setFloating_ip_address(String floating_ip_address) {
		this.floating_ip_address = floating_ip_address;
	}

	public String getFloating_network_id() {
		return floating_network_id;
	}

	public void setFloating_network_id(String floating_network_id) {
		this.floating_network_id = floating_network_id;
	}

	public String getFloating_port_id() {
		return floating_port_id;
	}

	public void setFloating_port_id(String floating_port_id) {
		this.floating_port_id = floating_port_id;
	}

	public String getFixed_port_id() {
		return fixed_port_id;
	}

	public void setFixed_port_id(String fixed_port_id) {
		this.fixed_port_id = fixed_port_id;
	}

	public String getFixed_ip_address() {
		return fixed_ip_address;
	}

	public void setFixed_ip_address(String fixed_ip_address) {
		this.fixed_ip_address = fixed_ip_address;
	}

	public String getRouter_id() {
		return router_id;
	}

	public void setRouter_id(String router_id) {
		this.router_id = router_id;
	}

	public void setTenant_id(String tenant_id) {
		this.tenant_id = tenant_id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FloatingIP other = (FloatingIP) obj;
		if (!id.equals(other.getId()))
			return false;
		if (!tenant_id.equals(other.getTenant_id()))
			return false;
		if (!floating_ip_address.equals(other.getFloating_ip_address()))
			return false;
		if (!floating_network_id.equals(other.getFloating_network_id()))
			return false;
		if (!floating_port_id.equals(other.getFloating_port_id()))
			return false;
		if (!fixed_port_id.equals(other.getFixed_port_id()))
			return false;
		if (!fixed_ip_address.equals(other.getFixed_ip_address()))
			return false;
		if (!router_id.equals(other.getRouter_id()))
			return false;
		return true;
	}
}
