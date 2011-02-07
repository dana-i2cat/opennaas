package net.i2cat.mantychore.core.resourcemanager.soapendpoint.tests;

import javax.jms.JMSException;

import net.i2cat.mantychore.core.resources.capability.AbstractJMSCapability;
import net.i2cat.mantychore.core.resources.capability.CapabilityException;
import net.i2cat.mantychore.core.resources.descriptor.CapabilityDescriptor;
import net.i2cat.mantychore.core.resources.message.ICapabilityMessage;

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

}
