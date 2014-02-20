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

import org.opennaas.extensions.genericnetwork.model.circuit.QoSPolicy;
import org.opennaas.extensions.genericnetwork.model.circuit.request.CircuitRequest;
import org.opennaas.extensions.genericnetwork.model.circuit.request.Destination;
import org.opennaas.extensions.genericnetwork.model.circuit.request.Source;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;

/**
 * Circuit Request helper methods
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class Circuit2RequestHelper {

	public static CircuitRequest generateCircuitRequest(QoSPolicy qosPolicy, FloodlightOFMatch match) {
		CircuitRequest circuitRequest = new CircuitRequest();

		circuitRequest.setSource(generateSource(match));
		circuitRequest.setDestination(generateDestination(match));

		// regenarate Label using ToS bits plus two 0 bits
		circuitRequest.setLabel(String.valueOf(Integer.parseInt(match.getTosBits()) * 4));

		circuitRequest.setQosPolicy(qosPolicy);

		return circuitRequest;
	}

	public static Source generateSource(FloodlightOFMatch match) {
		Source source = new Source();
		source.setAddress(match.getSrcIp());
		source.setTransportPort(match.getSrcPort());
		return source;
	}

	public static Destination generateDestination(FloodlightOFMatch match) {
		Destination destination = new Destination();
		destination.setAddress(match.getDstIp());
		destination.setTransportPort(match.getDstPort());
		return destination;
	}
}
