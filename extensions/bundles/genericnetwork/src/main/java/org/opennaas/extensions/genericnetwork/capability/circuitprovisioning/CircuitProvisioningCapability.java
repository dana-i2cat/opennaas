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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
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
import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Circuit> getCircuits() throws CapabilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void replace(OldAndNewCircuits oldAndNewCircuits) throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void replace(List<Circuit> oldCircuits, List<Circuit> newCircuits) throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void allocate(Circuit circuit) throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deallocate(String circuitId) throws CapabilityException {
		// TODO Auto-generated method stub

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