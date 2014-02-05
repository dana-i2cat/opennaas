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
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.LogicalTunnelPort;
import org.opennaas.extensions.router.model.ManagedSystemElement.OperationalStatus;
import org.opennaas.extensions.router.model.NetworkPort;
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
	 * Translates an {@link InterfaceInfo} to {@link NetworkPort}
	 * 
	 * @param interfaceInfo
	 * @return
	 */
	public static NetworkPort interfaceInfo2NetworkPort(InterfaceInfo interfaceInfo) {
		NetworkPort networkPort = new NetworkPort();

		// set name and port number based on the splitting
		networkPort.setName(getInterfaceName(interfaceInfo.getName()));
		networkPort.setPortNumber(getInterfacePortNumber(interfaceInfo.getName()));

		// VLAN
		String vlan = interfaceInfo.getVlan();
		if (vlan != null && !vlan.isEmpty()) {
			VLANEndpoint vlanEndpoint = new VLANEndpoint();
			vlanEndpoint.setVlanID(Integer.parseInt(vlan));
			networkPort.addProtocolEndpoint(vlanEndpoint);
		}

		// state
		String state = interfaceInfo.getState();
		if (state != null && !state.isEmpty()) {
			try {
				networkPort.setOperationalStatus(OperationalStatus.valueOf(state));
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
						"Invalid InterfaceInfo.state value. It must be one of these: " + Arrays.toString(OperationalStatus.values()), e);
			}
		}

		// description
		networkPort.setDescription(interfaceInfo.getDescription());

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
		NetworkPort networkPort = new NetworkPort();
		networkPort.setName(getInterfaceName(subInterfaceName));
		networkPort.setPortNumber(getInterfacePortNumber(subInterfaceName));
		return networkPort;
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

		if (interfacesNames != null) {
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
