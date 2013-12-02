package org.opennaas.itests.router.helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.GRETunnelConfiguration;
import org.opennaas.extensions.router.model.GRETunnelEndpoint;
import org.opennaas.extensions.router.model.GRETunnelService;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.LogicalTunnelPort;
import org.opennaas.extensions.router.model.ManagedSystemElement.OperationalStatus;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.NetworkPort.LinkTechnology;
import org.opennaas.extensions.router.model.OSPFArea;
import org.opennaas.extensions.router.model.OSPFArea.AreaType;
import org.opennaas.extensions.router.model.OSPFAreaConfiguration;
import org.opennaas.extensions.router.model.OSPFService;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
import org.opennaas.extensions.router.model.RouteCalculationService.AlgorithmType;
import org.opennaas.extensions.router.model.Service;
import org.opennaas.extensions.router.model.VLANEndpoint;
import org.opennaas.extensions.router.model.VRRPGroup;
import org.opennaas.extensions.router.model.VRRPProtocolEndpoint;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;
import org.opennaas.extensions.router.model.utils.ModelHelper;

public class ParamCreationHelper {

	/*
	 * test of an interface ethernet without vlan encapsulation
	 */
	public static Object newParamsInterfaceEthernet() {
		EthernetPort eth = new EthernetPort();
		eth.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		eth.setName("fe-0/3/2");
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address("192.168.32.1");
		ip.setSubnetMask("255.255.255.0");
		eth.addProtocolEndpoint(ip);
		return eth;
	}

	/*
	 * test of an interface ethernet without vlan encapsulation
	 */
	public static Object newParamsInterfaceLT() {
		LogicalTunnelPort ltp = new LogicalTunnelPort();
		ltp.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		ltp.setName("lt-0/3/2");
		ltp.setPeer_unit(101);
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address("192.168.32.1");
		ip.setSubnetMask("255.255.255.0");
		ltp.addProtocolEndpoint(ip);

		return ltp;
	}

	/*
	 * test of an interface ethernet without vlan encapsulation
	 */
	public static Object newParamsInterfaceLT_VLAN() {
		LogicalTunnelPort ltp = new LogicalTunnelPort();
		ltp.setLinkTechnology(NetworkPort.LinkTechnology.OTHER);
		ltp.setName("lt-0/3/2");
		ltp.setPeer_unit(101);
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address("192.168.32.1");
		ip.setSubnetMask("255.255.255.0");
		ltp.addProtocolEndpoint(ip);
		VLANEndpoint vlan = new VLANEndpoint();
		vlan.setVlanID(2);
		ltp.addProtocolEndpoint(vlan);
		return ltp;
	}

	public static Object newParamsInterfaceEtherVLAN() {
		EthernetPort eth = new EthernetPort();
		eth.setLinkTechnology(NetworkPort.LinkTechnology.OTHER);
		eth.setName("fe-0/3/2");
		eth.setPortNumber(2);
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address("192.168.32.1");
		ip.setSubnetMask("255.255.255.0");
		eth.addProtocolEndpoint(ip);
		VLANEndpoint vlan = new VLANEndpoint();
		vlan.setVlanID(2);
		eth.addProtocolEndpoint(vlan);
		return eth;
	}

	public static Object newParamsInterfaceGRE() {
		EthernetPort eth = new EthernetPort();
		eth.setName("gr-0/3/2");
		eth.setPortNumber(2);
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address("192.168.32.1");
		ip.setSubnetMask("255.255.255.0");
		eth.addProtocolEndpoint(ip);
		return eth;
	}

	public static ComputerSystem getLogicalRouter(String lrName) {
		ComputerSystem lrModel = new ComputerSystem();
		lrModel.setName(lrName);
		lrModel.setElementName(lrName);
		return lrModel;
	}

	/**
	 * @return
	 */
	public static IPProtocolEndpoint getIPProtocolEndPoint() {
		IPProtocolEndpoint ipProtocolEndpoint = new IPProtocolEndpoint();
		ipProtocolEndpoint.setIPv4Address("192.168.0.1");
		ipProtocolEndpoint.setSubnetMask("255.255.255.0");
		return ipProtocolEndpoint;
	}

