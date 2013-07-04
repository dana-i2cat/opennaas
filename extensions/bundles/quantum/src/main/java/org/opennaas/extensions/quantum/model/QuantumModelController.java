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

	/**
	 * Adds a network to the quantum resource model.
	 * 
	 * @param quantumModel
	 * @param network
	 * @throws QuantumException
	 */
	public void addNetwork(QuantumModel quantumModel, Network network) throws QuantumException {

		log.debug("Adding network " + network.getName() + " to Quantum model.");

		List<Network> networks = quantumModel.getNetworks();

		if (networks.contains(network))
			throw new QuantumException("Network  " + network.getName() + " already exists in Quantum Model.");

		quantumModel.getNetworks().add(network);

		log.debug("Network " + network.getName() + " added to Quantum model.");

	}

	/**
	 * Removes a network from the quantum resource model, and also its ports and subnetworks.
	 * 
	 * @param quantumModel
	 * @param networkId
	 * @throws QuantumException
	 */
	public void removeNetwork(QuantumModel quantumModel, String networkId) throws QuantumException {

		log.debug("Trying to remove network " + networkId + " from Quantum model.");

		Network network = getNetwork(quantumModel, networkId);

		if (network == null)
			throw new QuantumException("Network " + networkId + " does not exist in Quantum model");

		removeNetworkSubnets(quantumModel, network);
		removeNetworkPorts(quantumModel, network);

		quantumModel.getNetworks().remove(network);

		log.debug("Network " + networkId + " successfully removed from Quantum model.");

	}

	public void updateNetwork(String networkId, QuantumModel quantumModel, Network updatedNetwork) throws QuantumException {
		log.debug("Trying to update network " + networkId + " from Quantum model.");

		Network originalNetwork = getNetwork(quantumModel, networkId);

		if (originalNetwork == null)
			throw new QuantumException("Network " + networkId + " does not exist in Quantum model");

		if (!originalNetwork.equals(updatedNetwork)) {
			quantumModel.getNetworks().remove(originalNetwork);
			quantumModel.getNetworks().add(updatedNetwork);
		}

		log.debug("Network " + networkId + " successfully removed from Quantum model.");
	}

	private void removeNetworkSubnets(QuantumModel quantumModel, Network network) {

		log.debug("Removing network " + network.getId() + " subnets from Quantum Model.");

		for (Subnet net : network.getSubnets())
			quantumModel.getSubnets().remove(net);

		log.debug("Network " + network.getId() + " subnets removed from Quantum Model.");

	}

	private void removeNetworkPorts(QuantumModel quantumModel, Network network) {

		log.debug("Removing network " + network.getId() + " ports from Quantum Model.");

		for (Port port : network.getPorts())
			quantumModel.getPorts().remove(port);

		log.debug("Network " + network.getId() + " ports removed from Quantum Model.");

	}

	private Network getNetwork(QuantumModel quantumModel, String networkId) {

		for (Network network : quantumModel.getNetworks())
			if (network.getId().equals(networkId))
				return network;

		return null;
	}

}
