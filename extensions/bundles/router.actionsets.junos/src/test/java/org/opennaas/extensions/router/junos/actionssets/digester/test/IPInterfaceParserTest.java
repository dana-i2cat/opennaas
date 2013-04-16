package org.opennaas.extensions.router.junos.actionssets.digester.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.opennaas.extensions.router.junos.commandsets.digester.IPInterfaceParser;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.GREService;
import org.opennaas.extensions.router.model.GRETunnelConfiguration;
import org.opennaas.extensions.router.model.GRETunnelEndpoint;
import org.opennaas.extensions.router.model.GRETunnelService;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
import org.opennaas.extensions.router.model.Service;
import org.opennaas.extensions.router.model.ServiceAccessPoint;
import org.opennaas.extensions.router.model.System;
import org.opennaas.extensions.router.model.VLANEndpoint;
import org.opennaas.extensions.router.model.VRRPGroup;
import org.opennaas.extensions.router.model.VRRPProtocolEndpoint;

public class IPInterfaceParserTest {
	private final Log	log	= LogFactory.getLog(IPInterfaceParserTest.class);

	@Test
	public void testStatusParserTest() throws Exception {
		System model = createSampleModel();
		IPInterfaceParser parser = new IPInterfaceParser(model);

		String message = readStringFromFile("/parsers/getconfig.xml");

		parser.init();
		parser.configurableParse(new ByteArrayInputStream(message.getBytes()));
		String str = "\n";

		model = parser.getModel();

		str += printGRETunnels(model);

		for (LogicalDevice device : model.getLogicalDevices()) {
			if (device instanceof EthernetPort) {
				EthernetPort port = (EthernetPort) device;
				Assert.assertNotNull("OperationalStatus must be set", port.getOperationalStatus());

				str += "- EthernetPort: " + '\n';
				str += port.getName() + '.' + port.getPortNumber() + '\n';
				str += port.getOperationalStatus();
				str += '\n';
				for (ProtocolEndpoint protocolEndpoint : port.getProtocolEndpoint()) {
					if (protocolEndpoint instanceof IPProtocolEndpoint) {
						IPProtocolEndpoint ipProtocol = (IPProtocolEndpoint) protocolEndpoint;
						str += "ipv4: " + ipProtocol.getIPv4Address() + '\n';
						str += "ipv6: " + ipProtocol.getIPv6Address() + '\n';
					}
				}

			}
			else {
				str += "not searched device";
			}

		}

		log.info(str);
	}

