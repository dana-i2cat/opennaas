package org.opennaas.extensions.genericnetwork.model;

/*
 * #%L
 * OpenNaaS :: OF Network
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;
import org.opennaas.extensions.genericnetwork.model.driver.NetworkConnectionImplementationId;
import org.opennaas.extensions.genericnetwork.model.portstatistics.TimedStatistics;
import org.opennaas.extensions.genericnetwork.model.portstatistics.TimedSwitchPortStatistics;
import org.opennaas.extensions.genericnetwork.model.topology.Topology;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * @author Julio Carlos Barrera
 * 
 */
@XmlRootElement(namespace = "opennaas.api")
@XmlAccessorType(XmlAccessType.FIELD)
public class GenericNetworkModel implements IModel {

	/**
	 * Auto-generated serial version number
	 */
	private static final long										serialVersionUID	= -3223373735906486372L;

	private Topology												topology;

	// FIXME: don't store this in the model.
	// capability should read this information each time it is asked for it.
	/**
	 * Maps deviceId and Flows in each
	 */
	private Map<String, List<NetOFFlow>>							netFlowsPerResource;

	/**
	 * Maps circuitId to its implementation (the implementation of its NetworkConnections). It is meant to be used for drivers to store
	 * circuitImplementation. Key: circuitId, Value: List of ids in the driver.
	 */
	private Map<String, List<NetworkConnectionImplementationId>>	circuitImplementation;

	/**
	 * Map containing allocated circuits. Maintained by CircuitProvisioningCapability
	 */
	private Map<String, Circuit>									allocatedCircuits;

	/**
	 * Map containing requested circuits. Maintained by NCLProvisionerCapability. <br/>
	 * It stores Circuits as they are requested. (i.e. No changes made due to aggregation).
	 */
	private Map<String, Circuit>									requestedCircuits;

	/**
	 * Maps device ID in GenericNetworkModel and resource ID in OpenNaaS
	 */
	private Map<String, String>										deviceResourceMap;

	/**
	 * Stores timed statistics for switch ports. Used by NCLMonitoringCapability
	 */
	private TimedSwitchPortStatistics								timedSwitchPortStatistics;

	/**
	 * Map containing all circuit statistics reported to the network, ordered by timestamp.
	 */
	private SortedMap<Long, List<CircuitStatistics>>				circuitStatistics;

	public GenericNetworkModel() {
		netFlowsPerResource = new HashMap<String, List<NetOFFlow>>();
		allocatedCircuits = new HashMap<String, Circuit>();
		requestedCircuits = new HashMap<String, Circuit>();
		deviceResourceMap = new HashMap<String, String>();
		circuitImplementation = new HashMap<String, List<NetworkConnectionImplementationId>>();

		circuitStatistics = new TreeMap<Long, List<CircuitStatistics>>();
		timedSwitchPortStatistics = new TimedSwitchPortStatistics();
		timedSwitchPortStatistics.setStatisticsMap(new TreeMap<Long, Map<String, List<TimedStatistics>>>());
	}

	/**
	 * @return the topology
	 */
	public Topology getTopology() {
		return topology;
	}

	/**
	 * @param topology
	 *            the topology to set
	 */
	public void setTopology(Topology topology) {
		this.topology = topology;
	}

	public Map<String, List<NetOFFlow>> getNetFlowsPerResource() {
		return netFlowsPerResource;
	}

	public void setNetFlowsPerResource(Map<String, List<NetOFFlow>> netFlowsPerResource) {
		this.netFlowsPerResource = netFlowsPerResource;
	}

	public Map<String, Circuit> getAllocatedCircuits() {
		return allocatedCircuits;
	}

	public void setAllocatedCircuits(Map<String, Circuit> allocatedCircuits) {
		this.allocatedCircuits = allocatedCircuits;
	}

	public Map<String, Circuit> getRequestedCircuits() {
		return requestedCircuits;
	}

	public void setRequestedCircuits(Map<String, Circuit> requestedCircuits) {
		this.requestedCircuits = requestedCircuits;
	}

	public Map<String, String> getDeviceResourceMap() {
		if (deviceResourceMap == null) {
			deviceResourceMap = new HashMap<String, String>();
		}
		return deviceResourceMap;
	}

	public void setDeviceResourceMap(Map<String, String> deviceResourceMap) {
		this.deviceResourceMap = deviceResourceMap;
	}

	public Map<String, List<NetworkConnectionImplementationId>> getCircuitImplementation() {
		return circuitImplementation;
	}

	public void setCircuitImplementation(Map<String, List<NetworkConnectionImplementationId>> circuitImplementation) {
		this.circuitImplementation = circuitImplementation;
	}

	public SortedMap<Long, List<CircuitStatistics>> getCircuitStatistics() {
		return circuitStatistics;
	}

	public void setCircuitStatistics(SortedMap<Long, List<CircuitStatistics>> circuitStatistics) {
		this.circuitStatistics = circuitStatistics;
	}

	@Override
	public List<String> getChildren() {
		return new ArrayList<String>(0);
	}

	@Override
	public String toXml() throws SerializationException {
		return ObjectSerializer.toXml(this);
	}

	public TimedSwitchPortStatistics getTimedSwitchPortStatistics() {
		return timedSwitchPortStatistics;
	}

	public void setTimedSwitchPortStatistics(TimedSwitchPortStatistics timedSwitchPortStatistics) {
		this.timedSwitchPortStatistics = timedSwitchPortStatistics;
	}

}
