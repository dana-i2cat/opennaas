package org.opennaas.extensions.ofertie.ncl.provisioner.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Circuit;
import org.opennaas.extensions.ofnetwork.model.NetOFFlow;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class NCLModel {

	/**
	 * Key: CircuitId, Value: Circuit
	 */
	private Map<String, Circuit>			allocatedCircuits;

	/**
	 * Key: CircuitId, Value: NetOFFlow implementing the circuit
	 */
	private Map<String, List<NetOFFlow>>	allocatedFlows;

	private Set<Port>						congestedPorts;

	private Map<Port, Set<Circuit>>			circuitsPerPort;

	public NCLModel() {
		allocatedCircuits = new HashMap<String, Circuit>();
		allocatedFlows = new HashMap<String, List<NetOFFlow>>();
		congestedPorts = new HashSet<Port>();
		circuitsPerPort = new HashMap<Port, Set<Circuit>>();
	}

	/**
	 * @return the allocatedCircuits
	 */
	public Map<String, Circuit> getAllocatedCircuits() {
		return allocatedCircuits;
	}

	/**
	 * @param allocatedCircuits
	 *            the allocatedCircuits to set
	 */
	public void setAllocatedCircuits(Map<String, Circuit> allocatedCircuits) {
		this.allocatedCircuits = allocatedCircuits;
	}

	/**
	 * @return the allocatedFlows
	 */
	public Map<String, List<NetOFFlow>> getAllocatedFlows() {
		return allocatedFlows;
	}

	/**
	 * @param allocatedFlows
	 *            the allocatedFlows to set
	 */
	public void setAllocatedFlows(Map<String, List<NetOFFlow>> allocatedFlows) {
		this.allocatedFlows = allocatedFlows;
	}

	/**
	 * @return the congestedPorts
	 */
	public Set<Port> getCongestedPorts() {
		return congestedPorts;
	}

	/**
	 * @param congestedPorts
	 *            the congestedPorts to set
	 */
	public void setCongestedPorts(Set<Port> congestedPorts) {
		this.congestedPorts = congestedPorts;
	}

	/**
	 * @return the circuitsPerPort
	 */
	public Map<Port, Set<Circuit>> getCircuitsPerPort() {
		return circuitsPerPort;
	}

	/**
	 * @param circuitsPerPort
	 *            the circuitsPerPort to set
	 */
	public void setCircuitsPerPort(Map<Port, Set<Circuit>> circuitsPerPort) {
		this.circuitsPerPort = circuitsPerPort;
	}

}
