package net.i2cat.nexus.resourcemanager.soapendpoint.tests;

import net.i2cat.nexus.resources.capability.AbstractCapabilityFactory;
import net.i2cat.nexus.resources.capability.CapabilityException;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;

public class MockCapabilityFactory extends AbstractCapabilityFactory{

	@Override
	public ICapability createCapability(CapabilityDescriptor arg0, String arg1)
			throws CapabilityException {
		return new MockCapability(arg0, arg1);
	}

}
