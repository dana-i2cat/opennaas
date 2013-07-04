package org.opennaas.extensions.quantum.model;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.extensions.quantum.QuantumException;

/**
 * 
 * @author Adrian Rosello (i2CAT)
 * 
 */
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

	public void removeNetwork(QuantumModel quantumModel, String networkId) throws QuantumException {

		log.debug("Trying to remove network " + networkId + " from Quantum model.");

		Network network = getNetwork(quantumModel, networkId);

		if (network == null)
			throw new QuantumException("Network " + networkId + " does not exist in Quantum model");

		removeNetworkSubnets(quantumModel, network);
		removeNetworkPorts(quantumModel, network);

		quantumModel.getNetworks().remove(network);

	}

	private void removeNetworkSubnets(QuantumModel quantumModel, Network network) {

		for (Subnet net : network.getSubnets())
			quantumModel.getPorts().remove(net);

	}

	private void removeNetworkPorts(QuantumModel quantumModel, Network network) {

		for (Port port : network.getPorts())
			quantumModel.getPorts().remove(port);

	}

	private Network getNetwork(QuantumModel quantumModel, String networkId) {

		for (Network network : quantumModel.getNetworks())
			if (network.getId().equals(networkId))
				return network;

		return null;
	}

}