	@Test
	public void multipleIPsPerInterfaceTest() throws Exception {
		System model = new ComputerSystem();
		IPInterfaceParser parser = new IPInterfaceParser(model);

		String message = readStringFromFile("/parsers/ethernetPortWIthMultipleIPs.xml");

		parser.init();
		parser.configurableParse(new ByteArrayInputStream(message.getBytes()));
		String str = "\n";

		model = parser.getModel();
		Assert.assertEquals("Model shouldn't have any service.", 0, model.getHostedService().size());

		List<LogicalDevice> logicalDevices = model.getLogicalDevices();
		Assert.assertEquals("Model should have 4 differents logical devices.", 5, logicalDevices.size());

		for (LogicalDevice device : logicalDevices) {
			Assert.assertTrue("Model should only have Ethernet ports.", device instanceof EthernetPort);
			EthernetPort ethPort = (EthernetPort) device;

			if (ethPort.getName().equals("ge-2/0/0") && ethPort.getPortNumber() == 12) {
				Assert.assertNotNull("Ethernet Port ge-2/0/0.12 should have a description.", ethPort.getDescription());
				List<ProtocolEndpoint> protocolEndpoints = ethPort.getProtocolEndpoint();
				Assert.assertEquals("Ethernet Port ge-2/0/0.12 should have 3 ProtocolEndpoints", 3, protocolEndpoints.size());

				List<IPProtocolEndpoint> ipEndpoints = ethPort.getProtocolEndpointsByType(IPProtocolEndpoint.class);
				Assert.assertEquals("Ethernet Port ge-2/0/0.12 should have 2 IPProtocolEndpoints", 2, ipEndpoints.size());

				List<VLANEndpoint> vlanEndpoints = ethPort.getProtocolEndpointsByType(VLANEndpoint.class);
				Assert.assertEquals("Ethernet Port ge-2/0/0.12 should have 1 VlanProtocolEndpoint", 1, vlanEndpoints.size());

				IPProtocolEndpoint ipEndpoint = ipEndpoints.get(0);
				Assert.assertEquals("Ethernet Port ge-2/0/0.12 should only have IPv4 IPProtocolEndpoints", ProtocolIFType.IPV4,
						ipEndpoint.getProtocolIFType()
						);
				Assert.assertNull("Ethernet Port ge-2/0/0.12 should not have configured IPv6 addresses", ipEndpoint.getIPv6Address());
				Assert.assertEquals("Ethernet Port ge-2/0/0.12 should not have configured IPv6 prefix length", 0, ipEndpoint.getPrefixLength());
				Assert.assertTrue(ipEndpoint.getIPv4Address() + " is not a valid address for Ethernet Port ge-2/0/0.12", ipEndpoint.getIPv4Address()
						.equals("193.1.190.133") || ipEndpoint.getIPv4Address().equals("193.1.190.134"));
				Assert.assertEquals("Ethernet Port ge-2/0/0.12 should have configured IPv4 with /30 mask", "255.255.255.252",
						ipEndpoint.getSubnetMask());

				ipEndpoint = ipEndpoints.get(1);
				Assert.assertEquals("Ethernet Port ge-2/0/0.12 should only have IPv4 IPProtocolEndpoints", ipEndpoint.getProtocolIFType(),
						ProtocolIFType.IPV4);
				Assert.assertNull("Ethernet Port ge-2/0/0.12 should not have configured IPv6 addresses", ipEndpoint.getIPv6Address());
				Assert.assertEquals("Ethernet Port ge-2/0/0.12 should not have configured IPv6 prefix length", 0, ipEndpoint.getPrefixLength());
				Assert.assertTrue(ipEndpoint.getIPv4Address() + " is not a valid address for Ethernet Port ge-2/0/0.12", ipEndpoint.getIPv4Address()
						.equals("193.1.190.133") || ipEndpoint.getIPv4Address().equals("193.1.190.134"));
				Assert.assertEquals("Ethernet Port ge-2/0/0.12 should have configured IPv4 with /30 mask", "255.255.255.252",
						ipEndpoint.getSubnetMask());

				Assert.assertEquals("Ethernet Port ge-2/0/0.12 should only have VlanEndpoint with VlanID 12", 12, vlanEndpoints.get(0).getVlanID());

			}
			if (ethPort.getName().equals("ge-2/0/0") && ethPort.getPortNumber() == 201) {
				Assert.assertNotNull("Ethernet Port ge-2/0/0.201 should have a description.", ethPort.getDescription());
				List<ProtocolEndpoint> protocolEndpoints = ethPort.getProtocolEndpoint();
				Assert.assertEquals("Ethernet Port ge-2/0/0.201 should have 2 ProtocolEndpoints", 2, protocolEndpoints.size());

				List<IPProtocolEndpoint> ipEndpoints = ethPort.getProtocolEndpointsByType(IPProtocolEndpoint.class);
				Assert.assertEquals("Ethernet Port ge-2/0/0.201 should have 1 IPProtocolEndpoints", 1, ipEndpoints.size());

				List<VLANEndpoint> vlanEndpoints = ethPort.getProtocolEndpointsByType(VLANEndpoint.class);
				Assert.assertEquals("Ethernet Port ge-2/0/0.201 should have 1 VlanProtocolEndpoint", 1, vlanEndpoints.size());

				IPProtocolEndpoint ipEndpoint = ipEndpoints.get(0);
				Assert.assertEquals("Ethernet Port ge-2/0/0.201 should only have IPv4 IPProtocolEndpoints", ProtocolIFType.IPV4,
						ipEndpoint.getProtocolIFType());
				Assert.assertNull("Ethernet Port ge-2/0/0.201 should not have configured IPv6 addresses", ipEndpoint.getIPv6Address());
				Assert.assertEquals("Ethernet Port ge-2/0/0.201 should not have configured IPv6 prefix length", 0, ipEndpoint.getPrefixLength());
				Assert.assertTrue(ipEndpoint.getIPv4Address() + " is not a valid address for Ethernet Port ge-2/0/0.201", ipEndpoint.getIPv4Address()
						.equals("192.168.1.1"));
				Assert.assertEquals("Ethernet Port ge-2/0/0.201 should have configured IPv4 with /24 mask", "255.255.255.0",
						ipEndpoint.getSubnetMask());

				Assert.assertEquals("Ethernet Port ge-2/0/0.201 should only have VlanEndpoint with VlanID 201", 201, vlanEndpoints.get(0)
						.getVlanID());
			}

			if (ethPort.getName().equals("ge-2/0/0") && ethPort.getPortNumber() == 202) {
				Assert.assertNotNull("Ethernet Port ge-2/0/0.202 should have a description.", ethPort.getDescription());
				List<ProtocolEndpoint> protocolEndpoints = ethPort.getProtocolEndpoint();
				Assert.assertEquals("Ethernet Port ge-2/0/0.202 should have 2 ProtocolEndpoints", 2, protocolEndpoints.size());

				List<IPProtocolEndpoint> ipEndpoints = ethPort.getProtocolEndpointsByType(IPProtocolEndpoint.class);
				Assert.assertEquals("Ethernet Port ge-2/0/0.202 should have 1 IPProtocolEndpoints", 1, ipEndpoints.size());

				List<VLANEndpoint> vlanEndpoints = ethPort.getProtocolEndpointsByType(VLANEndpoint.class);
				Assert.assertEquals("Ethernet Port ge-2/0/0.202 should have 1 VlanProtocolEndpoint", 1, vlanEndpoints.size());

				IPProtocolEndpoint ipEndpoint = ipEndpoints.get(0);
				Assert.assertEquals("Ethernet Port ge-2/0/0.202 should only have IPv6 IPProtocolEndpoints", ProtocolIFType.IPV6,
						ipEndpoint.getProtocolIFType());
				Assert.assertNull("Ethernet Port ge-2/0/0.202 should not have configured IPv4 addresses", ipEndpoint.getIPv4Address());
				Assert.assertNull("Ethernet Port ge-2/0/0.202 should not have configured IPv4 subnet mask", ipEndpoint.getSubnetMask());
				Assert.assertTrue(ipEndpoint.getIPv6Address() + " is not a valid address for Ethernet Port ge-2/0/0.201", ipEndpoint.getIPv6Address()
						.equals("FEDC:43::32:50"));
				Assert.assertEquals("Ethernet Port ge-2/0/0.202 should have configured IPv6 with /64 prefix", 64,
						ipEndpoint.getPrefixLength());

				Assert.assertEquals("Ethernet Port ge-2/0/0.202 should only have VlanEndpoint with VlanID 202", 202, vlanEndpoints.get(0)
						.getVlanID());
			}

			if (ethPort.getName().equals("fe-0/3/2") && ethPort.getPortNumber() == 0) {
				Assert.assertNotNull("Ethernet Port fe-0/3/2.0 should have a description.", ethPort.getDescription());
				List<ProtocolEndpoint> protocolEndpoints = ethPort.getProtocolEndpoint();
				Assert.assertEquals("Ethernet Port fe-0/3/2.0 should have 3 ProtocolEndpoints", 3, protocolEndpoints.size());

				List<IPProtocolEndpoint> ipEndpoints = ethPort.getProtocolEndpointsByType(IPProtocolEndpoint.class);
				Assert.assertEquals("Ethernet Port fe-0/3/2.0 should have 2 IPProtocolEndpoints", 2, ipEndpoints.size());

				List<VLANEndpoint> vlanEndpoints = ethPort.getProtocolEndpointsByType(VLANEndpoint.class);
				Assert.assertEquals("Ethernet Port fe-0/3/2.0 should have 1 VlanProtocolEndpoint", 1, vlanEndpoints.size());

				IPProtocolEndpoint ipEndpoint = ipEndpoints.get(0);
				Assert.assertEquals("Ethernet Port fe-0/3/2.0 should only have IPv6 IPProtocolEndpoints", ProtocolIFType.IPV6,
						ipEndpoint.getProtocolIFType());
				Assert.assertNull("Ethernet Port fe-0/3/2.0 should not have configured IPv4 addresses", ipEndpoint.getIPv4Address());
				Assert.assertNull("Ethernet Port fe-0/3/2.0 should not have configured IPv4 subnet mask", ipEndpoint.getSubnetMask());
				Assert.assertTrue(ipEndpoint.getIPv6Address() + " is not a valid address for Ethernet Port fe-0/3/2.0", ipEndpoint.getIPv6Address()
						.equals("FEDC:43::32:1") || ipEndpoint.getIPv6Address().equals("FEDC:43::32:2"));
				Assert.assertEquals("Ethernet Port fe-0/3/2.0 should have configured IPv6 with /64 prefix length", 64,
						ipEndpoint.getPrefixLength());

				ipEndpoint = ipEndpoints.get(1);
				Assert.assertEquals("Ethernet Port fe-0/3/2.0 should only have IPv6 IPProtocolEndpoints", ProtocolIFType.IPV6,
						ipEndpoint.getProtocolIFType());
				Assert.assertNull("Ethernet Port fe-0/3/2.0 should not have configured IPv4 addresses", ipEndpoint.getIPv4Address());
				Assert.assertNull("Ethernet Port fe-0/3/2.0 should not have configured IPv4 subnet mask", ipEndpoint.getSubnetMask());
				Assert.assertTrue(ipEndpoint.getIPv6Address() + " is not a valid address for Ethernet Port fe-0/3/2.0", ipEndpoint.getIPv6Address()
						.equals("FEDC:43::32:1") || ipEndpoint.getIPv6Address().equals("FEDC:43::32:2"));
				Assert.assertEquals("Ethernet Port fe-0/3/2.0 should have configured IPv6 with /64 prefix length", 64,
						ipEndpoint.getPrefixLength());

				Assert.assertEquals("Ethernet Port fe-0/3/2.0 should only have VlanEndpoint with VlanID 32", 32, vlanEndpoints.get(0).getVlanID());

			}

			if (ethPort.getName().equals("fe-0/3/2") && ethPort.getPortNumber() == 1) {
				Assert.assertNotNull("Ethernet Port fe-0/3/2.1 should have a description.", ethPort.getDescription());
				List<ProtocolEndpoint> protocolEndpoints = ethPort.getProtocolEndpoint();
				Assert.assertEquals("Ethernet Port fe-0/3/2.1 should have 3 ProtocolEndpoints", 3, protocolEndpoints.size());

				List<IPProtocolEndpoint> ipEndpoints = ethPort.getProtocolEndpointsByType(IPProtocolEndpoint.class);
				Assert.assertEquals("Ethernet Port fe-0/3/2.1 should have 2 IPProtocolEndpoints", 2, ipEndpoints.size());

				List<VLANEndpoint> vlanEndpoints = ethPort.getProtocolEndpointsByType(VLANEndpoint.class);
				Assert.assertEquals("Ethernet Port fe-0/3/2.1 should have 1 VlanProtocolEndpoint", 1, vlanEndpoints.size());

				IPProtocolEndpoint ipEndpoint1 = ipEndpoints.get(0);
				IPProtocolEndpoint ipEndpoint2 = ipEndpoints.get(1);
				Assert.assertNotNull("ProtocolIFType should be set in IPProtocolEndpoint of Ethernet Port fe-0/3/2.1.",
						ipEndpoint1.getProtocolIFType());
				Assert.assertNotNull("ProtocolIFType should be set in IPProtocolEndpoint of Ethernet Port fe-0/3/2.1.",
						ipEndpoint2.getProtocolIFType());
				Assert.assertFalse("IPProtocolEndpoints should have differents protocols in Ethernet Port fe-0/3/2.1",
						ipEndpoint1.getProtocolIFType().equals(ipEndpoint2.getProtocolIFType()));

				for (IPProtocolEndpoint ipEndpoint : ipEndpoints) {
					if (ipEndpoint.getProtocolIFType().equals(ProtocolIFType.IPV4)) {

						Assert.assertNull("IPv4 IPProtocolEndpoint in Ethernet Port fe-0/3/2.1 should not have configured IPv6 addresses",
								ipEndpoint.getIPv6Address());
						Assert.assertEquals("IPv4 IPProtocolEndpoint in Ethernet Port fe-0/3/2.1 should not have configured IPv6 prefix length", 0,
								ipEndpoint.getPrefixLength());
						Assert.assertEquals("IPv4 IPProtocolEndpoint in Ethernet Port fe-0/3/2.1 should have the address 192.168.1.1.",
								"192.168.1.1", ipEndpoint.getIPv4Address());
						Assert.assertEquals("IPv4 IPProtocolEndpoint in Ethernet Port fe-0/3/2.1 should have the mask 255.255.255.0",
								"255.255.255.0", ipEndpoint.getSubnetMask());
					}

					if (ipEndpoint.getProtocolIFType().equals(ProtocolIFType.IPV6)) {

						Assert.assertNull("IPv6 IPProtocolEndpoint in Ethernet Port fe-0/3/2.1 should not have configured IPv4 addresses",
								ipEndpoint.getIPv4Address());
						Assert.assertNull("IPv6 IPProtocolEndpoint in Ethernet Port fe-0/3/2.1 should not have configured IPv4 subnet mask",
								ipEndpoint.getSubnetMask());
						Assert.assertEquals("IPv6 IPProtocolEndpoint in Ethernet Port fe-0/3/2.1 should have the address FEDC:43::32:3",
								"FEDC:43::32:3", ipEndpoint.getIPv6Address());
						Assert.assertEquals("IPv6 IPProtocolEndpoint in Ethernet Port fe-0/3/2.1 should have a prefix lenght of 64",
								64, ipEndpoint.getPrefixLength());
					}

				}

				Assert.assertEquals("Ethernet Port fe-0/3/2.1 should only have VlanEndpoint with VlanID 35", 35, vlanEndpoints.get(0).getVlanID());

			}

		}

	}

