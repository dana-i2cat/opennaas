package org.opennaas.extensions.quantum.model.helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.opennaas.extensions.quantum.model.DNSNameServer;
import org.opennaas.extensions.quantum.model.IPAllocation;
import org.opennaas.extensions.quantum.model.IPAllocationPool;
import org.opennaas.extensions.quantum.model.IPAvailabilityRange;
import org.opennaas.extensions.quantum.model.Network;
import org.opennaas.extensions.quantum.model.Port;
import org.opennaas.extensions.quantum.model.QuantumModel;
import org.opennaas.extensions.quantum.model.Subnet;
import org.opennaas.extensions.quantum.model.SubnetRoute;

public class QuantumModelHelper {

	public static QuantumModel generateSampleQuantumModel() {
		QuantumModel quantumModel = new QuantumModel();

		List<Network> networks = new ArrayList<Network>(0);
		networks.add(generateSampleNetwork(1));
		networks.add(generateSampleNetwork(2));

		quantumModel.setNetworks(networks);

		return quantumModel;
	}

	public static IPAllocation generateSampleIpAllocation(int i, int j, int k) {
		IPAllocation ipAllocation_ijk = new IPAllocation();
		ipAllocation_ijk.setPort_id("ipAllocation_port_id_" + i + j + k);
		ipAllocation_ijk.setIp_address("ipAllocation_ip_address_" + i + j + k);
		ipAllocation_ijk.setSubnet_id("ipAllocation_subnet_id_" + i + j + k);
		ipAllocation_ijk.setNetwork_id("ipAllocation_network_id_" + i + j + k);
		ipAllocation_ijk.setExpiration(Calendar.getInstance());

		return ipAllocation_ijk;
	}

	public static Port generateSamplePort(int i, int j) {
		List<IPAllocation> fixed_ips_ij = new ArrayList<IPAllocation>();
		fixed_ips_ij.add(generateSampleIpAllocation(i, j, 1));
		fixed_ips_ij.add(generateSampleIpAllocation(i, j, 2));

		Port port_ij = new Port();
		port_ij.setId("port_id_" + i + j);
		port_ij.setTenant_id("port_tenant_id_" + i + j);
		port_ij.setName("port_name_" + i + j);
		port_ij.setNetwork_id("port_network_id_" + i + j);
		port_ij.setFixed_ips(fixed_ips_ij);
		port_ij.setMac_address("port_mac_address_" + i + j);
		port_ij.setAdmin_state_up(true);
		port_ij.setStatus("port_status_" + i + j);
		port_ij.setDevice_id("port_device_id_" + i + j);
		port_ij.setDevice_owner("port_device_owner_" + i + j);

		return port_ij;
	}

	public static IPAvailabilityRange generateSampleIPAvailabilityRange(int i, int j, int k, int x) {
		IPAvailabilityRange ipAvailabilityRange_ijkx = new IPAvailabilityRange();
		ipAvailabilityRange_ijkx.setAllocation_pool_id("ipAvailabilityRange_allocation_pool_id_" + i + j + k + x);
		ipAvailabilityRange_ijkx.setFirst_ip("ipAvailabilityRange_first_ip_" + i + j + k + x);
		ipAvailabilityRange_ijkx.setLast_ip("ipAvailabilityRange_last_ip_" + i + j + k + x);

		return ipAvailabilityRange_ijkx;
	}

	public static IPAllocationPool generateSampleIPAllocationPool(int i, int j, int k) {
		List<IPAvailabilityRange> available_ranges = new ArrayList<IPAvailabilityRange>();
		available_ranges.add(generateSampleIPAvailabilityRange(i, j, k, 1));
		available_ranges.add(generateSampleIPAvailabilityRange(i, j, k, 2));

		IPAllocationPool ipAllocationPool_ijk = new IPAllocationPool();
		ipAllocationPool_ijk.setId("ipAllocationPool_id_" + i + j + k);
		ipAllocationPool_ijk.setSubnet_id("ipAllocationPool_subnet_id_" + i + j + k);
		ipAllocationPool_ijk.setFirst_ip("ipAllocationPool_first_ip_" + i + j + k);
		ipAllocationPool_ijk.setLast_ip("ipAllocationPool_last_ip_" + i + j + k);
		ipAllocationPool_ijk.setAvailable_ranges(available_ranges);

		return ipAllocationPool_ijk;
	}

	public static DNSNameServer generateSampleDNSNameServer(int i, int j, int k) {
		DNSNameServer dnsNameServer_ijk = new DNSNameServer();
		dnsNameServer_ijk.setAddress("dnsNameServer_address_" + i + j + k);
		dnsNameServer_ijk.setSubnet_id("dnsNameServer_subnet_id_" + i + j + k);

		return dnsNameServer_ijk;
	}

	public static SubnetRoute generateSampleSubnetRoute(int i, int j, int k) {
		SubnetRoute subnetRoute_ijk = new SubnetRoute();
		subnetRoute_ijk.setSubnet_id("subnetRoute_subnet_id_" + i + j + k);

		return subnetRoute_ijk;
	}

	public static Subnet generateSampleSubnet(int i, int j) {
		List<IPAllocationPool> allocation_pools = new ArrayList<IPAllocationPool>();
		allocation_pools.add(generateSampleIPAllocationPool(i, j, 1));
		allocation_pools.add(generateSampleIPAllocationPool(i, j, 2));

		List<DNSNameServer> dns_nameservers = new ArrayList<DNSNameServer>();
		dns_nameservers.add(generateSampleDNSNameServer(i, j, 1));
		dns_nameservers.add(generateSampleDNSNameServer(i, j, 2));

		List<SubnetRoute> routes = new ArrayList<SubnetRoute>();
		routes.add(generateSampleSubnetRoute(i, j, 1));
		routes.add(generateSampleSubnetRoute(i, j, 2));

		Subnet subnet_ij = new Subnet();
		subnet_ij.setId("subnet_id_" + i + j);
		subnet_ij.setTenant_id("subnet_tenant_id_" + i + j);
		subnet_ij.setName("subnet_name_" + i + j);
		subnet_ij.setNetwork_id("subnet_network_id_" + i + j);
		subnet_ij.setIp_version(4);
		subnet_ij.setCidr("subnet_cidr_" + i + j);
		subnet_ij.setGateway_ip("subnet_gateway_ip_" + i + j);
		subnet_ij.setAllocation_pools(allocation_pools);
		subnet_ij.setEnable_dhcp(true);
		subnet_ij.setDns_nameservers(dns_nameservers);
		subnet_ij.setRoutes(routes);
		subnet_ij.setShared(true);

		return subnet_ij;
	}

	public static Network generateSampleNetwork(int i) {
		List<Port> ports = new ArrayList<Port>();
		ports.add(generateSamplePort(i, 1));
		ports.add(generateSamplePort(i, 2));

		List<Subnet> subnets = new ArrayList<Subnet>();
		subnets.add(generateSampleSubnet(i, 1));
		subnets.add(generateSampleSubnet(i, 2));

		Network network_i = new Network();
		network_i.setId("network_id_" + i);
		network_i.setTenant_id("network_tenant_id_" + i);
		network_i.setName("network_name_" + i);
		network_i.setPorts(ports);
		network_i.setSubnets(subnets);
		network_i.setStatus("network_status_" + i);
		network_i.setAdmin_state_up(true);
		network_i.setShared(true);

		return network_i;
	}
}
