package org.opennaas.extensions.router.capability.ip;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.capability.AbstractCapabilityFactory;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;

public class IPCapabilityFactory extends AbstractCapabilityFactory {

	@Override
	public ICapability createCapability(CapabilityDescriptor capabilityDescriptor, String resourceId) throws CapabilityException {
		return new IPCapability(capabilityDescriptor, resourceId);
	}

	@Override
	public ICapability create(IResource resource) throws CapabilityException {
		ICapability capability = this.create(resource.getResourceDescriptor().getCapabilityDescriptor(IPCapability.IPv4), resource
				.getResourceDescriptor()
				.getId());
		capability.setResource(resource);
		return capability;
	}

}
