package org.opennaas.extensions.ofertie.ncl.helpers;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QoSRequirements;
import org.opennaas.extensions.sdnnetwork.model.NetworkConnection;
import org.opennaas.extensions.sdnnetwork.model.Port;
import org.opennaas.extensions.sdnnetwork.model.Route;

public abstract class FlowRequestHelper {

	public static FlowRequest generateSampleFlowRequest() {

		FlowRequest req = new FlowRequest();

		req.setSourceIPAddress("192.168.1.14");
		req.setDestinationIPAddress("192.168.1.13");

		req.setSourcePort(0);
		req.setDestinationPort(1);

		req.setSourceVlanId(21);
		req.setDestinationVlanId(22);

		req.setTos(1001);

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
		connection.setId(name);

		return connection;
	}

	public static Port generatePort(String deviceId, int portNumber) {

		Port port = new Port();

		port.setDeviceId(deviceId);
		port.setPortNumber(String.valueOf(portNumber));

		return port;

	}

}
