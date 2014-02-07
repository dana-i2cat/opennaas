package org.opennaas.extensions.router.capabilities.api.helper;

/*
 * #%L
 * OpenNaaS :: Router :: Capabilities :: API
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opennaas.extensions.router.capabilities.api.model.chassis.InterfaceInfo;
import org.opennaas.extensions.router.capabilities.api.model.chassis.InterfaceInfoList;
import org.opennaas.extensions.router.capabilities.api.model.chassis.InterfacesNamesList;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.LogicalTunnelPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.NetworkPort.LinkTechnology;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
import org.opennaas.extensions.router.model.System;
import org.opennaas.extensions.router.model.VLANEndpoint;
import org.opennaas.extensions.router.model.utils.ModelHelper;

/**
 * Chassis API Helper
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class ChassisAPIHelper {
	/**
	 * Returns Interface Information from a given GRE ProtocolEndpoint
	 * 
	 * @param greProtocolEndpoint
	 * @return
	 */
	public static InterfaceInfo getInterfaceInfo(ProtocolEndpoint greProtocolEndpoint) {
		InterfaceInfo interfaceInfo = new InterfaceInfo();

		interfaceInfo.setName(ModelHelper.getInterfaceName(greProtocolEndpoint));
		interfaceInfo.setDescription(greProtocolEndpoint.getDescription());

		return interfaceInfo;
	}

	/**
	 * Returns Interface Information from a given NetworkPort
	 * 
	 * @param networkPort
	 * @return
	 */
	public static InterfaceInfo getInterfaceInfo(NetworkPort networkPort) {
		InterfaceInfo interfaceInfo = new InterfaceInfo();

		interfaceInfo.setName(ModelHelper.getInterfaceName(networkPort));
		if (networkPort instanceof LogicalTunnelPort) {
			interfaceInfo.setPeerUnit(String.valueOf(((LogicalTunnelPort) networkPort).getPeer_unit()));
		}

		if (networkPort.getProtocolEndpoint() != null) {
			for (ProtocolEndpoint pE : networkPort.getProtocolEndpoint()) {
				if (pE instanceof VLANEndpoint) {
					interfaceInfo.setVlan(Integer.toString(((VLANEndpoint) pE).getVlanID()));
				}
			}
			interfaceInfo.setState(networkPort.getOperationalStatus().toString());
		}

		interfaceInfo.setDescription(networkPort.getDescription());

		return interfaceInfo;
	}

	/**
	 * Returns InterfaceInfo list of all interfaces from given System
	 * 
	 * @param interfaces
	 * @param greProtocolEndpoints
	 * @return
	 */
	public static List<InterfaceInfo> getInterfacesInfo(System system) {
		List<NetworkPort> interfaces = ModelHelper.getInterfaces(system);
		List<ProtocolEndpoint> greProtocolEndpoints = ModelHelper.getGREProtocolEndpoints(system);

		List<InterfaceInfo> interfaceInfos = new ArrayList<InterfaceInfo>();

		for (NetworkPort networkPort : interfaces) {
			interfaceInfos.add(getInterfaceInfo(networkPort));
		}

		for (ProtocolEndpoint greProtocolEndpoint : greProtocolEndpoints) {
			interfaceInfos.add(getInterfaceInfo(greProtocolEndpoint));
		}

		return interfaceInfos;
	}

	/**
	 * Returns InterfaceInfoList from given InterfaceInfo list
	 * 
	 * @param interfaceInfos
	 * @return
	 */
	public static InterfaceInfoList getInterfacesInfo(List<InterfaceInfo> interfaceInfos) {
		InterfaceInfoList interfaceInfoList = new InterfaceInfoList();
		interfaceInfoList.setInterfaceInfos(interfaceInfos);
		return interfaceInfoList;
	}

	/**
	 * FIXME this logic must be moved to the implementation driver because it is Junos specific.<br>
	 * Generates a valid Network port instance (including its subclasses) based on the interface name
	 * 
	 * @param interfaceInfo
	 * @return
	 * @throws Exception
	 */
	public static NetworkPort interfaceInfo2NetworkPort(InterfaceInfo interfaceInfo) throws IllegalArgumentException {
		// get interface parameters
		String interfaceName = interfaceInfo.getName();
		String subInterfaceName = ChassisAPIHelper.getInterfaceName(interfaceName);
		int portNumber = ChassisAPIHelper.getInterfacePortNumber(interfaceName);
		int vlanId;
		try {
			vlanId = interfaceInfo.getVlan() != null ? Integer.parseInt(interfaceInfo.getVlan()) : -1;
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid vlanId value: " + interfaceInfo.getVlan(), e);
		}
		int peerUnit;
		try {
			peerUnit = interfaceInfo.getPeerUnit() != null ? Integer.parseInt(interfaceInfo.getPeerUnit()) : -1;
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid peer unit value: " + interfaceInfo.getPeerUnit(), e);
		}
		String description = interfaceInfo.getDescription();

		// check ethernet ports
		if (isEthernetPort(interfaceName)) {
			if (vlanId == -1 && portNumber != 0)
				throw new IllegalArgumentException("Only unit 0 is valid for non tagged-ethernet encapsulation.");
		}

		NetworkPort networkPort = null;

		// check if it is a logical tunnel
		if (isLogicalTunnelPort(interfaceName)) {
			LogicalTunnelPort logicalTunnel = new LogicalTunnelPort();
			logicalTunnel.setLinkTechnology(LinkTechnology.OTHER);
			if (peerUnit == -1)
				throw new IllegalArgumentException("peerUnit must be specified in lt interfaces");
			logicalTunnel.setPeer_unit(peerUnit);
			networkPort = logicalTunnel;
		} else {
			networkPort = new EthernetPort();
		}

		networkPort.setName(subInterfaceName);

		networkPort.setPortNumber(portNumber);

		if (vlanId != -1) {
			VLANEndpoint vlanEndpoint = new VLANEndpoint();
			vlanEndpoint.setVlanID(vlanId); // TODO COMPLETE OTHER CASES... INITIALIZE THE VLAN ID TO 1
			networkPort.addProtocolEndpoint(vlanEndpoint);
		}

		networkPort.setDescription(description);

		return networkPort;
	}

	/**
	 * Split interface and port number String in form ifacename.unit into an String arrays with two values
	 * 
	 * @param nameAndPort
	 * @return
	 */
	public static String[] splitInterfaceNameAndPort(String nameAndPort) {
		if (nameAndPort == null || nameAndPort.isEmpty()) {
			throw new IllegalArgumentException("Invalid nameAndPort");
		}
		// split name and port
		String[] split = nameAndPort.split("\\.");
		if (split.length != 2) {
			throw new IllegalArgumentException("Invalid nameAndPort value. It should be in form \"ifacename.unit\"");
		}
		return split;
	}

	/**
	 * Gets the interface name from an String in form ifacename.unit
	 * 
	 * @param nameAndPort
	 * @return
	 */
	public static String getInterfaceName(String nameAndPort) {
		return splitInterfaceNameAndPort(nameAndPort)[0];
	}

	/**
	 * Gets the interface port number from an String in form ifacename.unit
	 * 
	 * @param nameAndPort
	 * @return
	 */
	public static int getInterfacePortNumber(String nameAndPort) {
		return Integer.parseInt(splitInterfaceNameAndPort(nameAndPort)[1]);
	}

	/**
	 * Translates a {@link String} interface name to an empty {@link LogicalPort}
	 * 
	 * @param interfaceName
	 * @return
	 */
	public static LogicalPort interfaceName2LogicalPort(String interfaceName) {
		if (interfaceName == null || interfaceName.isEmpty()) {
			throw new IllegalArgumentException("Invalid interfaceName");
		}

		LogicalPort lp = new LogicalPort();
		lp.setName(interfaceName);
		return lp;
	}

	/**
	 * Translates a {@link String} subinterface name to an empty {@link NetworkPort}
	 * 
	 * @param subInterfaceName
	 * @return
	 */
	public static NetworkPort subInterfaceName2NetworkPort(String subInterfaceName) {
		NetworkPort networkPort;
		if (isLogicalTunnelPort(subInterfaceName)) {
			networkPort = new LogicalTunnelPort();
		} else {
			networkPort = new EthernetPort();
		}
		networkPort.setName(getInterfaceName(subInterfaceName));
		networkPort.setPortNumber(getInterfacePortNumber(subInterfaceName));
		return networkPort;
	}

	/**
	 * Determines if an interface is a LogicalTunnelPort given the interface name.
	 * 
	 * FIXME: JunOS specific logic!!!
	 * 
	 * @param subInterfaceName
	 * @return
	 */
	public static boolean isLogicalTunnelPort(String subInterfaceName) {

		String interfaceName = getInterfaceName(subInterfaceName);
		return interfaceName.startsWith("lt");
	}

	/**
	 * Determines if an interface is an EthernetPort given the interface name.
	 * 
	 * FIXME: JunOS specific logic!!!
	 * 
	 * @param subInterfaceName
	 * @return
	 */
	public static boolean isEthernetPort(String subInterfaceName) {

		String interfaceName = getInterfaceName(subInterfaceName);
		return interfaceName.startsWith("ge") || interfaceName.startsWith("fe");
	}

	/**
	 * Determines if an interface is a loopback interface given the interface name.
	 * 
	 * FIXME: JunOS specific logic!!!
	 * 
	 * @param subInterfaceName
	 * @return
	 */
	public static boolean isLoopback(String interfaceName) {
		return interfaceName.startsWith("lo");
	}

	/**
	 * Determines if interfaceName is a physical one.
	 * 
	 * FIXME: JunOS specific logic!!!
	 * 
	 * @param subInterfaceName
	 * @return
	 */
	public static boolean isPhysicalInterface(String interfaceName) {
		if (interfaceName == null || interfaceName.isEmpty()) {
			throw new IllegalArgumentException("Invalid interfaceName");
		}
		// split name and port
		String[] split = interfaceName.split("\\.");
		if (split.length == 1)
			return true;

		return false;
	}

	/**
	 * Translates a {@link String} logical router name and an optional {@link InterfacesNamesList} interfaces names to a {@link ComputerSystem} with
	 * this information
	 * 
	 * @param logicalRouterName
	 * @param interfacesNames
	 * @return
	 */
	public static ComputerSystem logicalRouter2ComputerSystem(String logicalRouterName, InterfacesNamesList interfacesNames) {
		ComputerSystem logicalRouter = new ComputerSystem();
		logicalRouter.setName(logicalRouterName);
		logicalRouter.setElementName(logicalRouterName);

		if (interfacesNames != null && interfacesNames.getInterfaces() != null) {
			for (String interfaceName : interfacesNames.getInterfaces()) {
				logicalRouter.addLogicalDevice(subInterfaceName2NetworkPort(interfaceName));
			}
		}

		return logicalRouter;
	}

	/**
	 * Translates a {@link InterfacesNamesList} interfaces names list to a {@link List<NetworkPort>}
	 * 
	 * @param interfacesNamesList
	 * @return
	 */
	public static List<NetworkPort> interfaceNameList2NetworkPortList(InterfacesNamesList interfacesNamesList) {
		List<NetworkPort> networkPorstList = new ArrayList<NetworkPort>();

		for (String interfaceName : interfacesNamesList.getInterfaces()) {
			networkPorstList.add(subInterfaceName2NetworkPort(interfaceName));
		}

		return networkPorstList;
	}

	public static ProtocolIFType string2ProtocolIFType(String protocolIfType) {
		try {
			return ProtocolIFType.valueOf(protocolIfType);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid protocolIfType value. It must be one of these: " + Arrays.toString(ProtocolIFType.values()),
					e);
		}
	}
}
