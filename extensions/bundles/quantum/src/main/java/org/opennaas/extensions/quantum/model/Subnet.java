package org.opennaas.extensions.quantum.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a quantum subnet.
 * 
 * When a subnet is created the first and last entries will be created. These are used for the IP allocation.
 * 
 * @author Julio Carlos Barrera
 * 
 */
@XmlRootElement(name = "subnet")
@XmlAccessorType(XmlAccessType.FIELD)
public class Subnet implements HasId, HasTenant {

	@XmlID
	private String					id;
	private String					tenant_id;

	private String					name;
	private String					network_id;
	private Integer					ip_version;
	private String					cidr;
	private String					gateway_ip;
	private List<IPAllocationPool>	allocation_pools;
	private Boolean					enable_dhcp;
	private List<DNSNameServer>		dns_nameservers;
	@XmlElement(name = "host_routes")
	private List<SubnetRoute>		routes;
	private Boolean					shared;

	public Subnet() {
		allocation_pools = new ArrayList<IPAllocationPool>();
		dns_nameservers = new ArrayList<DNSNameServer>();
		routes = new ArrayList<SubnetRoute>();
	}

	public Subnet(String id) {
		this.id = id;
		allocation_pools = new ArrayList<IPAllocationPool>();
		dns_nameservers = new ArrayList<DNSNameServer>();
		routes = new ArrayList<SubnetRoute>();
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

	public Integer getIp_version() {
		return ip_version;
	}

	public void setIp_version(Integer ip_version) {
		this.ip_version = ip_version;
	}

	public String getCidr() {
		return cidr;
	}

	public void setCidr(String cidr) {
		this.cidr = cidr;
	}

	public String getGateway_ip() {
		return gateway_ip;
	}

	public void setGateway_ip(String gateway_ip) {
		this.gateway_ip = gateway_ip;
	}

	public List<IPAllocationPool> getAllocation_pools() {
		return allocation_pools;
	}

	public void setAllocation_pools(List<IPAllocationPool> allocation_pools) {
		this.allocation_pools = allocation_pools;
	}

	public Boolean isEnable_dhcp() {
		return enable_dhcp;
	}

	public void setEnable_dhcp(Boolean enable_dhcp) {
		this.enable_dhcp = enable_dhcp;
	}

	public List<DNSNameServer> getDns_nameservers() {
		return dns_nameservers;
	}

	public void setDns_nameservers(List<DNSNameServer> dns_nameservers) {
		this.dns_nameservers = dns_nameservers;
	}

	public List<SubnetRoute> getRoutes() {
		return routes;
	}

	public void setRoutes(List<SubnetRoute> routes) {
		this.routes = routes;
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
		Subnet other = (Subnet) obj;
		if (!id.equals(other.getId()))
			return false;
		if (!tenant_id.equals(other.getTenant_id()))
			return false;
		if (!name.equals(other.getName()))
			return false;
		if (!network_id.equals(other.getNetwork_id()))
			return false;
		if (!ip_version.equals(other.getIp_version()))
			return false;
		if (!cidr.equals(other.getCidr()))
			return false;
		if (!gateway_ip.equals(other.getGateway_ip()))
			return false;
		if (!allocation_pools.equals(other.getAllocation_pools()))
			return false;
		if (!enable_dhcp.equals(other.isEnable_dhcp()))
			return false;
		if (!dns_nameservers.equals(other.getDns_nameservers()))
			return false;
		if (!routes.equals(other.getRoutes()))
			return false;
		if (!shared.equals(other.isShared()))
			return false;
		return true;
	}
}
