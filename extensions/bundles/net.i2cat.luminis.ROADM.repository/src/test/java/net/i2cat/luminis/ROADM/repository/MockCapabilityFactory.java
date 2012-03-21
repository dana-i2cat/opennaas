package net.i2cat.luminis.ROADM.repository;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.capability.AbstractCapabilityFactory;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;

public class MockCapabilityFactory extends AbstractCapabilityFactory {
	public MockCapabilityFactory(String factoryId) {
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
		return new MockCapability();
	}

}
