package org.opennaas.extensions.genericnetwork.model.helpers;

import org.apache.commons.lang.StringUtils;
import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;
import org.opennaas.extensions.genericnetwork.model.circuit.QoSPolicy;
import org.opennaas.extensions.genericnetwork.model.path.Destination;
import org.opennaas.extensions.genericnetwork.model.path.Jitter;
import org.opennaas.extensions.genericnetwork.model.path.Latency;
import org.opennaas.extensions.genericnetwork.model.path.PacketLoss;
import org.opennaas.extensions.genericnetwork.model.path.PathRequest;
import org.opennaas.extensions.genericnetwork.model.path.QosPolicy;
import org.opennaas.extensions.genericnetwork.model.path.Source;
import org.opennaas.extensions.genericnetwork.model.path.Throughput;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public abstract class CircuitParser {

	public static Circuit pathRequestToCircuit(PathRequest request) {

		Circuit circuit = new Circuit();

		FloodlightOFMatch trafficFilter = pathRequestToFloodlightOFMatch(request);
		QoSPolicy qos = qosPolicyRequestToQosPolicyModel(request.getQosPolicy());

		circuit.setQos(qos);
		circuit.setTrafficFilter(trafficFilter);

		return circuit;
	}

	public static QoSPolicy qosPolicyRequestToQosPolicyModel(QosPolicy qosPolicy) {

		QoSPolicy qosToReturn = new QoSPolicy();

		Jitter jitter = qosPolicy.getJitter();
		qosToReturn.setMinJitter(Integer.valueOf(jitter.getMin()));
		qosToReturn.setMaxJitter(Integer.valueOf(jitter.getMax()));

		Latency latency = qosPolicy.getLatency();
		qosToReturn.setMinLantency(Integer.valueOf(latency.getMin()));
		qosToReturn.setMaxLatency(Integer.valueOf(latency.getMax()));

		PacketLoss packetLoss = qosPolicy.getPacketLoss();
		qosToReturn.setMinPacketLoss(Integer.valueOf(packetLoss.getMin()));
		qosToReturn.setMaxPaquetLoss(Integer.valueOf(packetLoss.getMax()));

		Throughput throughput = qosPolicy.getThroughput();
		qosToReturn.setMinThroughput(Integer.valueOf(throughput.getMin()));
		qosToReturn.setMaxThroughput(Integer.valueOf(throughput.getMax()));

		return qosToReturn;
	}

	/**
	 * Builds a {@link FloodlightOFMatch} object from the given {@link PathRequest}
	 * 
	 * @param request
	 * @return
	 */
	public static FloodlightOFMatch pathRequestToFloodlightOFMatch(PathRequest request) {
		FloodlightOFMatch match = new FloodlightOFMatch();

		Source source = request.getSource();

		if (!StringUtils.isEmpty(source.getAddress()))
			match.setSrcIp(source.getAddress());
		else
			match.setSrcIp(null);

		if (!StringUtils.isEmpty(source.getTransportPort()))
			match.setSrcPort(source.getTransportPort());
		else
			match.setSrcPort(null);

		Destination destination = request.getDestination();

		if (!StringUtils.isEmpty(destination.getAddress()))
			match.setDstIp(destination.getAddress());
		else
			match.setDstIp(null);
		if (!StringUtils.isEmpty(destination.getTransportPort()))
			match.setDstPort(source.getTransportPort());
		else
			match.setDstPort(null);
		if (!StringUtils.isEmpty(request.getLabel()))
			match.setTosBits(Integer.toString(Integer.parseInt(request.getLabel()) / 4));

		match.setEtherType(calculateRequiredEtherType(match));

		return match;
	}

	private static String calculateRequiredEtherType(FloodlightOFMatch match) {

		String etherType = null;

		if (match == null)
			return null;

		if (match.getSrcIp() != null || match.getDstIp() != null)
			etherType = "2048";

		if (match.getTosBits() != null)
			etherType = "2048";

		return etherType;
	}

}