	@Test
	public void testGREIsNotCreatedIfNoUnitConfigurated() throws Exception {

		System model = createSampleModel();
		IPInterfaceParser parser = new IPInterfaceParser(model);

		String message = readStringFromFile("/parsers/getconfigWithoutGREUnit.xml");

		parser.init();
		parser.configurableParse(new ByteArrayInputStream(message.getBytes()));
		String str = "\n";

		model = parser.getModel();

		List<GRETunnelService> greServices = model.getAllHostedServicesByType(new GRETunnelService());
		Assert.assertTrue("There should be no GREService if no gre unit is configured", greServices.isEmpty());

		log.info(str);
	}

	@Test
	public void testGREIsNotCreatedIfNoGRE() throws Exception {

		System model = createSampleModel();
		IPInterfaceParser parser = new IPInterfaceParser(model);

		String message = readStringFromFile("/parsers/getconfigWithoutGRE.xml");

		parser.init();
		parser.configurableParse(new ByteArrayInputStream(message.getBytes()));
		String str = "\n";

		model = parser.getModel();

		List<GRETunnelService> greServices = model.getAllHostedServicesByType(new GRETunnelService());
		Assert.assertTrue("There should be no GREService if no gre is configured", greServices.isEmpty());

		log.info(str);
	}

	@Test
	public void testGreServiceCreatedwithGRE() throws Exception {
		System model = createSampleModel();
		IPInterfaceParser parser = new IPInterfaceParser(model);

		String message = readStringFromFile("/parsers/getconfig.xml");
		parser.init();
		parser.configurableParse(new ByteArrayInputStream(message.getBytes()));
		String str = "\n";

		List<GREService> greServiceList = model.getAllHostedServicesByType(new GREService());
		Assert.assertEquals("There should be a GREService if a gre interface is present.", greServiceList.size(), 1);

		GREService greService = greServiceList.get(0);

		Assert.assertEquals("There GREService name should be gr-0/1/0 and not " + greService.getName(), greService.getName(), "gr-0/1/0");
		Assert.assertTrue("The number of GRE Services in the model are not the same as the number of GRE in the config file", greService
				.getProtocolEndpoint().size() == 1);

		ProtocolEndpoint pE = greService.getProtocolEndpoint().get(0);
		log.info(pE.getName());
		Assert.assertEquals("The name of the GRE interface should be gr-0/1/0.0 and not " + pE.getName(), pE.getName(), "gr-0/1/0.0");

	}

