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

	public static final String	SRC_IP					= "192.168.1.14";
	public static final String	DST_IP					= "192.168.1.13";

	public static final String	SRC_PORT				= "0";
	public static final String	DST_PORT				= "1";

	private static final String	PORT_1_1_ID				= "port.1.1";
	private static final String	PORT_1_2_ID				= "port.1.2";
	private static final String	PORT_2_1_ID				= "port.2.1";
	private static final String	PORT_2_2_ID				= "port.2.2";

	public static final String	TOS						= "16";

	public static final String	LATENCY_MIN				= "5";
	public static final String	LATENCY_MAX				= "12";
	public static final String	JITTER_MIN				= "2";
	public static final String	JITTER_MAX				= "5";
	public static final String	THROUGHTPUT_MIN			= "10";
	public static final String	THROUGHTPUT_MAX			= "100";
	public static final String	PACKETLOSS_MIN			= "0";
	public static final String	PACKETLOSS_MAX			= "100";

	public static final String	DEFAULT_VALUE_FOR_TEST	= "0";

	public static QosPolicyRequest cloneQosPolicyRequest(QosPolicyRequest originalQosRequest) {

		if (originalQosRequest == null)
			throw new IllegalArgumentException("QosPolicyRequest to be cloned can not be null.");

		QosPolicyRequest clonedQosRequest = new QosPolicyRequest();
		clonedQosRequest.setAtomic(originalQosRequest.getAtomic());
		clonedQosRequest.setDestination(originalQosRequest.getDestination());
		clonedQosRequest.setLabel(originalQosRequest.getLabel());
		clonedQosRequest.setQosPolicy(originalQosRequest.getQosPolicy());
		clonedQosRequest.setSource(originalQosRequest.getSource());

		return clonedQosRequest;
	}

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

		QosPolicy qosPolicy = generateSampleQosPolicy();

		req.setQosPolicy(qosPolicy);

		return req;
	}

	public static QosPolicy generateSampleQosPolicy() {

		QosPolicy qosPolicy = new QosPolicy();

		qosPolicy.setLatency(generateSampleLatency());
		qosPolicy.setJitter(generateSampleJitter());
		qosPolicy.setPacketLoss(generateSamplePacketLoss());
		qosPolicy.setThroughput(generateSampleThroughput());

		return qosPolicy;
	}

	public static PacketLoss generateSamplePacketLoss() {
		PacketLoss packetLoss = new PacketLoss();

		packetLoss.setMin(PACKETLOSS_MIN);
		packetLoss.setMax(PACKETLOSS_MAX);
		packetLoss.setPriority(DEFAULT_VALUE_FOR_TEST);
		packetLoss.setDelay(DEFAULT_VALUE_FOR_TEST);
		packetLoss.setTimeout(DEFAULT_VALUE_FOR_TEST);

		return packetLoss;
	}

	public static Throughput generateSampleThroughput() {
		Throughput throughput = new Throughput();

		throughput.setMin(THROUGHTPUT_MIN);
		throughput.setMax(THROUGHTPUT_MAX);
		throughput.setDelay(DEFAULT_VALUE_FOR_TEST);
		throughput.setPriority(DEFAULT_VALUE_FOR_TEST);
		throughput.setTimeout(DEFAULT_VALUE_FOR_TEST);

		return throughput;
	}

	public static Jitter generateSampleJitter() {
		Jitter jitter = new Jitter();

		jitter.setMin(JITTER_MIN);
		jitter.setMax(JITTER_MAX);
		jitter.setDelay(DEFAULT_VALUE_FOR_TEST);
		jitter.setPriority(DEFAULT_VALUE_FOR_TEST);
		jitter.setTimeout(DEFAULT_VALUE_FOR_TEST);

		return jitter;
	}

	public static Latency generateSampleLatency() {

		Latency latency = new Latency();
		latency.setMin(LATENCY_MIN);
		latency.setMax(LATENCY_MAX);
		latency.setDelay(DEFAULT_VALUE_FOR_TEST);
		latency.setPriority(DEFAULT_VALUE_FOR_TEST);
		latency.setTimeout(DEFAULT_VALUE_FOR_TEST);

		return latency;

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

	/**
	 * Creates a new {@link QosPolicy} by merging the values of the given ones.
	 * 
	 * @param originalQosPolicy
	 * @param updatedQosPolicy
	 * @return
	 */
	public static QosPolicy mergeQosPolicies(QosPolicy originalQosPolicy, QosPolicy updatedQosPolicy) {

		// new request did not update any qos value
		if (updatedQosPolicy == null)
			return originalQosPolicy;

		// old request did not contain any qos value
		if (originalQosPolicy == null)
			return updatedQosPolicy;

		QosPolicy qosPolicy = new QosPolicy();

		Jitter mergedJitter = mergeJitter(originalQosPolicy.getJitter(), updatedQosPolicy.getJitter());
		Throughput mergedThrougput = mergeThroughput(originalQosPolicy.getThroughput(), updatedQosPolicy.getThroughput());
		Latency mergedLatency = mergeLatency(originalQosPolicy.getLatency(), updatedQosPolicy.getLatency());
		PacketLoss mergedPacketloss = mergePacketLoss(originalQosPolicy.getPacketLoss(), updatedQosPolicy.getPacketLoss());

		qosPolicy.setJitter(mergedJitter);
		qosPolicy.setLatency(mergedLatency);
		qosPolicy.setPacketLoss(mergedPacketloss);
		qosPolicy.setThroughput(mergedThrougput);

		return qosPolicy;
	}

	public static Jitter mergeJitter(Jitter originalJitter, Jitter updatedJitter) {

		if (originalJitter == null)
			return updatedJitter;

		if (updatedJitter == null)
			return originalJitter;

		Jitter jitter = new Jitter();

		jitter.setDelay((originalJitter.getDelay() != updatedJitter.getDelay()) ? updatedJitter.getDelay() : originalJitter.getDelay());
		jitter.setMax(((originalJitter.getMax() != updatedJitter.getMax()) ? updatedJitter.getMax() : originalJitter.getMax()));
		jitter.setMin(((originalJitter.getMin() != updatedJitter.getMin()) ? updatedJitter.getMin() : originalJitter.getMin()));
		jitter.setTimeout(((originalJitter.getTimeout() != updatedJitter.getTimeout()) ? updatedJitter.getTimeout() : originalJitter.getTimeout()));
		jitter.setPriority(((originalJitter.getPriority() != updatedJitter.getPriority()) ? updatedJitter.getPriority() : originalJitter
				.getPriority()));

		return jitter;
	}

	public static Throughput mergeThroughput(Throughput originalThroughput, Throughput updatedThroughput) {

		if (originalThroughput == null)
			return updatedThroughput;

		if (updatedThroughput == null)
			return originalThroughput;

		Throughput throughput = new Throughput();
		throughput.setDelay((originalThroughput.getDelay() != updatedThroughput.getDelay()) ? updatedThroughput.getDelay() : originalThroughput
				.getDelay());

		throughput.setMax(((originalThroughput.getMax() != updatedThroughput.getMax()) ? updatedThroughput.getMax() : originalThroughput.getMax()));
		throughput.setMin(((originalThroughput.getMin() != updatedThroughput.getMin()) ? updatedThroughput.getMin() : originalThroughput.getMin()));
		throughput
				.setTimeout(((originalThroughput.getTimeout() != updatedThroughput.getTimeout()) ? updatedThroughput.getTimeout() : originalThroughput
						.getTimeout()));
		throughput
				.setPriority(((originalThroughput.getPriority() != updatedThroughput.getPriority()) ? updatedThroughput.getPriority() : originalThroughput
						.getPriority()));

		return throughput;
	}

	public static Latency mergeLatency(Latency originalLatency, Latency updatedLatency) {
		if (originalLatency == null)
			return updatedLatency;

		if (updatedLatency == null)
			return originalLatency;

		Latency latency = new Latency();

		latency.setDelay((originalLatency.getDelay() != updatedLatency.getDelay()) ? updatedLatency.getDelay() : originalLatency.getDelay());
		latency.setMax(((originalLatency.getMax() != updatedLatency.getMax()) ? updatedLatency.getMax() : originalLatency.getMax()));
		latency.setMin(((originalLatency.getMin() != updatedLatency.getMin()) ? updatedLatency.getMin() : originalLatency.getMin()));
		latency.setTimeout(((originalLatency.getTimeout() != updatedLatency.getTimeout()) ? updatedLatency.getTimeout() : originalLatency
				.getTimeout()));
		latency.setPriority(((originalLatency.getPriority() != updatedLatency.getPriority()) ? updatedLatency.getPriority() : originalLatency
				.getPriority()));

		return latency;
	}

	public static PacketLoss mergePacketLoss(PacketLoss originalPacketLoss, PacketLoss updatedPacketLoss) {

		if (originalPacketLoss == null)
			return updatedPacketLoss;

		if (updatedPacketLoss == null)
			return originalPacketLoss;

		PacketLoss packetLoss = new PacketLoss();

		packetLoss.setDelay((originalPacketLoss.getDelay() != updatedPacketLoss.getDelay()) ? updatedPacketLoss.getDelay() : originalPacketLoss
				.getDelay());
		packetLoss.setMax(((originalPacketLoss.getMax() != updatedPacketLoss.getMax()) ? updatedPacketLoss.getMax() : originalPacketLoss.getMax()));
		packetLoss.setMin(((originalPacketLoss.getMin() != updatedPacketLoss.getMin()) ? updatedPacketLoss.getMin() : originalPacketLoss.getMin()));
		packetLoss
				.setTimeout(((originalPacketLoss.getTimeout() != updatedPacketLoss.getTimeout()) ? updatedPacketLoss.getTimeout() : originalPacketLoss
						.getTimeout()));
		packetLoss
				.setPriority(((originalPacketLoss.getPriority() != updatedPacketLoss.getPriority()) ? updatedPacketLoss.getPriority() : originalPacketLoss
						.getPriority()));

		return packetLoss;

	}

}
