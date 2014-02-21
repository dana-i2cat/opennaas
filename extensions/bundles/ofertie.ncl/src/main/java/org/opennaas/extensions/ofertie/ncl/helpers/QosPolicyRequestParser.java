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

import org.apache.cxf.common.util.StringUtils;
import org.opennaas.extensions.genericnetwork.model.circuit.QoSPolicy;
import org.opennaas.extensions.genericnetwork.model.circuit.request.CircuitRequest;
import org.opennaas.extensions.genericnetwork.model.circuit.request.Destination;
import org.opennaas.extensions.genericnetwork.model.circuit.request.Source;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.ProvisionerException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicy;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;

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

		if (qosPolicy.getJitter() != null) {
			if (StringUtils.isEmpty(qosPolicy.getJitter().getMax()) || StringUtils.isEmpty(qosPolicy.getJitter().getMin()))
				throw new ProvisionerException("You didin't specify a valid Jitter value.");

			qosToReturn.setMaxJitter(Integer.valueOf(qosPolicy.getJitter().getMax()));
			qosToReturn.setMinJitter(Integer.valueOf(qosPolicy.getJitter().getMin()));
		}

		if (qosPolicy.getLatency() != null) {
			if (StringUtils.isEmpty(qosPolicy.getLatency().getMax()) || StringUtils.isEmpty(qosPolicy.getLatency().getMin()))
				throw new ProvisionerException("You didin't specify a valid Latency value.");

			qosToReturn.setMaxLatency(Integer.valueOf(qosPolicy.getLatency().getMax()));
			qosToReturn.setMinLatency(Integer.valueOf(qosPolicy.getLatency().getMin()));
		}

		if (qosPolicy.getPacketLoss() != null) {
			if (StringUtils.isEmpty(qosPolicy.getPacketLoss().getMax()) || StringUtils.isEmpty(qosPolicy.getPacketLoss().getMin()))
				throw new ProvisionerException("You didin't specify a valid PacketLoss value.");

			qosToReturn.setMaxPacketLoss(Integer.valueOf(qosPolicy.getPacketLoss().getMax()));
			qosToReturn.setMinPacketLoss(Integer.valueOf(qosPolicy.getPacketLoss().getMin()));

		}

		if (qosPolicy.getThroughput() != null) {
			if (StringUtils.isEmpty(qosPolicy.getThroughput().getMax()) || StringUtils.isEmpty(qosPolicy.getThroughput().getMin()))
				throw new ProvisionerException("You didin't specify a valid Throughput value.");

			qosToReturn.setMaxThroughput(Integer.valueOf(qosPolicy.getThroughput().getMax()));
			qosToReturn.setMinThroughput(Integer.valueOf(qosPolicy.getThroughput().getMin()));

		}

		return qosToReturn;
	}
}