	public static LogicalPort getLogicalPort() {
		LogicalPort logicalPort = new LogicalPort();
		logicalPort.setName("fe-0/3/2");
		logicalPort.setDescription("Description for the setSubInterfaceDescription test");
		return logicalPort;
	}

	/**
	 * @param interfaceNames
	 * @return List<LogicalPort>
	 * @throws Exception
	 */
	public static List<LogicalPort> getLogicalPorts(String[] interfaceNames) throws Exception {
		List<LogicalPort> interfaces = new ArrayList<LogicalPort>(interfaceNames.length);
		for (String interfaceName : interfaceNames) {
			interfaces.add(createInterface(interfaceName));
		}
		return interfaces;
	}

	/**
	 * Create an interface
	 * 
	 * @param interfaceName
	 * @return NetworkPort
	 * @throws Exception
	 */
	public static NetworkPort createInterface(String interfaceName) throws Exception {
		String argsInterface[] = new String[2];

		argsInterface = splitInterfaces(interfaceName);

		String name = argsInterface[0];
		int port = Integer.parseInt(argsInterface[1]);

		NetworkPort networkPort = new NetworkPort();
		networkPort.setName(name);
		networkPort.setPortNumber(port);
		networkPort.setLinkTechnology(LinkTechnology.OTHER);

		return networkPort;
	}

	/**
	 * Split interfaces
	 * 
	 * @param complexInterface
	 * @return String[]
	 * @throws Exception
	 */
	private static String[] splitInterfaces(String complexInterface) throws Exception {
		String[] argsInterface = new String[2];

		argsInterface = complexInterface.split("\\.");
		if (argsInterface.length != 2) {
			Exception excep = new Exception("Invalid format in interface name.");
			throw excep;
		}

		return argsInterface;
	}

	/**
	 * @param areaId
	 * @return
	 * @return OSPFArea
	 * @throws IOException
	 */
	public static OSPFArea getOSPFArea(String areaId) throws IOException {
		OSPFArea area = new OSPFArea();
		area.setAreaID(ModelHelper.ipv4StringToLong(areaId));
		return area;
	}

	/**
	 * @param routerId
	 * @return OSPFService
	 */
	public static OSPFService getOSPFService(String routerId) {
		OSPFService ospfService = new OSPFService();
		if (routerId != null) {
			ospfService.setRouterID(routerId);
		}
		return ospfService;
	}

	public static OSPFService getOSPFv3Service(String routerId) {
		OSPFService ospfService = new OSPFService();
		if (routerId != null) {
			ospfService.setRouterID(routerId);
		}
		ospfService.setAlgorithmType(AlgorithmType.OSPFV3);
		return ospfService;
	}

	/**
	 * @param areaId
	 * @param selectedAreaType
	 * @return OSPFAreaConfiguration
	 * @throws IOException
	 */
	public static OSPFAreaConfiguration getOSPFAreaConfiguration(String areaId, AreaType selectedAreaType) throws IOException {
		OSPFArea area = new OSPFArea();
		area.setAreaID(ModelHelper.ipv4StringToLong(areaId));
		area.setAreaType(selectedAreaType);

		OSPFAreaConfiguration areaConfig = new OSPFAreaConfiguration();
		areaConfig.setOSPFArea(area);
		return areaConfig;
	}

	/**
	 * Get the GRETunnelService
	 * 
	 * @return GRETunnelService
	 * @throws IOException
	 */
	public static GRETunnelService getGRETunnelService(String tunnelName, String ipv4Address, String subnetMask, String ipSource, String ipDestiny) {
		GRETunnelService greService = new GRETunnelService();
		greService.setElementName("");
		greService.setName(tunnelName);

		GRETunnelConfiguration greConfig = new GRETunnelConfiguration();
		greConfig.setSourceAddress(ipSource);
		greConfig.setDestinationAddress(ipDestiny);

		GRETunnelEndpoint gE = new GRETunnelEndpoint();
		gE.setIPv4Address(ipv4Address);
		gE.setSubnetMask(subnetMask);
		gE.setProtocolIFType(ProtocolIFType.IPV6);

		greService.setGRETunnelConfiguration(greConfig);
		greService.addProtocolEndpoint(gE);

		return greService;
	}

