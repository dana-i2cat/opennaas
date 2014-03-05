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

import java.util.List;
import java.util.Map;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.genericnetwork.Activator;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.INCLProvisionerCapability;
import org.opennaas.extensions.genericnetwork.model.GenericNetworkModel;
import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;
import org.opennaas.extensions.genericnetwork.model.driver.NetworkConnectionImplementationId;
import org.opennaas.extensions.genericnetwork.model.helpers.TopologyHelper;
import org.opennaas.extensions.genericnetwork.model.topology.Domain;
import org.opennaas.extensions.genericnetwork.model.topology.NetworkElement;
import org.opennaas.extensions.genericnetwork.model.topology.Switch;
import org.opennaas.extensions.genericnetwork.model.topology.Topology;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.IOpenflowForwardingCapability;

public class DeallocateCircuitAction extends Action {

	public static final String	DEFAULT_FLOW_PRIORITY	= "32000";

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
		Circuit circuit = (Circuit) params;

		// get Topology from the model
		Topology topology = ((GenericNetworkModel) getModelToUpdate()).getTopology();

		// get circuitImplementationMap from the model
		Map<String, List<NetworkConnectionImplementationId>> circuitImplementationMap = ((GenericNetworkModel) getModelToUpdate())
				.getCircuitImplementation();

		// get circuitImplementations IDs List to get each allocated connection
		List<NetworkConnectionImplementationId> circuitImplementationsIds = circuitImplementationMap.get(circuit.getCircuitId());

		try {
			// remove each NetworkConnectionImplementation
			for (NetworkConnectionImplementationId networkConnectionImplementationId : circuitImplementationsIds) {
				// extract NetworkElement from Topology
				NetworkElement networkElement = TopologyHelper.getNetworkElementFromId(topology, networkConnectionImplementationId.getDeviceId());
				if (networkElement instanceof Domain) {
					// domain Id must be the same than the Generic Network ID that represents
					IResource domainResource = TopologyHelper.getResourceFromNetworkElementId(networkElement.getId(),
							Activator.getResourceManagerService());
					INCLProvisionerCapability nclProvisionerCapability = (INCLProvisionerCapability) domainResource
							.getCapabilityByInterface(INCLProvisionerCapability.class);

					// deallocate circuit
					nclProvisionerCapability.deallocateCircuit(networkConnectionImplementationId.getCircuitId());
				} else if (networkElement instanceof Switch) {
					// switch Id must be the same than the Openflow Switch ID that represents
					IResource switchResource = TopologyHelper.getResourceFromNetworkElementId(networkElement.getId(),
							Activator.getResourceManagerService());
					IOpenflowForwardingCapability ofForwardingCapability = (IOpenflowForwardingCapability) switchResource
							.getCapabilityByInterface(IOpenflowForwardingCapability.class);

					// remove forwarding rule
					ofForwardingCapability.removeOpenflowForwardingRule(networkConnectionImplementationId.getCircuitId());
				} else {
					throw new ActionException("This generic network can only manage Domains and Switches. It contains " + networkElement.getClass()
							.getSimpleName() + " with ID = " + networkElement.getId());
				}
			}
			// remove implementations list of the map
			circuitImplementationMap.remove(circuit.getCircuitId());

		} catch (Exception e) {
			throw new ActionException("Error allocating circuit : ", e);
		}

		return ActionResponse.okResponse(getActionID());
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (!(params instanceof Circuit)) {
			throw new ActionException("Action parameters must be set with a Circuit");
		}

		return true;
	}
}
