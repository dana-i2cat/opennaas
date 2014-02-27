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

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;
import org.opennaas.extensions.genericnetwork.model.circuit.NetworkConnection;
import org.opennaas.extensions.genericnetwork.model.circuit.QoSPolicy;
import org.opennaas.extensions.genericnetwork.model.circuit.Route;
import org.opennaas.extensions.genericnetwork.model.topology.Port;
import org.opennaas.extensions.genericnetwork.model.topology.TopologyElementState;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public abstract class GenericNetworkModelUtils {

	public static final String	CIRCUIT_ID				= "1234-5678-90AB";

	public static final String	SRC_IP					= "192.168.1.10";
	public static final String	DST_IP					= "192.168.1.11";
	public static final String	ETHER_TYPE				= "2048";
	public static final String	TOS						= "4";
	public static final String	SRC_PORT				= "22";
	public static final String	DST_PORT				= "22";

	public static final String	ROUTE_ID				= "route01";
	public static final String	NET_CONNECTION_ID_1		= "1111-2222-3333-444";
	public static final String	NET_CONNECTION_NAME_1	= "netConnection01";
	public static final String	NET_CONNECTION_ID_2		= "9999-8888-7777-6666";
	public static final String	NET_CONNECTION_NAME_2	= "netConnection02";

	public static final String	PORT_ID_1				= "eth1";
	public static final String	PORT_ID_2				= "eth2";
	public static final String	PORT_ID_3				= "eth3";
	public static final String	PORT_ID_4				= "eth4";

	public static final int		JITTER_MAX				= 10;
	public static final int		JITTER_MIN				= 1;
	public static final int		LATENCY_MAX				= 100;
	public static final int		LATENCY_MIN				= 0;
	public static final int		THROUGHPUT_MAX			= 50;
	public static final int		THROUGHPUT_MIN			= 20;
	public static final int		PACKETLOSS_MAX			= 2;
	public static final int		PACKETLOSS_MIN			= 0;

	public static Circuit generateSampleCircuit() {

		Circuit circuit = new Circuit();

		FloodlightOFMatch trafficFilter = generateSampleFloodlightOFMatch();
		Route route = generateSampleRoute();
		QoSPolicy qos = generateSampleQoSPolicy();

		circuit.setCircuitId(CIRCUIT_ID);
		circuit.setTrafficFilter(trafficFilter);
		circuit.setQos(qos);
		circuit.setRoute(route);

		return circuit;
	}

	public static FloodlightOFMatch generateSampleFloodlightOFMatch() {

		FloodlightOFMatch match = new FloodlightOFMatch();

		match.setSrcIp(SRC_IP);
		match.setDstIp(DST_IP);
		match.setEtherType(ETHER_TYPE);
		match.setTosBits(TOS);
		match.setSrcPort(SRC_PORT);
		match.setDstPort(DST_PORT);

		return match;

	}

	public static Route generateSampleRoute() {

		Route route = new Route();
		List<NetworkConnection> networkConnections = generateSampleNetworkConnections();

		route.setId(ROUTE_ID);
		route.setNetworkConnections(networkConnections);

		return route;
	}

	/**
	 * Generates a list of two {@link NetworkConnection}
	 * 
	 * @return
	 */
	public static List<NetworkConnection> generateSampleNetworkConnections() {

		List<NetworkConnection> connection = new ArrayList<NetworkConnection>();

		NetworkConnection networkConnection1 = new NetworkConnection();

		Port srcPort = generateSamplePort(PORT_ID_1, false);
		Port dstPort = generateSamplePort(PORT_ID_2, false);
		networkConnection1.setId(NET_CONNECTION_ID_1);
		networkConnection1.setName(NET_CONNECTION_NAME_1);
		networkConnection1.setSource(srcPort);
		networkConnection1.setDestination(dstPort);

		NetworkConnection networkConnection2 = new NetworkConnection();

		Port srcPort2 = generateSamplePort(PORT_ID_3, false);
		Port dstPort2 = generateSamplePort(PORT_ID_4, true);
		networkConnection2.setId(NET_CONNECTION_ID_2);
		networkConnection2.setName(NET_CONNECTION_NAME_2);
		networkConnection2.setSource(srcPort2);
		networkConnection2.setDestination(dstPort2);

		connection.add(networkConnection1);
		connection.add(networkConnection2);

		return connection;
	}

	public static Port generateSamplePort(String portId, boolean state) {

		Port port = new Port();
		TopologyElementState elementState = new TopologyElementState();

		elementState.setCongested(state);
		port.setId(portId);
		port.setState(elementState);

		return port;
	}

	public static QoSPolicy generateSampleQoSPolicy() {

		QoSPolicy qos = new QoSPolicy();

		qos.setMaxJitter(JITTER_MAX);
		qos.setMinJitter(JITTER_MIN);
		qos.setMaxLatency(LATENCY_MAX);
		qos.setMinLatency(LATENCY_MIN);
		qos.setMaxThroughput(THROUGHPUT_MAX);
		qos.setMinThroughput(THROUGHPUT_MIN);
		qos.setMaxPacketLoss(PACKETLOSS_MAX);
		qos.setMinPacketLoss(PACKETLOSS_MIN);

		return qos;
	}
}
