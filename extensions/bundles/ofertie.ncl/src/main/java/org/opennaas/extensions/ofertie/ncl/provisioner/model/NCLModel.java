package org.opennaas.extensions.ofertie.ncl.provisioner.model;

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;
import org.opennaas.extensions.ofnetwork.model.NetOFFlow;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class NCLModel {

	/**
	 * Key: id, Value: QosPolicyRequest
	 */
	private Map<String, QosPolicyRequest>		allocatedCircuits;

	/**
	 * Key: CircuitId, Value: NetOFFlow implementing the circuit
	 */
	private Map<String, List<NetOFFlow>>		allocatedFlows;

	private Set<Port>							congestedPorts;

	private Map<Port, Set<QosPolicyRequest>>	circuitsPerPort;

	public NCLModel() {
		allocatedCircuits = new HashMap<String, QosPolicyRequest>();
		allocatedFlows = new HashMap<String, List<NetOFFlow>>();
		congestedPorts = new HashSet<Port>();
		circuitsPerPort = new HashMap<Port, Set<QosPolicyRequest>>();
	}

	/**
	 * @return the allocatedCircuits
	 */
	public Map<String, QosPolicyRequest> getAllocatedQoSPolicyRequests() {
		return allocatedCircuits;
	}

	/**
	 * @param allocatedCircuits
	 *            the allocatedCircuits to set
	 */
	public void setAllocatedCircuits(Map<String, QosPolicyRequest> allocatedCircuits) {
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
	public Map<Port, Set<QosPolicyRequest>> getCircuitsPerPort() {
		return circuitsPerPort;
	}

	/**
	 * @param circuitsPerPort
	 *            the circuitsPerPort to set
	 */
	public void setCircuitsPerPort(Map<Port, Set<QosPolicyRequest>> circuitsPerPort) {
		this.circuitsPerPort = circuitsPerPort;
	}

}
