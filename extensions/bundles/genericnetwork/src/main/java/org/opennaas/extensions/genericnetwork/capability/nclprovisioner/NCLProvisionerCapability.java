package org.opennaas.extensions.genericnetwork.capability.nclprovisioner;

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

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.genericnetwork.capability.circuitprovisioning.ICircuitProvisioningCapability;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.api.CircuitCollection;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.api.CircuitId;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.components.CircuitFactoryLogic;
import org.opennaas.extensions.genericnetwork.capability.pathfinding.IPathFindingCapability;
import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;
import org.opennaas.extensions.genericnetwork.model.circuit.Route;
import org.opennaas.extensions.genericnetwork.model.path.PathRequest;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */

public class NCLProvisionerCapability extends AbstractCapability implements INCLProvisionerCapability {

	public static final String	CAPABILITY_TYPE	= "nclprovisioner";

	private Log					log				= LogFactory.getLog(NCLProvisionerCapability.class);
	private String				resourceId		= "";

	public NCLProvisionerCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new NCLProvisioner Capability");

	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public String allocateCircuit(PathRequest pathRequest) throws CapabilityException {

		IPathFindingCapability pathFindingCapab;
		ICircuitProvisioningCapability circuitProvCapability;
		try {
			pathFindingCapab = (IPathFindingCapability) getCapability(IPathFindingCapability.class);
			circuitProvCapability = (ICircuitProvisioningCapability) getCapability(ICircuitProvisioningCapability.class);

		} catch (ResourceException e) {
			throw new CapabilityException(e);
		}

		Route route = pathFindingCapab.findPathForRequest(pathRequest);
		Circuit circuit = CircuitFactoryLogic.generateCircuit(pathRequest, route);
		// TODO add aggregation logic when capability is implemented.

		circuitProvCapability.allocate(circuit);

		return circuit.getCircuitId();
	}

	@Override
	public void deallocateCircuit(String circuitId) throws CapabilityException {
		// TODO add aggregation logic when capability is implemented.
		ICircuitProvisioningCapability circuitProvCapability;
		try {
			circuitProvCapability = (ICircuitProvisioningCapability) getCapability(ICircuitProvisioningCapability.class);

		} catch (ResourceException e) {
			throw new CapabilityException(e);
		}

		circuitProvCapability.deallocate(circuitId);

	}

	@Override
	public Collection<Circuit> getAllocatedCircuits() throws CapabilityException {

		ICircuitProvisioningCapability circuitProvCapability;

		try {
			circuitProvCapability = (ICircuitProvisioningCapability) getCapability(ICircuitProvisioningCapability.class);

		} catch (ResourceException e) {
			throw new CapabilityException(e);
		}

		List<Circuit> circuits = circuitProvCapability.getCircuits();

		return circuits;
	}

	@Override
	public void updateCircuit(String circuitId, PathRequest pathRequest) throws CapabilityException {

		deallocateCircuit(circuitId);
		allocateCircuit(pathRequest);

	}

	@Override
	public CircuitId allocateCircuitAPI(PathRequest pathRequest) throws CapabilityException {

		String id = allocateCircuit(pathRequest);
		CircuitId circuitId = new CircuitId(id);

		return circuitId;
	}

	@Override
	public CircuitCollection getAllocatedCircuitsAPI() throws CapabilityException {

		CircuitCollection circuitCollection = new CircuitCollection();

		circuitCollection.setCircuits(getAllocatedCircuits());

		return circuitCollection;
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		throw new UnsupportedOperationException("Not Implemented. This capability is not using the queue.");
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		throw new UnsupportedOperationException("This capability does not contain actionset.");
	}

	private ICapability getCapability(Class<? extends ICapability> clazz) throws ResourceException {
		return this.resource.getCapabilityByInterface(clazz);
	}

}
