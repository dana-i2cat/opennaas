package org.opennaas.core.resources.mock;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.capability.AbstractCapabilityFactory;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;

public class MockQueueCapabilityFactory extends AbstractCapabilityFactory {
	public MockQueueCapabilityFactory(String factoryId) {
		this.setType(factoryId);
	}

	@Override
	public ICapability create(IResource resource) throws CapabilityException {
		// TODO Auto-generated method stub
		return this.createCapability(resource.getResourceDescriptor().getCapabilityDescriptor("mock"), resource.getResourceDescriptor().getId());
	}

	@Override
	public ICapability createCapability(CapabilityDescriptor capabilityDescriptor, String resourceId) throws CapabilityException {
		// TODO Auto-generated method stub
		return new MockQueueCapability(capabilityDescriptor);
	}

}
