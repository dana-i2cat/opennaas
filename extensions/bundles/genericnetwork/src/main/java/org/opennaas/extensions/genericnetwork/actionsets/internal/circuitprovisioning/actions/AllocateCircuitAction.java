package org.opennaas.extensions.genericnetwork.actionsets.internal.circuitprovisioning.actions;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.genericnetwork.Activator;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.INCLProvisionerCapability;
import org.opennaas.extensions.genericnetwork.model.GenericNetworkModel;
import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;
import org.opennaas.extensions.genericnetwork.model.circuit.NetworkConnection;
import org.opennaas.extensions.genericnetwork.model.circuit.Route;
import org.opennaas.extensions.genericnetwork.model.circuit.request.CircuitRequest;
import org.opennaas.extensions.genericnetwork.model.driver.NetworkConnectionImplementationId;
import org.opennaas.extensions.genericnetwork.model.helpers.Circuit2RequestHelper;
import org.opennaas.extensions.genericnetwork.model.helpers.CircuitTopologyHelper;
import org.opennaas.extensions.genericnetwork.model.helpers.TopologyHelper;
import org.opennaas.extensions.genericnetwork.model.topology.Domain;
import org.opennaas.extensions.genericnetwork.model.topology.NetworkElement;
import org.opennaas.extensions.genericnetwork.model.topology.Switch;
import org.opennaas.extensions.genericnetwork.model.topology.Topology;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.IOpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;

/**
 * Allocate Circuit Action for Domains or Switches
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class AllocateCircuitAction extends Action {

	public static final String	DEFAULT_FLOW_PRIORITY	= "32000";

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
		Circuit circuit = (Circuit) params;

		Route route = circuit.getRoute();

		// get Topology from the model
		Topology topology = ((GenericNetworkModel) getModelToUpdate()).getTopology();

		// get circuitImplementationMap from the model
		Map<String, List<NetworkConnectionImplementationId>> circuitImplementationMap = ((GenericNetworkModel) getModelToUpdate())
				.getCircuitImplementation();

		// create new circuitImplementations IDs List to add each new allocated connection
		List<NetworkConnectionImplementationId> circuitImplementationsIds = new ArrayList<NetworkConnectionImplementationId>();

		try {
			// create the NetworkElement - NetworkConnection map and iterate over it, allocating
			Map<NetworkElement, NetworkConnection> networkConnectionsMap = CircuitTopologyHelper.networkElementNetworkConnectionMap(route, topology);

			for (NetworkElement networkElement : networkConnectionsMap.keySet()) {
				if (networkElement instanceof Domain) {
					// domain Id must be the same than the Generic Network ID that represents
					IResource domainResource = TopologyHelper.getResourceFromNetworkElementId(networkElement.getId(),
							Activator.getResourceManagerService());
					INCLProvisionerCapability nclProvisionerCapability = (INCLProvisionerCapability) domainResource
							.getCapabilityByInterface(INCLProvisionerCapability.class);

					// generate new CircuitRequest and allocate it
					CircuitRequest circuitRequest = Circuit2RequestHelper.generateCircuitRequest(circuit.getQos(), circuit.getTrafficFilter());
					String driverId = nclProvisionerCapability.allocateCircuit(circuitRequest);

					// store connection in the implementation list
					NetworkConnectionImplementationId networkConnectionImplementationId = new NetworkConnectionImplementationId();
					networkConnectionImplementationId.setCircuitId(driverId);
					networkConnectionImplementationId.setDeviceId(networkElement.getId());
					circuitImplementationsIds.add(networkConnectionImplementationId);
				} else if (networkElement instanceof Switch) {
					// switch Id must be the same than the Openflow Switch ID that represents
					IResource switchResource = TopologyHelper.getResourceFromNetworkElementId(networkElement.getId(),
							Activator.getResourceManagerService());
					IOpenflowForwardingCapability ofForwardingCapability = (IOpenflowForwardingCapability) switchResource
							.getCapabilityByInterface(IOpenflowForwardingCapability.class);

					NetworkConnection networkConnection = networkConnectionsMap.get(networkElement);

					// generate new FloodlightFlow and allocate it
					FloodlightOFFlow flow = new FloodlightOFFlow();
					flow.setName(generateRandomFlowId());
					flow.setActive(true);
					flow.setMatch(circuit.getTrafficFilter());
					flow.setPriority(DEFAULT_FLOW_PRIORITY);
					flow.getMatch().setIngressPort(networkConnection.getSource().getId());

					FloodlightOFAction action = new FloodlightOFAction();
					action.setType("output");
					action.setValue(networkConnection.getDestination().getId());
					flow.setActions(Arrays.asList(action));

					ofForwardingCapability.createOpenflowForwardingRule(flow);

					// store connection in the implementation list
					NetworkConnectionImplementationId networkConnectionImplementationId = new NetworkConnectionImplementationId();
					networkConnectionImplementationId.setCircuitId(flow.getName());
					networkConnectionImplementationId.setDeviceId(networkElement.getId());
					circuitImplementationsIds.add(networkConnectionImplementationId);
				} else {
					throw new ActionException("This generic network can only manage Domains and Switches. It contains " + networkElement.getClass()
							.getSimpleName() + " with ID = " + networkElement.getId());
				}
			}
			// add implementations list to the map
			circuitImplementationMap.put(circuit.getCircuitId(), circuitImplementationsIds);

		} catch (Exception e) {
			// TODO undo created connections if something went wrong (not trivial at all)
			throw new ActionException("Error allocating circuit : ", e);
		}

		return ActionResponse.okResponse(getActionID());
	}

	private static String generateRandomFlowId() {
		return UUID.randomUUID().toString();
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (!(params instanceof Circuit)) {
			throw new ActionException("Action parameters must be set with a Circuit");
		}

		return true;
	}
}
