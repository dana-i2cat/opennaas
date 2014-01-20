package org.opennaas.extensions.network.capability.queue;

/*
 * #%L
 * OpenNaaS :: Network :: Queue Capability
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

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.capability.AbstractCapabilityFactory;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;

/**
 * Create the capability
 * 
 * @author Jordi Puig
 */
public class QueueCapabilityFactory extends AbstractCapabilityFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.ICapabilityFactory#create(org.opennaas.core.resources.IResource)
	 */
	@Override
	public ICapability create(IResource resource) throws CapabilityException {
		ICapability capability = this.create(resource.getResourceDescriptor().getCapabilityDescriptor(QueueCapability.NETQUEUE_CAPABILITY_NAME),
				resource.getResourceDescriptor().getId());
		capability.setResource(resource);
		return capability;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.core.resources.capability.AbstractCapabilityFactory#createCapability(org.opennaas.core.resources.descriptor.CapabilityDescriptor,
	 * java.lang.String)
	 */
	@Override
	public ICapability createCapability(CapabilityDescriptor capabilityDescriptor, String resourceId) throws CapabilityException {
		return new QueueCapability(capabilityDescriptor, resourceId);
	}

}
