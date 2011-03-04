package net.i2cat.nexus.resources.tests.capability;

import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.capability.AbstractCapability;
import net.i2cat.nexus.resources.capability.CapabilityException;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;

public class MockCapability extends AbstractCapability{

	public MockCapability(CapabilityDescriptor capabilityDescriptor) {
		super(capabilityDescriptor);	
	}
	
	public String internalCall = null;
	
	public String getInternalCall(){
		return internalCall;
	}
	
	@Override
	protected void activateCapability() throws CapabilityException {
		internalCall = "activate";
	}

	@Override
	protected void deactivateCapability() throws CapabilityException {
		internalCall = "deactivate";
	}

	@Override
	protected void initializeCapability() throws CapabilityException {
		internalCall = "initialize";
	}

	@Override
	protected void shutdownCapability() throws CapabilityException {
		internalCall = "shutdown";
	}
	
	@Override
	public void setResource(IResource resource){
	}	
}