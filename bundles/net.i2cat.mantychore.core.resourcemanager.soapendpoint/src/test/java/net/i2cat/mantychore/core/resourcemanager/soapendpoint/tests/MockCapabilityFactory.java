package net.i2cat.mantychore.core.resourcemanager.soapendpoint.tests;

import net.i2cat.mantychore.core.resources.capability.AbstractCapabilityFactory;
import net.i2cat.mantychore.core.resources.capability.CapabilityException;
import net.i2cat.mantychore.core.resources.capability.ICapability;
import net.i2cat.mantychore.core.resources.descriptor.CapabilityDescriptor;

public class MockCapabilityFactory extends AbstractCapabilityFactory{

	@Override
	public ICapability createCapability(CapabilityDescriptor arg0, String arg1)
			throws CapabilityException {
		return new MockCapability(arg0, arg1);
	}

}
