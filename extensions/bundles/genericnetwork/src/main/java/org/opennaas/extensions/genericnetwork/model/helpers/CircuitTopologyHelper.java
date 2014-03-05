package org.opennaas.extensions.genericnetwork.model.helpers;

/*
 * #%L
 * OpenNaaS :: Generic Network
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;
import org.opennaas.extensions.genericnetwork.model.circuit.NetworkConnection;
import org.opennaas.extensions.genericnetwork.model.circuit.Route;
import org.opennaas.extensions.genericnetwork.model.topology.NetworkElement;
import org.opennaas.extensions.genericnetwork.model.topology.Port;
import org.opennaas.extensions.genericnetwork.model.topology.Topology;

/**
 * {@link Circuit} - {@link Topology} helper functions
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class CircuitTopologyHelper {

	/**
	 * Returns a {@link Map} containing {@link NetworkConnection}'s inside {@link NetworkElement}'s in a {@link Route} given a {@link Topology}
	 * 
	 * @param route
	 * @param topology
	 * @return
	 */
	public static Map<NetworkElement, NetworkConnection> networkElementNetworkConnectionMap(Route route, Topology topology) {
		Map<NetworkElement, NetworkConnection> networkElementNetworkConnectionMap = new HashMap<NetworkElement, NetworkConnection>();

		// iterate over NetworkConnections in route
		for (NetworkConnection networkConnection : route.getNetworkConnections()) {

			Port srcPort = networkConnection.getSource();
			Port dstPort = networkConnection.getDestination();

			// find NetworkElement with both srcPort and dstPort
			NetworkElement foundNetworkElement = getNetworkElementWithPorts(topology.getNetworkElements(), srcPort, dstPort);
			if (foundNetworkElement != null) {
				// add found NetworkElement
				networkElementNetworkConnectionMap.put(foundNetworkElement, networkConnection);
			}
		}

		return networkElementNetworkConnectionMap;
	}

	/**
	 * Looks for a {@link NetworkElement} with both srcPort and dstPort
	 * 
	 * @param networkElements
	 *            {@link Set} of {@link NetworkElement}'s
	 * @param srcPort
	 * @param dstPort
	 * @return {@link NetworkElement} found, null otherwise
	 */
	public static NetworkElement getNetworkElementWithPorts(Set<NetworkElement> networkElements, Port srcPort, Port dstPort) {
		// iterate over Networkelement's Set
		for (NetworkElement ne : networkElements) {
			if (ne.getPorts().contains(srcPort) && ne.getPorts().contains(dstPort)) {
				// return NetworkElement containing both srcProt and dstPort
				return ne;
			}
		}

		// return null otherwise
		return null;
	}
}
