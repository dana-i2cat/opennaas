package org.opennaas.extensions.quantum.model;

import java.util.Set;

/**
 * Represents a quantum subnet.
 * 
 * When a subnet is created the first and last entries will be created. These are used for the IP allocation.
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class Subnet implements HasId, HasTenant {

	private String					id;
	private String					tenant_id;

	private String					name;
	private String					network_id;
	private int						ip_version;
	private String					cidr;
	private String					gateway_ip;
	private Set<IPAllocationPool>	allocation_pools;
	private boolean					enable_dhcp;
	private Set<DNSNameServer>		dns_nameservers;
	private Set<SubnetRoute>		routes;
	private boolean					shared;

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

	public int getIp_version() {
		return ip_version;
	}

	public void setIp_version(int ip_version) {
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

	public Set<IPAllocationPool> getAllocation_pools() {
		return allocation_pools;
	}

	public void setAllocation_pools(Set<IPAllocationPool> allocation_pools) {
		this.allocation_pools = allocation_pools;
	}

	public boolean isEnable_dhcp() {
		return enable_dhcp;
	}

	public void setEnable_dhcp(boolean enable_dhcp) {
		this.enable_dhcp = enable_dhcp;
	}

	public Set<DNSNameServer> getDns_nameservers() {
		return dns_nameservers;
	}

	public void setDns_nameservers(Set<DNSNameServer> dns_nameservers) {
		this.dns_nameservers = dns_nameservers;
	}

	public Set<SubnetRoute> getRoutes() {
		return routes;
	}

	public void setRoutes(Set<SubnetRoute> routes) {
		this.routes = routes;
	}

	public boolean isShared() {
		return shared;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
	}
}
