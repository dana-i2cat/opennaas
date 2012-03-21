package org.opennaas.network.capability.queue;

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
