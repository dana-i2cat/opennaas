package org.opennaas.extensions.network.capability.topology;

/*
 * #%L
 * OpenNaaS :: Network :: Basic capability
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
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.network.capability.topology.api.NetworkTopology;

/**
 * {@link ITopologyDiscoveryCapability} implementation
 * 
 * @author Julio Carlos Barrera
 *
 */
public class TopologyDiscoveryCapability extends AbstractCapability implements ITopologyDiscoveryCapability {

	public static final String	CAPABILITY_TYPE	= "topology";

	private final static Log	log				= LogFactory.getLog(TopologyDiscoveryCapability.class);

	private String				resourceId		= "";

	public TopologyDiscoveryCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Network Basic Capability");
	}

	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), ITopologyDiscoveryCapability.class.getName());
		super.activate();
	}

	@Override
	public void deactivate() throws CapabilityException {
		registration.unregister();
		super.deactivate();
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public NetworkTopology getNetworkTopology() throws CapabilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addResource(String resourceId) throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addResource(IResource resourceToAdd) throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeResource(String resourceId) throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeResource(IResource resourceToRemove) throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		// this capability does not have action set
		return null;
	}

	@Override
	public void queueAction(IAction arg0) throws CapabilityException {
		// this capability does not use queue
	}

}
