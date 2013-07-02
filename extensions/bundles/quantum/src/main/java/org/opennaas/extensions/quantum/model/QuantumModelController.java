package org.opennaas.extensions.quantum.model;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.extensions.quantum.QuantumException;

public class QuantumModelController {

	private Log	log	= LogFactory.getLog(QuantumModelController.class);

	public void addNetwork(QuantumModel quantumModel, Network network) throws QuantumException {

		log.debug("Adding network " + network.getName() + " to Quantum model.");

		List<Network> networks = quantumModel.getNetworks();

		if (networks.contains(network))
			throw new QuantumException("Network  " + network.getName() + " already exists in Quantum Model.");

		quantumModel.getNetworks().add(network);

		log.debug("Network " + network.getName() + " added to Quantum model.");

	}
}
