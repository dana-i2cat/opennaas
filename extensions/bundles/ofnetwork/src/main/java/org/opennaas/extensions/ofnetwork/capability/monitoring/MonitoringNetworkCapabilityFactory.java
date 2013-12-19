package org.opennaas.extensions.ofnetwork.capability.monitoring;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.capability.AbstractCapabilityFactory;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;

public class MonitoringNetworkCapabilityFactory extends AbstractCapabilityFactory {

	@Override
	public ICapability create(IResource resource) throws CapabilityException {
		ICapability capability = this.create(resource.getResourceDescriptor()
				.getCapabilityDescriptor(MonitoringNetworkCapability.CAPABILITY_TYPE),
				resource.getResourceDescriptor().getId());
		capability.setResource(resource);
		return capability;
	}

	@Override
	public ICapability createCapability(CapabilityDescriptor capabilityDescriptor, String resourceId) throws CapabilityException {
		return new MonitoringNetworkCapability(capabilityDescriptor, resourceId);
	}
}
