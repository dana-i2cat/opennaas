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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.genericnetwork.Activator;
import org.opennaas.extensions.genericnetwork.model.circuit.request.CircuitRequest;

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
	public String allocateFlow(CircuitRequest circuitRequest) throws CapabilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		throw new UnsupportedOperationException("Not Implemented. This capability is not using the queue.");
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		throw new UnsupportedOperationException("This capability does not contain actionset.");

	}

	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(),
				INCLProvisionerCapability.class.getName());
		super.activate();
	}

	@Override
	public void deactivate() throws CapabilityException {
		unregisterService();
		super.deactivate();
	}

}
