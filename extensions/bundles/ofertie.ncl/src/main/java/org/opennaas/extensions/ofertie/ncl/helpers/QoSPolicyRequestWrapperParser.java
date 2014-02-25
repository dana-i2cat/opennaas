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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.wrapper.QoSPolicyRequestsWrapper;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public abstract class QoSPolicyRequestWrapperParser {

	public static QoSPolicyRequestsWrapper fromCircuitCollection(Collection<Circuit> circuits) {

		QoSPolicyRequestsWrapper wrapper = new QoSPolicyRequestsWrapper();
		Map<String, QosPolicyRequest> qoSPolicyRequests = new HashMap<String, QosPolicyRequest>();

		Iterator<Circuit> circuitIterator = circuits.iterator();

		while (circuitIterator.hasNext()) {
			Circuit circuit = circuitIterator.next();
			QosPolicyRequest qosRequest = QosPolicyRequestParser.fromCircuit(circuit);
			qoSPolicyRequests.put(circuit.getCircuitId(), qosRequest);
		}

		wrapper.setQoSPolicyRequests(qoSPolicyRequests);

		return wrapper;

	}
}
