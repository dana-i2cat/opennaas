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

import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QoSRequirements;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.NetworkConnection;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.Port;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.Route;

/**
 * 
 * @author Adrian Rosello (i2CAT)
 * 
 */
public abstract class FlowRequestHelper {

	public static FlowRequest generateSampleFlowRequest() {

		FlowRequest req = new FlowRequest();

		req.setRequestId("1001");

		req.setSourceIPAddress("192.168.1.14");
		req.setDestinationIPAddress("192.168.1.13");

		req.setSourcePort(0);
		req.setDestinationPort(1);

		req.setSourceVlanId(21);
		req.setDestinationVlanId(22);

		req.setTos(16);

		QoSRequirements qosRequirements = new QoSRequirements();
		qosRequirements.setMaxBandwidth(100);
		qosRequirements.setMinBandwidth(10);
		qosRequirements.setMaxDelay(12);
		qosRequirements.setMinDelay(5);
		qosRequirements.setMaxJitter(5);
		qosRequirements.setMinJitter(2);
		qosRequirements.setMaxPacketLoss(10);
		qosRequirements.setMinPacketLoss(0);

		req.setQoSRequirements(qosRequirements);

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
