package org.opennaas.extensions.genericnetwork.capability.nclprovisioner.components;

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