	public static LogicalPort newParamsConfigureStatus(String interfaceName, OperationalStatus status) {
		LogicalPort logicalPort = new LogicalPort();
		logicalPort.setName(interfaceName);
		logicalPort.setOperationalStatus(status);
		return logicalPort;
	}

	public static LogicalDevice getLogicalDevice(String nameInterface, ComputerSystem router) throws Exception {
		Iterator<LogicalDevice> iterator = router.getLogicalDevices().iterator();

		while (iterator.hasNext()) {
			LogicalDevice logicalDevice = iterator.next();
			if (logicalDevice.getName().equals(nameInterface))
				return logicalDevice;
		}

		throw new Exception("Not found logical device");

	}

	public static EthernetPort newParamsInterfaceEthernetPort(String name, int port, int vlanID) {
		EthernetPort eth = new EthernetPort();
		eth.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		eth.setName(name);
		eth.setPortNumber(port);
		VLANEndpoint vlanEndpoint = new VLANEndpoint();
		vlanEndpoint.setVlanID(vlanID);
		eth.addProtocolEndpoint(vlanEndpoint);
		eth.setDescription("capability test");

		return eth;
	}

	public static ComputerSystem newParamsLRWithInterface(String lrName) {
		ComputerSystem lrModel = new ComputerSystem();
		lrModel.setName(lrName);
		lrModel.setElementName(lrName);
		return lrModel;
	}

	public static VRRPGroup newParamsVRRPGroupWithOneEndpoint(String virtualIPAddress, String interfaceName, String interfaceIPAddress,
			String interfaceSubnetMask) {
		// VRRPGroup
		VRRPGroup vrrpGroup = new VRRPGroup();
		vrrpGroup.setVrrpName(201);
		vrrpGroup.setVirtualIPAddress(virtualIPAddress);

		// VRRPProtocolEndpoint
		VRRPProtocolEndpoint vrrProtocolEndpoint1 = new VRRPProtocolEndpoint();
		vrrProtocolEndpoint1.setPriority(100);
		vrrProtocolEndpoint1.setService(vrrpGroup);
		vrrProtocolEndpoint1.setProtocolIFType(ProtocolIFType.IPV4);
		// IPProtocolEndpoint
		IPProtocolEndpoint ipProtocolEndpoint1 = new IPProtocolEndpoint();
		ipProtocolEndpoint1.setIPv4Address(interfaceIPAddress);
		ipProtocolEndpoint1.setSubnetMask(interfaceSubnetMask);
		ipProtocolEndpoint1.setProtocolIFType(ProtocolIFType.IPV4);
		vrrProtocolEndpoint1.bindServiceAccessPoint(ipProtocolEndpoint1);

		// EthernetPort
		EthernetPort eth1 = new EthernetPort();
		eth1.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		eth1.setName(interfaceName);
		ipProtocolEndpoint1.addLogiaclPort(eth1);

		return vrrpGroup;
	}

