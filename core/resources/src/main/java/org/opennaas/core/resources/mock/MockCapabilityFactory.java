package org.opennaas.core.resources.mock;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.capability.AbstractCapabilityFactory;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;

public class MockCapabilityFactory extends AbstractCapabilityFactory {

	public MockCapabilityFactory() {
		this.setType("not set");
	}

	public MockCapabilityFactory(String factoryId) {
		this.setType(factoryId);
	}

	@Override
	public ICapability createCapability(CapabilityDescriptor capabilityDescriptor, String resourceId) throws CapabilityException {
		MockCapability capability = new MockCapability(getMockCapabilityDescriptor());
		capability.setCapabilityDescriptor(capabilityDescriptor);
		capability.initialize();

		return capability;
	}

	private CapabilityDescriptor getMockCapabilityDescriptor() {
		CapabilityDescriptor capabilityDescriptor = new CapabilityDescriptor();
		Information information = new Information();
		information.setName("Mock capability");
		information.setType("Mock");
		information.setVersion("0.0.1");
		capabilityDescriptor.setCapabilityInformation(information);
		return capabilityDescriptor;
	}

	// @Override
	// public ICapability create(IResource resource) throws CapabilityException {
	// // TODO Auto-generated method stub
	// return this.createCapability(resource.getResourceDescriptor().getCapabilityDescriptor("mock"), resource.getResourceDescriptor().getId());
	// }

	public ICapability create(IResource resource) throws CapabilityException {
		MockCapability capability = new MockCapability(getMockCapabilityDescriptor());
		capability.setCapabilityDescriptor(resource.getResourceDescriptor().getCapabilityDescriptor("MOCK"));
		capability.initialize();
		return capability;
	}

}
