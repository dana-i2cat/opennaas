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

import java.util.Arrays;
import java.util.UUID;

import org.opennaas.extensions.genericnetwork.model.circuit.NetworkConnection;
import org.opennaas.extensions.genericnetwork.model.circuit.QoSPolicy;
import org.opennaas.extensions.genericnetwork.model.circuit.Route;
import org.opennaas.extensions.genericnetwork.model.circuit.request.CircuitRequest;
import org.opennaas.extensions.genericnetwork.model.circuit.request.Destination;
import org.opennaas.extensions.genericnetwork.model.circuit.request.Source;
import org.opennaas.extensions.genericnetwork.model.driver.DevicePortId;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;
import org.opennaas.extensions.openflowswitch.model.OFFlow;

import com.google.common.collect.BiMap;

/**
 * Circuit Request helper methods
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class Circuit2RequestHelper {

	public static final String	DEFAULT_FLOW_PRIORITY	= "32000";

	/**
	 * Generates a new {@link CircuitRequest} given {@link QoSPolicy}, {@link FloodlightOFMatch}
	 * 
	 * @param qosPolicy
	 * @param match
	 * @param route
	 * @return
	 */
	public static CircuitRequest generateCircuitRequest(QoSPolicy qosPolicy, FloodlightOFMatch match, Route route) {
		CircuitRequest circuitRequest = new CircuitRequest();

		String srcPort = null;
		String dstPort = null;

		if (route != null) {
			if (route.getNetworkConnections() != null && !(route.getNetworkConnections().isEmpty())) {
				srcPort = route.getNetworkConnections().get(0).getSource().getId();
				dstPort = route.getNetworkConnections().get(route.getNetworkConnections().size() - 1).getDestination().getId();
			}
		}

		circuitRequest.setSource(generateSource(match, srcPort));
		circuitRequest.setDestination(generateDestination(match, dstPort));

		// regenarate Label using ToS bits plus two 0 bits
		circuitRequest.setLabel(String.valueOf(Integer.parseInt(match.getTosBits()) * 4));

		circuitRequest.setQosPolicy(qosPolicy);

		return circuitRequest;
	}

	/**
	 * Generates a new {@link CircuitRequest} given {@link QoSPolicy}, {@link FloodlightOFMatch} and calculates Port ID values given
	 * NetworkDevicePortIdsMap and current {@link NetworkConnection}
	 * 
	 * @param qosPolicy
	 * @param match
	 * @param networkDevicePortIdsMap
	 * @param networkConnection
	 * @return
	 */
	public static CircuitRequest generateCircuitRequest(QoSPolicy qosPolicy, FloodlightOFMatch match,
			BiMap<String, DevicePortId> networkDevicePortIdsMap, NetworkConnection networkConnection) {
		CircuitRequest circuitRequest = new CircuitRequest();

		String internalSourcePortId = generateInternalPortId(networkConnection.getSource().getId(), networkDevicePortIdsMap);
		String internalDestinationPortId = generateInternalPortId(networkConnection.getDestination().getId(), networkDevicePortIdsMap);

		circuitRequest.setSource(generateSource(match, internalSourcePortId));
		circuitRequest.setDestination(generateDestination(match, internalDestinationPortId));

		// regenarate Label using ToS bits plus two 0 bits
		circuitRequest.setLabel(String.valueOf(Integer.parseInt(match.getTosBits()) * 4));

		circuitRequest.setQosPolicy(qosPolicy);

		return circuitRequest;
	}

	/**
	 * Generates a new {@link OFFlow} given {@link FloodlightOFMatch} and calculates Port ID values given NetworkDevicePortIdsMap and current
	 * {@link NetworkConnection}
	 * 
	 * @param match
	 * @param networkDevicePortIdsMap
	 * @param networkConnection
	 * @return
	 */
	public static OFFlow generateSwitchOFFlow(FloodlightOFMatch match, BiMap<String, DevicePortId> networkDevicePortIdsMap,
			NetworkConnection networkConnection) {
		String internalSourcePortId = generateInternalPortId(networkConnection.getSource().getId(), networkDevicePortIdsMap);
		String internalDestinationPortId = generateInternalPortId(networkConnection.getDestination().getId(), networkDevicePortIdsMap);

		OFFlow flow = new OFFlow();
		flow.setName(generateRandomFlowId());
		flow.setActive(true);
		FloodlightOFMatch newMatch = new FloodlightOFMatch(match);
		flow.setMatch(newMatch);
		flow.setPriority(DEFAULT_FLOW_PRIORITY);
		flow.getMatch().setIngressPort(internalSourcePortId);

		FloodlightOFAction action = new FloodlightOFAction();
		action.setType("output");
		action.setValue(internalDestinationPortId);

		flow.setActions(Arrays.asList(action));

		return flow;
	}

	public static Source generateSource(FloodlightOFMatch match, String linkPort) {
		Source source = new Source();
		source.setAddress(match.getSrcIp());
		source.setTransportPort(match.getSrcPort());
		source.setLinkPort(linkPort);
		return source;
	}

	public static Destination generateDestination(FloodlightOFMatch match, String linkPort) {
		Destination destination = new Destination();
		destination.setAddress(match.getDstIp());
		destination.setTransportPort(match.getDstPort());
		destination.setLinkPort(linkPort);
		return destination;
	}

	/**
	 * Generates internal Port Id given external Port Id and networkDevicePortIdsMap
	 * 
	 * @param networkDevicePortIdsMap
	 * @param externalPortId
	 * @return
	 */
	public static String generateInternalPortId(String externalPortId, BiMap<String, DevicePortId> networkDevicePortIdsMap) {
		return networkDevicePortIdsMap.get(externalPortId).getDevicePortId();
	}

	public static String generateRandomFlowId() {
		return UUID.randomUUID().toString();
	}

}
