package org.opennaas.extensions.genericnetwork.model.helpers;

import org.opennaas.extensions.genericnetwork.model.circuit.QoSPolicy;
import org.opennaas.extensions.genericnetwork.model.circuit.request.CircuitRequest;
import org.opennaas.extensions.genericnetwork.model.circuit.request.Destination;
import org.opennaas.extensions.genericnetwork.model.circuit.request.Source;

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

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * @author Julio Carlos Barrera
 * 
 */
public abstract class CircuitRequestHelper {

	public static Source generateSource(String ipAddress, String linkPort, String transportPort) {

		Source source = new Source();

		source.setAddress(ipAddress);
		source.setLinkPort(linkPort);
		source.setTransportPort(transportPort);

		return source;
	}

	public static Destination generateDestination(String ipAddress, String linkPort, String transportPort) {

		Destination destination = new Destination();

		destination.setAddress(ipAddress);
		destination.setLinkPort(linkPort);
		destination.setTransportPort(transportPort);

		return destination;
	}

	public static QoSPolicy generateQoSPolicy(int minJitter, int maxJitter, int minLatency, int maxLatency, int minThroughput, int maxThroughput,
			int minPacketLoss, int maxPacketLoss) {

		QoSPolicy qosPolicy = new QoSPolicy();

		qosPolicy.setMinJitter(minJitter);
		qosPolicy.setMaxJitter(maxJitter);

		qosPolicy.setMinLatency(minLatency);
		qosPolicy.setMaxLatency(maxLatency);

		qosPolicy.setMinThroughput(minThroughput);
		qosPolicy.setMaxThroughput(maxThroughput);

		qosPolicy.setMinPacketLoss(minPacketLoss);
		qosPolicy.setMaxPacketLoss(maxPacketLoss);

		return qosPolicy;
	}

	public static CircuitRequest generateCircuitRequest(Source source, Destination destination, String label, QoSPolicy qoSPolicy, String atomic) {
		CircuitRequest circuitRequest = new CircuitRequest();

		circuitRequest.setSource(source);
		circuitRequest.setDestination(destination);

		circuitRequest.setLabel(label);

		circuitRequest.setQosPolicy(qoSPolicy);

		circuitRequest.setAtomic(atomic);

		return circuitRequest;
	}

}
