package net.i2cat.mantychore.capability.chassis;

import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.capability.AbstractCapabilityFactory;
import net.i2cat.nexus.resources.capability.CapabilityException;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;

public class ChassisCapabilityFactory extends AbstractCapabilityFactory {

	@Override
	public ICapability create(IResource resource) throws CapabilityException {
		ICapability capability = this.create(resource.getResourceDescriptor().getCapabilityDescriptor(ChassisCapability.CHASSIS), resource
				.getResourceDescriptor()
				.getId());
		capability.setResource(resource);
		return capability;
	}

	@Override
	public ICapability createCapability(CapabilityDescriptor capabilityDescriptor, String resourceId) throws CapabilityException {
		return new ChassisCapability(capabilityDescriptor, resourceId);
	}

}
