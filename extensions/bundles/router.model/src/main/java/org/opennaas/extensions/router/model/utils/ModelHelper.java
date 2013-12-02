package org.opennaas.extensions.router.model.utils;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
import org.opennaas.extensions.router.model.System;
import org.opennaas.extensions.router.model.VRRPGroup;
import org.opennaas.extensions.router.model.VRRPProtocolEndpoint;

public class ModelHelper {

	public static List<NetworkPort> getInterfaces(System system) {
		List<NetworkPort> ports = new ArrayList<NetworkPort>();
		for (LogicalDevice dev : system.getLogicalDevices()) {
			if (dev instanceof NetworkPort) {
				ports.add((NetworkPort) dev);
			}
		}
		return ports;
	}

	public static long ipv4StringToLong(String ip) throws IOException {
		// transform String ([0..255].[0..255].[0..255].[0..255]) into long
		InetAddress address = Inet4Address.getByName(ip);
		ByteBuffer bb = ByteBuffer.wrap(address.getAddress());
		long ipLong;
		if (address.getAddress().length > 4) {
			ipLong = bb.getLong();// reads 8 bytes and creates a long
		} else {
			ipLong = bb.getInt(); // reads 4 bytes and creates an int
		}
		return ipLong;
	}

	public static String ipv4LongToString(long ip) throws IOException {
		// transform long into String ([0..255].[0..255].[0..255].[0..255])
		ByteBuffer bb = ByteBuffer.allocate(8).putLong(ip);
		byte[] bytes = new byte[4];
		bb.position(4);
		bb.get(bytes, 0, 4); // read 4 bytes starting at position 4.

		InetAddress address = Inet4Address.getByAddress(bytes);
		return address.getHostAddress();
	}

	public static VRRPGroup copyVRRPConfiguration(VRRPGroup vrrpGroup) {

		VRRPGroup copy = copyVRRPGroup(vrrpGroup);

		for (ProtocolEndpoint protocolEndpoint : vrrpGroup.getProtocolEndpoint()) {

			// copy VRRPProtocolEndpoint
			VRRPProtocolEndpoint vrrpProtocolEndpoint = copyVRRPProtocolEndpoint((VRRPProtocolEndpoint) protocolEndpoint);
			copy.addProtocolEndpoint(vrrpProtocolEndpoint);

			// IPProtocolEndpoint copy
			IPProtocolEndpoint ipProtocolEndpoint = (IPProtocolEndpoint) protocolEndpoint.getBindedProtocolEndpoints().get(0);
			IPProtocolEndpoint ipCopy = copyIPProtocolEndpoint(ipProtocolEndpoint);
			vrrpProtocolEndpoint.bindServiceAccessPoint(ipCopy);

			// NetworkPort copy
			NetworkPort networkPort = (NetworkPort) ipProtocolEndpoint.getLogicalPorts().get(0);
			NetworkPort netCopy = copyNetworkPort(networkPort);

			netCopy.addProtocolEndpoint(ipCopy);

		}

		return copy;
	}

	public static VRRPGroup copyVRRPGroup(VRRPGroup vrrpGroup) {

		VRRPGroup copy = new VRRPGroup();

		copy.setVrrpName(vrrpGroup.getVrrpName());
		copy.setVirtualIPAddress(vrrpGroup.getVirtualIPAddress());
		copy.setVirtualLinkAddress(vrrpGroup.getVirtualLinkAddress());

		return copy;
	}

	public static NetworkPort copyNetworkPort(NetworkPort networkPort) {
		NetworkPort netCopy = new NetworkPort();
		netCopy.setName(networkPort.getName());
		netCopy.setPortNumber(networkPort.getPortNumber());
		return netCopy;
	}

	public static IPProtocolEndpoint copyIPProtocolEndpoint(IPProtocolEndpoint ipProtocolEndpoint) {

		IPProtocolEndpoint copy = new IPProtocolEndpoint();
		copy.setProtocolIFType(ipProtocolEndpoint.getProtocolIFType());

		if (ipProtocolEndpoint.getProtocolIFType().equals(ProtocolIFType.IPV4)) {
			copy.setIPv4Address(ipProtocolEndpoint.getIPv4Address());
			copy.setSubnetMask(ipProtocolEndpoint.getSubnetMask());

		} else if (ipProtocolEndpoint.getProtocolIFType().equals(ProtocolIFType.IPV6)) {
			copy.setIPv6Address(ipProtocolEndpoint.getIPv6Address());
			copy.setPrefixLength(ipProtocolEndpoint.getPrefixLength());

		} else {
			copy.setIPv4Address(ipProtocolEndpoint.getIPv4Address());
			copy.setSubnetMask(ipProtocolEndpoint.getSubnetMask());
			copy.setIPv6Address(ipProtocolEndpoint.getIPv6Address());
			copy.setPrefixLength(ipProtocolEndpoint.getPrefixLength());
		}
		return copy;

	}

	public static VRRPProtocolEndpoint copyVRRPProtocolEndpoint(VRRPProtocolEndpoint pE) {

		VRRPProtocolEndpoint copy = new VRRPProtocolEndpoint();
		copy.setPriority(pE.getPriority());
		copy.setProtocolIFType(pE.getProtocolIFType());
		return copy;
	}

	public static VRRPGroup getVRRPGroupByName(ComputerSystem model, int groupId) throws CapabilityException {

		List<VRRPGroup> vrrpGroupsList = model.getAllHostedServicesByType(new VRRPGroup());

		for (VRRPGroup candidate : vrrpGroupsList)
			if (candidate.getVrrpName() == groupId)
				return candidate;

		return null;
	}
}