	public static VRRPGroup newParamsVRRPGroupWithOneEndpointIPv6(String virtualIPAddress, String virtualLinkAddress, String interfaceName,
			String interfaceIPAddress) {
		// VRRPGroup
		VRRPGroup vrrpGroup = new VRRPGroup();
		vrrpGroup.setVrrpName(201);
		vrrpGroup.setVirtualIPAddress(virtualIPAddress);
		vrrpGroup.setVirtualLinkAddress(virtualLinkAddress);
		// VRRPProtocolEndpoint
		VRRPProtocolEndpoint vrrProtocolEndpoint1 = new VRRPProtocolEndpoint();
		vrrProtocolEndpoint1.setPriority(100);
		vrrProtocolEndpoint1.setService(vrrpGroup);
		vrrProtocolEndpoint1.setProtocolIFType(ProtocolIFType.IPV6);
		// IPProtocolEndpoint
		IPProtocolEndpoint ipProtocolEndpoint1 = new IPProtocolEndpoint();
		ipProtocolEndpoint1.setIPv6Address(IPUtilsHelper.getAddressFromIP(interfaceIPAddress));
		ipProtocolEndpoint1.setPrefixLength(Short.valueOf(IPUtilsHelper.getPrefixFromIp(interfaceIPAddress)));
		ipProtocolEndpoint1.setProtocolIFType(ProtocolIFType.IPV6);
		vrrProtocolEndpoint1.bindServiceAccessPoint(ipProtocolEndpoint1);

		// EthernetPort
		EthernetPort eth1 = new EthernetPort();
		eth1.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		eth1.setName(interfaceName);
		eth1.setPortNumber(1);
		ipProtocolEndpoint1.addLogiaclPort(eth1);

		return vrrpGroup;
	}

	public static GRETunnelService getGRETunnelService(String tunnelName, String ipAddress, String ipSource, String ipDestiny) {
		GRETunnelService greService = new GRETunnelService();
		greService.setElementName("");
		greService.setName(tunnelName);

		GRETunnelConfiguration greConfig = new GRETunnelConfiguration();
		greConfig.setSourceAddress(ipSource);
		greConfig.setDestinationAddress(ipDestiny);

		GRETunnelEndpoint gE = new GRETunnelEndpoint();

		if (IPUtilsHelper.isIPv4ValidAddress(ipAddress)) {
			gE.setIPv4Address(IPUtilsHelper.getAddressFromIP(ipAddress));
			String mask = IPUtilsHelper.getPrefixFromIp(ipAddress);
			gE.setSubnetMask(IPUtilsHelper.parseShortToLongIpv4NetMask(mask));
			gE.setProtocolIFType(ProtocolIFType.IPV4);
		} else {
			gE.setIPv6Address(IPUtilsHelper.getAddressFromIP(ipAddress));
			gE.setPrefixLength(Short.valueOf(IPUtilsHelper.getPrefixFromIp(ipAddress)));
			gE.setProtocolIFType(ProtocolIFType.IPV6);
		}

		greService.setGRETunnelConfiguration(greConfig);
		greService.addProtocolEndpoint(gE);

		return greService;
	}

	public static Service newParamsVRRPGroupWithTwoEndpointIPv6(String virtualIPAddress, String virtualLinkAddress, String interfaceName,
			String interfaceIPAddress, String interfaceIPLinkAddress) {
		VRRPGroup vrrpGroup = new VRRPGroup();
		vrrpGroup.setVrrpName(201);
		vrrpGroup.setVirtualIPAddress(virtualIPAddress);
		vrrpGroup.setVirtualLinkAddress(virtualLinkAddress);
		// VRRPProtocolEndpoint
		VRRPProtocolEndpoint vrrProtocolEndpoint1 = new VRRPProtocolEndpoint();
		vrrProtocolEndpoint1.setPriority(100);
		vrrProtocolEndpoint1.setService(vrrpGroup);
		vrrProtocolEndpoint1.setProtocolIFType(ProtocolIFType.IPV6);
		// IPProtocolEndpoint
		IPProtocolEndpoint ipProtocolEndpoint1 = new IPProtocolEndpoint();
		ipProtocolEndpoint1.setIPv6Address(IPUtilsHelper.getAddressFromIP(interfaceIPAddress));
		ipProtocolEndpoint1.setPrefixLength(Short.valueOf(IPUtilsHelper.getPrefixFromIp(interfaceIPAddress)));
		ipProtocolEndpoint1.setProtocolIFType(ProtocolIFType.IPV6);
		vrrProtocolEndpoint1.bindServiceAccessPoint(ipProtocolEndpoint1);
		// Another IPProtocolEndpoint
		IPProtocolEndpoint ipProtocolEndpoint2 = new IPProtocolEndpoint();
		ipProtocolEndpoint2.setIPv6Address(IPUtilsHelper.getAddressFromIP(interfaceIPLinkAddress));
		ipProtocolEndpoint2.setPrefixLength(Short.valueOf(IPUtilsHelper.getPrefixFromIp(interfaceIPLinkAddress)));
		ipProtocolEndpoint2.setProtocolIFType(ProtocolIFType.IPV6);
		vrrProtocolEndpoint1.bindServiceAccessPoint(ipProtocolEndpoint2);

		// EthernetPort
		EthernetPort eth1 = new EthernetPort();
		eth1.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		eth1.setName(interfaceName);
		eth1.setPortNumber(1);
		ipProtocolEndpoint1.addLogiaclPort(eth1);

		return vrrpGroup;
	}

