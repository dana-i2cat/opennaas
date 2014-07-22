package org.opennaas.extensions.ofertie.ncl.helpers;

/*
 * #%L
 * OpenNaaS :: OFERTIE :: NCL components
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

import org.opennaas.extensions.genericnetwork.model.circuit.NetworkConnection;
import org.opennaas.extensions.genericnetwork.model.circuit.Route;
import org.opennaas.extensions.genericnetwork.model.topology.Port;
import org.opennaas.extensions.genericnetwork.model.topology.TopologyElementState;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Destination;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Jitter;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Latency;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.PacketLoss;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicy;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Source;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Throughput;

/**
 * 
 * @author Adrian Rosello (i2CAT)
 * 
 */
public abstract class QoSPolicyRequestHelper {

	public static final String	SRC_IP			= "192.168.1.14";
	public static final String	DST_IP			= "192.168.1.13";

	public static final String	SRC_PORT		= "0";
	public static final String	DST_PORT		= "1";

	private static final String	PORT_1_1_ID		= "port.1.1";
	private static final String	PORT_1_2_ID		= "port.1.2";
	private static final String	PORT_2_1_ID		= "port.2.1";
	private static final String	PORT_2_2_ID		= "port.2.2";

	public static final String	TOS				= "16";

	public static final String	LATENCY_MIN		= "5";
	public static final String	LATENCY_MAX		= "12";
	public static final String	JITTER_MIN		= "2";
	public static final String	JITTER_MAX		= "5";
	public static final String	THROUGHTPUT_MIN	= "10";
	public static final String	THROUGHTPUT_MAX	= "100";
	public static final String	PACKETLOSS_MIN	= "0";
	public static final String	PACKETLOSS_MAX	= "100";

	public static QosPolicyRequest generateSampleQosPolicyRequest() {

		QosPolicyRequest req = new QosPolicyRequest();

		Source source = new Source();
		source.setAddress(SRC_IP);
		source.setPort(SRC_PORT);
		req.setSource(source);

		Destination destination = new Destination();
		destination.setAddress(DST_IP);
		destination.setPort(DST_PORT);
		req.setDestination(destination);

		req.setLabel(TOS);

		QosPolicy qosPolicy = new QosPolicy();

		Latency latency = new Latency();
		latency.setMin(LATENCY_MIN);
		latency.setMax(LATENCY_MAX);
		qosPolicy.setLatency(latency);

		Jitter jitter = new Jitter();
		jitter.setMin(JITTER_MIN);
		jitter.setMax(JITTER_MAX);
		qosPolicy.setJitter(jitter);

		Throughput throughput = new Throughput();
		throughput.setMin(THROUGHTPUT_MIN);
		throughput.setMax(THROUGHTPUT_MAX);
		qosPolicy.setThroughput(throughput);

		PacketLoss packetLoss = new PacketLoss();
		packetLoss.setMin(PACKETLOSS_MIN);
		packetLoss.setMax(PACKETLOSS_MAX);
		qosPolicy.setPacketLoss(packetLoss);

		req.setQosPolicy(qosPolicy);

		return req;
	}

	public static Route generateSampleRoute() {
		Route route = new Route();

		route.setId("route01");

		List<NetworkConnection> networkConnections = new ArrayList<NetworkConnection>();

		NetworkConnection netConnection01 = generateNetworkConnection("internal-connection-01", PORT_1_1_ID, false, PORT_1_2_ID, false);
		NetworkConnection netConnection02 = generateNetworkConnection("external-connection-01", PORT_1_2_ID, false, PORT_2_1_ID, false);
		NetworkConnection netConnection03 = generateNetworkConnection("internal-connection-02", PORT_2_1_ID, false, PORT_2_2_ID, true);

		networkConnections.add(netConnection01);
		networkConnections.add(netConnection02);
		networkConnections.add(netConnection03);

		route.setNetworkConnections(networkConnections);

		return route;
	}

	public static NetworkConnection generateNetworkConnection(String name, String srcPortId, boolean srcPortState, String dstPortId,
			boolean dstPortState) {
		NetworkConnection connection = new NetworkConnection();

		Port srcPort = generatePort(srcPortId, srcPortState);
		Port dstPort = generatePort(dstPortId, dstPortState);

		connection.setSource(srcPort);
		connection.setDestination(dstPort);
		connection.setName(name);

		return connection;
	}

	public static Port generatePort(String portId, boolean isCongested) {

		Port port = new Port();

		TopologyElementState state = new TopologyElementState();
		state.setCongested(isCongested);

		port.setId(portId);
		port.setState(state);

		return port;

	}

}
