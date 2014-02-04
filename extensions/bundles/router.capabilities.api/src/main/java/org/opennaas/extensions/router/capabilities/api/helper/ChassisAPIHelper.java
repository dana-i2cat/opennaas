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
import java.util.List;

import org.opennaas.extensions.router.capabilities.api.model.chassis.InterfaceInfo;
import org.opennaas.extensions.router.capabilities.api.model.chassis.InterfaceInfoList;
import org.opennaas.extensions.router.model.LogicalTunnelPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
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
}