	private String printGRETunnels(System model) {
		String str = "";
		int greCount = 0;
		for (Service service : model.getHostedService()) {
			if (service instanceof GRETunnelService) {
				greCount++;
				String name = ((GRETunnelService) service).getName();
				GRETunnelConfiguration gretunnelConfiguration = ((GRETunnelService) service).getGRETunnelConfiguration();
				str += " - GRE Tunnel Configuration : " + "\n";

				String source = gretunnelConfiguration.getSourceAddress();
				String destination = gretunnelConfiguration.getDestinationAddress();
				int key = gretunnelConfiguration.getKey();
				str += name + "\n";
				str += "key : " + String.valueOf(key) + "\n";
				str += "source : " + source + "\n";
				str += "destination : " + destination + "\n";

				int protocolEpCount = 0;
				for (ProtocolEndpoint pE : service.getProtocolEndpoint()) {
					if (pE instanceof GRETunnelEndpoint) {
						protocolEpCount++;
						GRETunnelEndpoint gE = (GRETunnelEndpoint) pE;
						String ip = gE.getIPv4Address();
						if (ip == null) {
							ip = gE.getIPv6Address();
							str += "ipv6 :" + ip + "\n";
						} else {
							str += "ipv4 : " + ip + "\n";
						}

					}
				}
				Assert.assertTrue(protocolEpCount > 0);
			}
		}
		Assert.assertTrue("There is only one gre, but it's not configured (we know in config there's only one)", greCount == 1);

		return str;
	}

