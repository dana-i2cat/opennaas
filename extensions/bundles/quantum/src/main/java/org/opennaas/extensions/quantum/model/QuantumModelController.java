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
	 * @param netModel
	 * @throws QuantumException
	 */
	public void removeNetwork(QuantumModel quantumModel, NetworkModel netModel) throws QuantumException {

		log.debug("Trying to remove network " + netModel.getQuantumNetworkId() + " from Quantum model.");

		Network network = getNetwork(quantumModel, netModel.getQuantumNetworkId());

		if (network == null)
			throw new QuantumException("Network " + netModel + " does not exist in Quantum model");

		removeNetworkSubnets(quantumModel, network);
		removeNetworkPorts(quantumModel, network);

		quantumModel.getNetworks().remove(network);

		log.debug("Network " + netModel + " successfully removed from Quantum model.");

	}

	public void updateNetwork(String networkId, QuantumModel quantumModel, Network updatedNetwork) throws QuantumException {
		log.debug("Trying to update network " + networkId + " from Quantum model.");

		Network originalNetwork = getNetwork(quantumModel, networkId);

		if (originalNetwork == null)
			throw new QuantumException("Network " + networkId + " does not exist in Quantum model");

		quantumModel.getNetworks().remove(originalNetwork);

		quantumModel.getNetworks().add(updatedNetwork);

		log.debug("Network " + networkId + " successfully removed from Quantum model.");
	}

	public void createPort(QuantumModel quantumModel, String networkId, Port newPort) throws QuantumException {
		log.debug("Trying to create port " + newPort.getId() + " from network " + networkId);

		Network network = getNetwork(quantumModel, networkId);

		if (network == null)
			throw new QuantumException("Network " + networkId + " does not exist in Quantum model");

		network.getPorts().add(newPort);

		log.debug("Port " + newPort.getId() + " from network " + networkId + " updated.");

	}

	/**
	 * Update or create a new port instance in network's port list.
	 * 
	 * @FIXME Even though this method is called by the client with a PUT method, we create a new port here because BigSwitch plugin tries to update a
	 *        new port without using the POST method.
	 * 
	 * @param quantumModel
	 * @param networkId
	 * @param updatedPort
	 * @throws QuantumException
	 */
	public void updatePort(QuantumModel quantumModel, String networkId, Port updatedPort) throws QuantumException {

		log.debug("Trying to update port " + updatedPort.getId() + " from network " + networkId);

		Network network = getNetwork(quantumModel, networkId);

		if (network == null)
			throw new QuantumException("Network " + networkId + " does not exist in Quantum model");

		Port originalPort = getNetworkPortFromId(network, updatedPort.getId());
		if (originalPort != null)
			network.getPorts().remove(originalPort);

		network.getPorts().add(updatedPort);

		log.debug("Port " + updatedPort.getId() + " from network " + networkId + " updated.");

	}

	public void removePort(QuantumModel quantumModel, String networkId, String portId) throws QuantumException {

		log.debug("Trying to remove port " + portId + " from network " + networkId);

		Network network = getNetwork(quantumModel, networkId);

		if (network == null)
			throw new QuantumException("Network " + networkId + " does not exist in Quantum model");

		Port portToRemove = getNetworkPortFromId(network, portId);
		if (portToRemove == null)
			throw new QuantumException("Network " + networkId + " does not contain port " + portId);

		network.getPorts().remove(portToRemove);

		log.debug("Port " + portId + " from network " + networkId + " removed.");

	}

	public void updateAttachment(QuantumModel quantumModel, String networkId, String portId, Attachment attachment) throws QuantumException {

		log.debug("Creating attachment for port " + portId + " in network " + networkId);

		Network network = getNetwork(quantumModel, networkId);

		if (network == null)
			throw new QuantumException("Network " + networkId + " does not exist in Quantum model");

		Port port = getNetworkPortFromId(network, portId);

		if (port == null)
			throw new QuantumException("Port " + portId + " does not exist in Quantum model");

		port.setAttachment(attachment);

		log.debug("Created attachment for port " + portId + " in network " + networkId);

	}

	public void addNetworkModelToQuantumModel(QuantumModel model, NetworkModel netModel) throws QuantumException {

		for (NetworkModel quantumNetworkModels : model.getNetworksModel())
			if (quantumNetworkModels.getQuantumNetworkId().equals(netModel.getQuantumNetworkId()))
				throw new QuantumException("There's already a networkModel for Quantum network " + netModel.getQuantumNetworkId());

		model.addNetworkModel(netModel);

	}

	public void removeNetworkModelFromQuantumModel(QuantumModel model, NetworkModel netModel) throws QuantumException {

		if (!model.getNetworksModel().contains(netModel))
			throw new QuantumException("There's no networkModel for Quantum network " + netModel.getQuantumNetworkId());

		model.removeNetworkModel(netModel);
	}

	private Port getNetworkPortFromId(Network network, String portId) {

		for (Port port : network.getPorts())
			if (port.getId().equals(portId))
				return port;

		return null;
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
