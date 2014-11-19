package org.opennaas.extensions.genericnetwork.capability.circuitprovisioning;

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
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.genericnetwork.Activator;
import org.opennaas.extensions.genericnetwork.capability.circuitprovisioning.api.CircuitsList;
import org.opennaas.extensions.genericnetwork.capability.circuitprovisioning.api.OldAndNewCircuits;
import org.opennaas.extensions.genericnetwork.capability.circuitprovisioning.api.helpers.CircuitProvisioningAPIHelper;
import org.opennaas.extensions.genericnetwork.exceptions.NotExistingCircuitException;
import org.opennaas.extensions.genericnetwork.model.GenericNetworkModel;
import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;
import org.opennaas.extensions.genericnetwork.model.circuit.NetworkConnection;

/**
 * Circuit Provisioning Capability implementation
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class CircuitProvisioningCapability extends AbstractCapability implements ICircuitProvisioningCapability {

	public static final String	CAPABILITY_TYPE	= "circuitprovisioning";

	private Log					log				= LogFactory.getLog(CircuitProvisioningCapability.class);

	private String				resourceId		= "";

	public CircuitProvisioningCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Circuit Provisioning Capability");
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public CircuitsList getCircuitsAPI() throws CapabilityException {
		return CircuitProvisioningAPIHelper.listOfCircuits2CircuitList(getCircuits());
	}

	@Override
	public List<Circuit> getCircuits() throws CapabilityException {
		Map<String, Circuit> allocatedCircuitsMap = ((GenericNetworkModel) resource.getModel()).getAllocatedCircuits();
		return new ArrayList<Circuit>(allocatedCircuitsMap.values());
	}

	@Override
	public void replace(OldAndNewCircuits oldAndNewCircuits) throws CapabilityException {
		replace(oldAndNewCircuits.getOldCircuits(), oldAndNewCircuits.getNewCircuits());
	}

	@Override
	public void replace(List<Circuit> oldCircuits, List<Circuit> newCircuits) throws CapabilityException {
		List<Circuit> toAllocate = new ArrayList<Circuit>(newCircuits.size());
		List<String> toDeallocate = new ArrayList<String>(oldCircuits.size());
		List<Circuit> commonCircuits = new ArrayList<Circuit>(oldCircuits.size());

		// toAllocate contains all flows in newCircuits that are not in oldCircuits
		// commonCircuits contains all flows that are both in oldCircuits and in newCircuits
		for (Circuit newCircuit : newCircuits) {
			if (!oldCircuits.contains(newCircuit)) {
				toAllocate.add(newCircuit);
			} else {
				commonCircuits.add(newCircuit);
			}
		}

		// toDeallocate contains all flows in oldCircuits that are not in newCircuits
		// commonCircuits contains all flows that are both in oldCircuits and in newCircuits
		for (Circuit oldCircuit : oldCircuits) {
			if (!commonCircuits.contains(oldCircuit)) {
				toDeallocate.add(oldCircuit.getCircuitId());
			}
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Will deallocate circuits:");
			for (String c : toDeallocate) {
				log.debug(c);
			}
			log.debug("Will allocate circuits:");
			for (Circuit c : toAllocate) {
				log.debug(c.getCircuitId() + " with route " + c.getRoute().getId());
				StringBuilder sb = new StringBuilder();
				for (NetworkConnection x : c.getRoute().getNetworkConnections()) {
					sb.append(x.getName());
					sb.append(", ");
				}
				log.debug("Route details: " + sb.toString());
			}
		}

		// HAVE TO DEALLOCATE BEFORE ALLOCATE
		// if toDllocate and toDeallocate contain circuits with same id but with different from each others (i.e. because of re-routing)
		// doing this in reverse order would mean deallocating the newly allocated circuit.
		deallocateCircuits(toDeallocate);
		allocateCircuits(toAllocate);
	}

	private void allocateCircuits(List<Circuit> circuits) throws CapabilityException {
		// Allocate in reverse order.
		// Reverse order is desired to ensure "circuits" are completely established when traffic starts to pass within them.
		// This is so to minimize the amount of packet-in messages to treat.
		for (int i = circuits.size() - 1; i >= 0; i--) {
			allocate(circuits.get(i));
		}
	}

	private void deallocateCircuits(List<String> circuitIds) throws CapabilityException {
		// Deallocate in given order.
		// Given order is desired to ensure "circuits" do not accept traffic when deallocation is taking place.
		// This is so to minimize the amount of packet-in messages to treat.
		for (String circuitId : circuitIds) {
			deallocate(circuitId);
		}
	}

	@Override
	public void allocate(Circuit circuit) throws CapabilityException {
		IAction action = createActionAndCheckParams(CircuitProvisioningActionSet.ALLOCATE_CIRCUIT, circuit);

		ActionResponse response = executeAction(action);

		if (!response.getStatus().equals(ActionResponse.STATUS.OK)) {
			throw new ActionException(response.toString());
		}

		Map<String, Circuit> allocatedCircuitsMap = ((GenericNetworkModel) resource.getModel()).getAllocatedCircuits();
		allocatedCircuitsMap.put(circuit.getCircuitId(), circuit);
	}

	@Override
	public void deallocate(String circuitId) throws CapabilityException {
		Map<String, Circuit> allocatedCircuitsMap = ((GenericNetworkModel) resource.getModel()).getAllocatedCircuits();

		if (!allocatedCircuitsMap.containsKey(circuitId)) {
			throw new NotExistingCircuitException("There is no such circuit with ID = " + circuitId);
		}

		Circuit circuitToRemove = allocatedCircuitsMap.get(circuitId);

		IAction action = createActionAndCheckParams(CircuitProvisioningActionSet.DEALLOCATE_CIRCUIT, circuitToRemove);

		ActionResponse response = executeAction(action);

		if (!response.getStatus().equals(ActionResponse.STATUS.OK)) {
			throw new ActionException(response.toString());
		}

		allocatedCircuitsMap.remove(circuitId);
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

		try {
			return Activator.getActionSetService(CircuitProvisioningCapability.CAPABILITY_TYPE, name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
	}

	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(),
				CircuitProvisioningCapability.class.getName());
		super.activate();
	}

	@Override
	public void deactivate() throws CapabilityException {
		unregisterService();
		super.deactivate();
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		throw new UnsupportedOperationException("Not Implemented. This capability is not using the queue.");
	}

	private ActionResponse executeAction(IAction action) throws CapabilityException {
		try {
			IProtocolManager protocolManager = getProtocolManagerService();
			IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManager(this.resourceId);

			ActionResponse response = action.execute(protocolSessionManager);
			return response;

		} catch (ProtocolException pe) {
			log.error("Error getting protocol session - " + pe.getMessage());
			throw new CapabilityException(pe);
		} catch (ActivatorException ae) {
			String errorMsg = "Error getting protocol manager - " + ae.getMessage();
			log.error(errorMsg);
			throw new CapabilityException(errorMsg, ae);
		}
	}

	private IProtocolManager getProtocolManagerService() throws ActivatorException {
		return Activator.getProtocolManagerService();
	}

}