	private System createSampleModel() {
		System model = new ComputerSystem();

		// create interfaces to check OSPFEnpoint is created on them
		EthernetPort interface1 = new EthernetPort();
		interface1.setName("fe-0/1/3");
		interface1.setPortNumber(0);
		IPProtocolEndpoint ipProtocolEndpoint = new IPProtocolEndpoint();
		ipProtocolEndpoint.setIPv4Address("192.168.1.3");
		ipProtocolEndpoint.setSubnetMask("255.255.255.0");
		interface1.addProtocolEndpoint(ipProtocolEndpoint);

		model.addLogicalDevice(interface1);

		return model;
	}

	@Test
	public void testVRRPConfigParserTest() throws Exception {
		System model = createSampleModel();
		IPInterfaceParser parser = new IPInterfaceParser(model);

		String message = readStringFromFile("/parsers/getConfigWithVRRP.xml");

		parser.init();
		parser.configurableParse(new ByteArrayInputStream(message.getBytes()));
		String str = "\n";

		model = parser.getModel();

		for (LogicalDevice device : model.getLogicalDevices()) {
			if (device instanceof NetworkPort) {
				NetworkPort port = (NetworkPort) device;

				str += "- NetworkPort: " + '\n';
				str += port.getName() + '.' + port.getPortNumber() + '\n';
				for (ProtocolEndpoint protocolEndpoint : port.getProtocolEndpoint()) {
					if (protocolEndpoint instanceof IPProtocolEndpoint) {
						IPProtocolEndpoint ipProtocol = (IPProtocolEndpoint) protocolEndpoint;
						for (ServiceAccessPoint bindedProtocolEndpoint : ipProtocol
								.getBindedServiceAccessPoints()) {
							if (bindedProtocolEndpoint instanceof VRRPProtocolEndpoint) {
								str += "VRRP endpoint ==> ";
								VRRPProtocolEndpoint vrrpProtocolEndpoint = (VRRPProtocolEndpoint) bindedProtocolEndpoint;
								str += "priority: " + vrrpProtocolEndpoint.getPriority() + ", ";
								Assert.assertEquals(ProtocolIFType.IPV4, ((VRRPProtocolEndpoint) bindedProtocolEndpoint).getProtocolIFType());
								str += "protocol: " + ((VRRPProtocolEndpoint) bindedProtocolEndpoint).getProtocolIFType() + ", ";
								Service service = vrrpProtocolEndpoint.getService();
								Assert.assertTrue("VRRPProtocolEndpoint must have a Service instace of VRRPGroup", service instanceof VRRPGroup);
								VRRPGroup vrrpGroup = (VRRPGroup) service;
								Assert.assertNotNull("VRRPGroup must have a VRRP name (ID)", vrrpGroup.getVrrpName());
								str += "VRRP group: " + vrrpGroup.getVrrpName() + ", ";
								Assert.assertNotNull("VRRPGroup must have a virtual IP address", vrrpGroup.getVirtualIPAddress());
								str += "virtual IP address: " + vrrpGroup.getVirtualIPAddress();
								str += '\n';
							}
						}
					}
				}
				str += '\n';
			} else {
				str += "not searched device";
			}

		}

		log.info(str);

		int vrrpGroups = 0;
		int vrrpProtocolEndpoints = 0;
		List<Service> routerServices = model.getHostedService();
		for (Service service : routerServices) {
			if (service instanceof VRRPGroup) {
				vrrpGroups++;
				VRRPGroup vrrpGroup = (VRRPGroup) service;
				Assert.assertTrue("Our VRRPGroup vrrpName must be 201", vrrpGroup.getVrrpName() == 201);
				Assert.assertTrue("Our VRRPGroup virtual IP address mult be 193.1.190.161", vrrpGroup.getVirtualIPAddress().equals("193.1.190.161"));
				List<ProtocolEndpoint> protocolEndpoints = vrrpGroup.getProtocolEndpoint();
				for (ProtocolEndpoint protocolEndpoint : protocolEndpoints) {
					Assert.assertTrue("ProtocolEndpoint's binded to VRRPGroup must be instances of VRRPProtocolEndpoint",
							protocolEndpoint instanceof VRRPProtocolEndpoint);
					vrrpProtocolEndpoints++;
					VRRPProtocolEndpoint vrrpProtocolEndpoint = (VRRPProtocolEndpoint) protocolEndpoint;
					Assert.assertTrue("Our VRRPProtocolEndpoint's must have priority 100 or 200",
							vrrpProtocolEndpoint.getPriority() == 100 || vrrpProtocolEndpoint.getPriority() == 200);
				}
			}
		}
		Assert.assertTrue("Our configuration must have one VRRPGroup", vrrpGroups == 1);
		Assert.assertTrue("Our configuration must have two VRRPProtocolEndpoint's", vrrpProtocolEndpoints == 2);
	}

