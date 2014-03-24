package org.opennaas.extensions.router.model.utils;

/*
 * #%L
 * OpenNaaS :: CIM Model
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.router.model.BridgeDomain;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.GREService;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.ManagedSystemElement;
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

	// TODO: Store physical interfaces in model as LogicalPorts
	// public static List<LogicalPort> getPhysicalInterfaces(System system) {
	// List<LogicalPort> ports = new ArrayList<LogicalPort>();
	// for (LogicalDevice dev : system.getLogicalDevices()) {
	// if (dev.getClass().equals(LogicalPort.class)) {
	// ports.add((LogicalPort) dev);
	// }
	// }
	// return ports;
	// }

	public static List<ProtocolEndpoint> getGREProtocolEndpoints(System system) {
		List<ProtocolEndpoint> greEps = new ArrayList<ProtocolEndpoint>();
		List<GREService> greServices = system.getAllHostedServicesByType(new GREService());
		// FIXME why do we use greServices.get(0) instead of iterating over all greServices?
		if (!greServices.isEmpty()) {
			GREService greService = greServices.get(0);
			greEps.addAll(greService.getProtocolEndpoint());
		}
		return greEps;
	}

	/**
	 * Returns interface name given a NetworkPort
	 * 
	 * @param networkPort
	 * @return
	 */
	public static String getInterfaceName(NetworkPort networkPort) {
		return networkPort.getName() + "." + String.valueOf(networkPort.getPortNumber());
	}

	/**
	 * Returns interface name given a GRE ProtocolEndpoint
	 * 
	 * @param networkPort
	 * @return
	 */
	public static String getInterfaceName(ProtocolEndpoint greProtocolEndpoint) {
		return greProtocolEndpoint.getName();
	}

	// TODO: Store physical interfaces in model as LogicalPorts
	// /**
	// * Returns the NetworkPort associated with given name, null if it not found
	// *
	// * @param name
	// * @param system
	// * @return
	// */
	// public static LogicalPort getLogicalPortFromName(String name, System system) {
	// for (LogicalPort port : getPhysicalInterfaces(system)) {
	// if (port.getName().equals(name)) {
	// return port;
	// }
	// }
	// return null;
	// }

	/**
	 * Returns the NetworkPort associated with given name, null if it not found
	 * 
	 * @param name
	 * @param system
	 * @return
	 */
	public static NetworkPort getNetworkPortFromName(String name, System system) {
		for (NetworkPort networkPort : getInterfaces(system)) {
			if (getInterfaceName(networkPort).equals(name)) {
				return networkPort;
			}
		}
		return null;
	}

	/**
	 * Returns the GRE ProtocolEndpoint associated with given name, null if it not found
	 * 
	 * @param name
	 * @param system
	 * @return
	 */
	public static ProtocolEndpoint getGREProtocolEndpointFromName(String name, System system) {
		for (ProtocolEndpoint protocolEndpoint : getGREProtocolEndpoints(system)) {
			if (getInterfaceName(protocolEndpoint).equals(name)) {
				return protocolEndpoint;
			}
		}
		return null;
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

	/**
	 * Returns a list of Logical Routers given a Router model
	 * 
	 * @param computerSystem
	 * @return
	 */
	public static List<ComputerSystem> getLogicalRouters(ComputerSystem computerSystem) {
		List<ComputerSystem> list = new ArrayList<ComputerSystem>();

		for (ManagedSystemElement systemElement : computerSystem.getManagedSystemElements()) {
			if (systemElement instanceof ComputerSystem) {
				list.add((ComputerSystem) systemElement);
			}
		}

		return list;
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

	/**
	 * 
	 * @param bridgeDomainList
	 * @param name
	 * @return The {@link BridgeDomain} of the list which {@link BridgeDomain#getElementName()} matches the specified name. Null if there's no
	 *         {@link BridgeDomain) in the list which {@link BridgeDomain#getElementName()} matches the specified name.
	 */
	public static BridgeDomain getBridgeDomainByName(List<BridgeDomain> bridgeDomainList, String name) {

		for (BridgeDomain brDomain : bridgeDomainList)
			if (brDomain.getElementName().equals(name))
				return brDomain;

		return null;
	}

}
