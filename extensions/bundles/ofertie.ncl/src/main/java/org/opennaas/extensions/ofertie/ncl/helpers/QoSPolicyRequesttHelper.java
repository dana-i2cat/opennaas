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

import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Destination;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Jitter;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Latency;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.PacketLoss;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicy;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Source;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Throughput;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.NetworkConnection;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.Port;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.Route;

/**
 * 
 * @author Adrian Rosello (i2CAT)
 * 
 */
public abstract class QoSPolicyRequesttHelper {

	public static QosPolicyRequest generateSampleFlowRequest() {

		QosPolicyRequest req = new QosPolicyRequest();

		Source source = new Source();
		source.setAddress("192.168.1.14");
		source.setPort("0");
		req.setSource(source);

		Destination destination = new Destination();
		destination.setAddress("192.168.1.13");
		destination.setPort("1");
		req.setDestination(destination);

		req.setLabel("16");

		QosPolicy qosPolicy = new QosPolicy();

		Latency latency = new Latency();
		latency.setMin("5");
		latency.setMax("12");
		qosPolicy.setLatency(latency);

		Jitter jitter = new Jitter();
		jitter.setMin("2");
		jitter.setMax("5");
		qosPolicy.setJitter(jitter);

		Throughput throughput = new Throughput();
		throughput.setMin("10");
		throughput.setMax("100");
		qosPolicy.setThroughput(throughput);

		PacketLoss packetLoss = new PacketLoss();
		packetLoss.setMin("0");
		packetLoss.setMax("100");
		qosPolicy.setPacketLoss(packetLoss);

		req.setQosPolicy(qosPolicy);

		return req;
	}

	public static Route generateSampleRoute() {
		Route route = new Route();

		route.setId("route01");

		List<NetworkConnection> networkConnections = new ArrayList<NetworkConnection>();

		NetworkConnection netConnection01 = generateNetworkConnection("internal-connection-01", "device01", 0, "device01", 1);
		NetworkConnection netConnection02 = generateNetworkConnection("external-connection-01", "device01", 1, "device02", 0);
		NetworkConnection netConnection03 = generateNetworkConnection("internal-connection-02", "device02", 0, "device02", 1);

		networkConnections.add(netConnection01);
		networkConnections.add(netConnection02);
		networkConnections.add(netConnection03);

		route.setNetworkConnections(networkConnections);

		return route;
	}

	public static NetworkConnection generateNetworkConnection(String name, String srcDeviceId, int srcPortNumber, String dstDeviceId,
			int dstPortNumber) {
		NetworkConnection connection = new NetworkConnection();

		Port srcPort = generatePort(srcDeviceId, srcPortNumber);
		Port dstPort = generatePort(dstDeviceId, dstPortNumber);

		connection.setSource(srcPort);
		connection.setDestination(dstPort);
		connection.setName(name);

		return connection;
	}

	public static Port generatePort(String deviceId, int portNumber) {

		Port port = new Port();

		port.setDeviceId(deviceId);
		port.setPortNumber(String.valueOf(portNumber));

		return port;

	}

}