	@Test
	public void testVRRPIPv6ConfigParser() throws Exception {
		System model = createSampleModel();
		IPInterfaceParser parser = new IPInterfaceParser(model);

		String message = readStringFromFile("/parsers/getConfigWithVRRPIPv6.xml");

		parser.init();
		parser.configurableParse(new ByteArrayInputStream(message.getBytes()));
		model = parser.getModel();

		List<VRRPGroup> vrrpList = model.getAllHostedServicesByType(new VRRPGroup());
		Assert.assertEquals("Model should have only one VRRPGroup.", 1, vrrpList.size());

		VRRPGroup group = vrrpList.get(0);
		Assert.assertEquals("VRRPGroup's name should be 201", 201, group.getVrrpName());
		Assert.assertEquals("VRRPGroup's virtual address should be fec0::5:0:0:7", "fec0::5:0:0:7", group.getVirtualIPAddress());
		Assert.assertEquals("VRRPGroup's virtual link address should be fe80::5:0:0:7", "fe80::5:0:0:7", group.getVirtualLinkAddress());

		List<ProtocolEndpoint> protocolEndpointList = group.getProtocolEndpoint();
		Assert.assertEquals("VRRPGroup service should have 2 protocol endpoints. ", 2, protocolEndpointList.size());

		ProtocolEndpoint pE = protocolEndpointList.get(0);
		Assert.assertTrue("VRRPGroup should only have VRRPProtocolEndpoints.", pE instanceof VRRPProtocolEndpoint);

		VRRPProtocolEndpoint vE = (VRRPProtocolEndpoint) pE;
		Assert.assertEquals("First VRRPProtocolEndpoint should have a priority value of 200.", 200, vE.getPriority());
		Assert.assertEquals("First VRRPProtocolEndpoint should have been configured with IPv6 protocol.", ProtocolIFType.IPV6, vE.getProtocolIFType());

		pE = protocolEndpointList.get(1);
		Assert.assertTrue("VRRPGroup should only have VRRPProtocolEndpoints", pE instanceof VRRPProtocolEndpoint);

		vE = (VRRPProtocolEndpoint) pE;
		Assert.assertEquals("Second VRRPProtocolEndpoint should have a priority value of 100.", 100, vE.getPriority());
		Assert.assertEquals("Second VRRPProtocolEndpoint should have been configured with IPv6 protocol.", ProtocolIFType.IPV6,
				vE.getProtocolIFType());

		String str = "\n";
		for (LogicalDevice device : model.getLogicalDevices()) {
			if (device instanceof NetworkPort) {
				NetworkPort port = (NetworkPort) device;

				str += "- NetworkPort: " + '\n';
				str += port.getName() + '.' + port.getPortNumber() + '\n';
				for (ProtocolEndpoint protocolEndpoint : port.getProtocolEndpoint()) {
					if (protocolEndpoint instanceof IPProtocolEndpoint) {
						IPProtocolEndpoint ipProtocol = (IPProtocolEndpoint) protocolEndpoint;
						for (ServiceAccessPoint bindedProtocolEndpoint : ipProtocol
								.getBindedServiceAccessPoints()) {
							if (bindedProtocolEndpoint instanceof VRRPProtocolEndpoint) {
								str += "VRRP endpoint ==> ";
								VRRPProtocolEndpoint vrrpProtocolEndpoint = (VRRPProtocolEndpoint) bindedProtocolEndpoint;
								str += "priority: " + vrrpProtocolEndpoint.getPriority() + ", ";
								Assert.assertEquals(ProtocolIFType.IPV6, ((VRRPProtocolEndpoint) bindedProtocolEndpoint).getProtocolIFType());
								str += "protocol: " + ((VRRPProtocolEndpoint) bindedProtocolEndpoint).getProtocolIFType() + ", ";
								Service service = vrrpProtocolEndpoint.getService();
								Assert.assertTrue("VRRPProtocolEndpoint must have a Service instace of VRRPGroup", service instanceof VRRPGroup);
								VRRPGroup vrrpGroup = (VRRPGroup) service;
								Assert.assertNotNull("VRRPGroup must have a VRRP name (ID)", vrrpGroup.getVrrpName());
								str += "VRRP group: " + vrrpGroup.getVrrpName() + ", ";
								Assert.assertNotNull("VRRPGroup must have a virtual IP address", vrrpGroup.getVirtualIPAddress());
								str += "virtual IP address: " + vrrpGroup.getVirtualIPAddress() + ", ";
								str += "virtual link address: " + vrrpGroup.getVirtualLinkAddress();
								str += '\n';
							}
						}
					}
				}
				str += '\n';
			} else {
				str += "not searched device";
			}

		}

		log.info(str);

	}

	@Ignore
	@Test
	public void testMultipleVRRPGroupsConfigParser() {
		// TODO
	}

	/**
	 * Simple parser. It was used for proves with xml files
	 * 
	 * @param stream
	 * @return
	 */
	private String readStringFromFile(String pathFile) throws Exception {
		String answer = null;
		InputStream inputFile = getClass().getResourceAsStream(pathFile);
		InputStreamReader streamReader = new InputStreamReader(inputFile);
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(streamReader);
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		answer = fileData.toString();

		return answer;
	}
}