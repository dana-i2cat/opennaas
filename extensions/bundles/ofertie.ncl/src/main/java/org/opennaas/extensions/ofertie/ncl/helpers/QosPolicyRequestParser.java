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

import org.apache.commons.lang.StringUtils;
import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;
import org.opennaas.extensions.genericnetwork.model.circuit.QoSPolicy;
import org.opennaas.extensions.genericnetwork.model.circuit.request.CircuitRequest;
import org.opennaas.extensions.genericnetwork.model.circuit.request.Destination;
import org.opennaas.extensions.genericnetwork.model.circuit.request.Source;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.ProvisionerException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicy;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class QosPolicyRequestParser {

	public static CircuitRequest toCircuitRequest(QosPolicyRequest qosPolicyRequest) throws ProvisionerException {

		CircuitRequest circuitRequest = new CircuitRequest();

		circuitRequest.setAtomic(qosPolicyRequest.getAtomic());
		circuitRequest.setLabel(qosPolicyRequest.getLabel());

		circuitRequest.setSource(toCircuitRequestSource(qosPolicyRequest.getSource()));
		circuitRequest.setDestination(toCircuitsRequestDestination(qosPolicyRequest.getDestination()));
		circuitRequest.setQosPolicy(toCircuitRequestQos(qosPolicyRequest.getQosPolicy()));

		return circuitRequest;
	}

	public static Source toCircuitRequestSource(org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Source source) {

		Source sourceToReturn = new Source();

		sourceToReturn.setAddress(source.getAddress());
		sourceToReturn.setTransportPort(source.getPort());

		return sourceToReturn;
	}

	public static Destination toCircuitsRequestDestination(org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Destination destination) {

		Destination destinationToReturn = new Destination();

		destinationToReturn.setAddress(destination.getAddress());
		destinationToReturn.setTransportPort(destination.getPort());

		return destinationToReturn;
	}

	public static QoSPolicy toCircuitRequestQos(QosPolicy qosPolicy) throws ProvisionerException {

		QoSPolicy qosToReturn = new QoSPolicy();

		if (qosPolicy == null)
			return null;

		if (qosPolicy.getJitter() != null) {
			if (!StringUtils.isEmpty(qosPolicy.getJitter().getMax()))
				qosToReturn.setMaxJitter(Integer.valueOf(qosPolicy.getJitter().getMax()));
			if (!StringUtils.isEmpty(qosPolicy.getJitter().getMin()))
				qosToReturn.setMinJitter(Integer.valueOf(qosPolicy.getJitter().getMin()));
		}

		if (qosPolicy.getLatency() != null) {
			if (!StringUtils.isEmpty(qosPolicy.getLatency().getMax()))
				qosToReturn.setMaxLatency(Integer.valueOf(qosPolicy.getLatency().getMax()));
			if (!StringUtils.isEmpty(qosPolicy.getLatency().getMin()))
				qosToReturn.setMinLatency(Integer.valueOf(qosPolicy.getLatency().getMin()));
		}

		if (qosPolicy.getPacketLoss() != null) {
			if (!StringUtils.isEmpty(qosPolicy.getPacketLoss().getMax()))
				qosToReturn.setMaxPacketLoss(Integer.valueOf(qosPolicy.getPacketLoss().getMax()));
			if (!StringUtils.isEmpty(qosPolicy.getPacketLoss().getMin()))
				qosToReturn.setMinPacketLoss(Integer.valueOf(qosPolicy.getPacketLoss().getMin()));
		}

		if (qosPolicy.getThroughput() != null) {
			if (!StringUtils.isEmpty(qosPolicy.getThroughput().getMax()))
				qosToReturn.setMaxThroughput(Integer.valueOf(qosPolicy.getThroughput().getMax()));
			if (!StringUtils.isEmpty(qosPolicy.getThroughput().getMin()))
				qosToReturn.setMinThroughput(Integer.valueOf(qosPolicy.getThroughput().getMin()));
		}

		return qosToReturn;
	}

	public static QosPolicyRequest fromCircuit(Circuit circuit) {

		QosPolicyRequest qosReq = new QosPolicyRequest();

		qosReq.setSource(parseSource(circuit.getTrafficFilter()));
		qosReq.setDestination(parseDestination(circuit.getTrafficFilter()));
		qosReq.setLabel(parseLabel(circuit.getTrafficFilter()));
		qosReq.setQosPolicy(QoSPolicyParser.fromGenericNetworkQoS(circuit.getQos()));

		return qosReq;
	}

	public static org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Source parseSource(FloodlightOFMatch trafficFilter) {

		org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Source source = new org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Source();

		source.setAddress(trafficFilter.getSrcIp());
		source.setPort(trafficFilter.getSrcPort());

		return source;
	}

	public static org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Destination parseDestination(FloodlightOFMatch trafficFilter) {

		org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Destination destination = new org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Destination();

		destination.setAddress(trafficFilter.getDstIp());
		destination.setPort(trafficFilter.getDstPort());

		return destination;
	}

	public static String parseLabel(FloodlightOFMatch trafficFilter) {

		return String.valueOf(trafficFilter.getTosBits());
	}

	public static QosPolicyRequest fromCircuitRequest(CircuitRequest circuitRequest) {

		QosPolicyRequest qosRequest = new QosPolicyRequest();

		qosRequest.setSource(fromCircuitRequestSource(circuitRequest.getSource()));
		qosRequest.setDestination(fromCircuitRequestDestination(circuitRequest.getDestination()));
		qosRequest.setLabel(fromCircuitRequestLabel(circuitRequest.getLabel()));
		qosRequest.setQosPolicy(QoSPolicyParser.fromGenericNetworkQoS(circuitRequest.getQosPolicy()));

		return qosRequest;
	}

	public static org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Source fromCircuitRequestSource(Source circuitSource) {

		if (circuitSource == null)
			return null;

		org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Source source = new org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Source();

		source.setAddress(circuitSource.getAddress());
		source.setPort(circuitSource.getTransportPort());

		return source;
	}

	public static org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Destination fromCircuitRequestDestination(Destination circuitDestination) {

		if (circuitDestination == null)
			return null;

		org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Destination destination = new org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Destination();

		destination.setAddress(circuitDestination.getAddress());
		destination.setPort(circuitDestination.getTransportPort());

		return destination;
	}

	public static String fromCircuitRequestLabel(String label) {

		return label;

	}

}
