package net.i2cat.mantychore.core.resources.tests.capability;

import javax.jms.JMSException;

import net.i2cat.mantychore.core.resources.IResource;
import net.i2cat.mantychore.core.resources.capability.AbstractJMSCapability;
import net.i2cat.mantychore.core.resources.capability.CapabilityException;
import net.i2cat.mantychore.core.resources.descriptor.CapabilityDescriptor;
import net.i2cat.mantychore.core.resources.message.ICapabilityMessage;

public class MockCapability extends AbstractJMSCapability{

	public MockCapability(CapabilityDescriptor capabilityDescriptor) {
		super(capabilityDescriptor, "engine123");	
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
	protected void handleMessage(ICapabilityMessage msg) throws JMSException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setResource(IResource resource){
	}	
}