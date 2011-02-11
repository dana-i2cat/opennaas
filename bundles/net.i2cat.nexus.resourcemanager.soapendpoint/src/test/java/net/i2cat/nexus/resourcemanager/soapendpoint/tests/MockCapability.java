package net.i2cat.nexus.resourcemanager.soapendpoint.tests;

import javax.jms.JMSException;

import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.capability.AbstractJMSCapability;
import net.i2cat.nexus.resources.capability.CapabilityException;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.message.ICapabilityMessage;

public class MockCapability extends AbstractJMSCapability{

	public MockCapability(CapabilityDescriptor arg0, String arg1) {
		super(arg0, arg1);
	}

	@Override
	protected void handleMessage(ICapabilityMessage arg0) throws JMSException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void activateCapability() throws CapabilityException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void deactivateCapability() throws CapabilityException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initializeCapability() throws CapabilityException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void shutdownCapability() throws CapabilityException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setResource(IResource resource){
			// TODO Auto-generated method stub
	}

}
