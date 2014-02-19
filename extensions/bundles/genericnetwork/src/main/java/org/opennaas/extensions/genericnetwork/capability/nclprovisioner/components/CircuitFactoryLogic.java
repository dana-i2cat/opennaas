package org.opennaas.extensions.genericnetwork.capability.nclprovisioner.components;

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

import java.util.UUID;

import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;
import org.opennaas.extensions.genericnetwork.model.circuit.Route;
import org.opennaas.extensions.genericnetwork.model.helpers.CircuitParser;
import org.opennaas.extensions.genericnetwork.model.path.PathRequest;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public abstract class CircuitFactoryLogic {

	public Circuit generateCircuit(PathRequest request, Route route) {

		Circuit circuit = CircuitParser.pathRequestToCircuit(request);

		circuit.setCircuitId(generateRandomCircuitId());
		circuit.setRoute(route);

		return circuit;

	}

	private String generateRandomCircuitId() {
		return UUID.randomUUID().toString();
	}

}