	public static Service newParamsVRRPGroupWithThreeEndpointIPv6(String virtualIPAddress, String virtualLinkAddress, String interfaceName,
			String interfaceIPAddress, String interfaceIPLinkAddress, String advertisementPrefix) {
		VRRPGroup vrrpGroup = new VRRPGroup();
		vrrpGroup.setVrrpName(201);
		vrrpGroup.setVirtualIPAddress(virtualIPAddress);
		vrrpGroup.setVirtualLinkAddress(virtualLinkAddress);
		// VRRPProtocolEndpoint
		VRRPProtocolEndpoint vrrProtocolEndpoint1 = new VRRPProtocolEndpoint();
		vrrProtocolEndpoint1.setPriority(100);
		vrrProtocolEndpoint1.setService(vrrpGroup);
		vrrProtocolEndpoint1.setProtocolIFType(ProtocolIFType.IPV6);
		// IPProtocolEndpoint
		IPProtocolEndpoint ipProtocolEndpoint1 = new IPProtocolEndpoint();
		ipProtocolEndpoint1.setIPv6Address(IPUtilsHelper.getAddressFromIP(interfaceIPAddress));
		ipProtocolEndpoint1.setPrefixLength(Short.valueOf(IPUtilsHelper.getPrefixFromIp(interfaceIPAddress)));
		ipProtocolEndpoint1.setProtocolIFType(ProtocolIFType.IPV6);
		vrrProtocolEndpoint1.bindServiceAccessPoint(ipProtocolEndpoint1);
		// Another IPProtocolEndpoint
		IPProtocolEndpoint ipProtocolEndpoint2 = new IPProtocolEndpoint();
		ipProtocolEndpoint2.setIPv6Address(IPUtilsHelper.getAddressFromIP(interfaceIPLinkAddress));
		ipProtocolEndpoint2.setPrefixLength(Short.valueOf(IPUtilsHelper.getPrefixFromIp(interfaceIPLinkAddress)));
		ipProtocolEndpoint2.setProtocolIFType(ProtocolIFType.IPV6);
		vrrProtocolEndpoint1.bindServiceAccessPoint(ipProtocolEndpoint2);

		// Another IPprotocolEndpoint containing router-advertisement
		IPProtocolEndpoint ipProtocolEndpoint3 = new IPProtocolEndpoint();
		ipProtocolEndpoint3.setIPv6Address(IPUtilsHelper.getAddressFromIP(advertisementPrefix));
		ipProtocolEndpoint3.setPrefixLength(Short.valueOf(IPUtilsHelper.getPrefixFromIp(advertisementPrefix)));
		ipProtocolEndpoint3.setProtocolIFType(ProtocolIFType.IPV6);
		vrrProtocolEndpoint1.bindServiceAccessPoint(ipProtocolEndpoint3);

		// EthernetPort
		EthernetPort eth1 = new EthernetPort();
		eth1.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		eth1.setName(interfaceName);
		eth1.setPortNumber(1);
		ipProtocolEndpoint1.addLogiaclPort(eth1);

		return vrrpGroup;
	}

	public static IPProtocolEndpoint getIPProtocolEndPointIPv6() {
		IPProtocolEndpoint ipProtocolEndpoint = new IPProtocolEndpoint();
		ipProtocolEndpoint.setIPv6Address("fedc:34:ff::af");
		ipProtocolEndpoint.setPrefixLength((short) 64);
		return ipProtocolEndpoint;
	}
}
