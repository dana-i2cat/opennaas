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

import org.apache.commons.lang.StringUtils;
import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;
import org.opennaas.extensions.genericnetwork.model.circuit.QoSPolicy;
import org.opennaas.extensions.genericnetwork.model.circuit.request.CircuitRequest;
import org.opennaas.extensions.genericnetwork.model.circuit.request.Destination;
import org.opennaas.extensions.genericnetwork.model.circuit.request.Source;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * @author Julio Carlos Barrera
 * 
 */
public abstract class CircuitParser {

	public static Circuit circuitRequestToCircuit(CircuitRequest request) {

		Circuit circuit = new Circuit();

		FloodlightOFMatch trafficFilter = circuitRequestToFloodlightOFMatch(request);
		QoSPolicy qos = request.getQosPolicy();

		circuit.setQos(qos);
		circuit.setTrafficFilter(trafficFilter);

		return circuit;
	}

	/**
	 * Builds a {@link FloodlightOFMatch} object from the given {@link CircuitRequest}
	 * 
	 * @param request
	 * @return
	 */
	public static FloodlightOFMatch circuitRequestToFloodlightOFMatch(CircuitRequest request) {
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
			match.setDstPort(destination.getTransportPort());
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
