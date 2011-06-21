package net.i2cat.mantychore.capability.ip;

import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.capability.AbstractCapabilityFactory;
import net.i2cat.nexus.resources.capability.CapabilityException;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;

public class IPCapabilityFactory extends AbstractCapabilityFactory {

	@Override
	public ICapability createCapability(CapabilityDescriptor capabilityDescriptor, String resourceId) throws CapabilityException {
		return new IPCapability(capabilityDescriptor, resourceId);
	}

	@Override
	public ICapability create(IResource resource) throws CapabilityException {
		ICapability capability = this.create(resource.getResourceDescriptor().getCapabilityDescriptor(IPCapability.IP), resource
				.getResourceDescriptor()
				.getId());
		capability.setResource(resource);
		return capability;
	}

}
