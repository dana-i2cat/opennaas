package org.opennaas.extensions.quantum.network.builder;

import org.opennaas.core.resources.IResource;
import org.opennaas.extensions.quantum.QuantumException;
import org.opennaas.extensions.quantum.model.Network;
import org.opennaas.extensions.quantum.model.NetworkModel;

public interface NetworkBuilder {

	public NetworkModel buildNetwork(Network network) throws QuantumException;

	public void destroyNetwork(IResource quantumResource, Network network) throws QuantumException;

}
