package org.opennaas.extensions.bod.repository.tests;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.ICapabilityLifecycle;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;

public class MockCapability implements ICapabilityLifecycle {
	Log	log	= LogFactory.getLog(MockCapability.class);

	@Override
	public State getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize() throws ResourceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void activate() throws ResourceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deactivate() throws ResourceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void shutdown() throws ResourceException {
		// TODO Auto-generated method stub

	}

	@Override
	public CapabilityDescriptor getCapabilityDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCapabilityDescriptor(CapabilityDescriptor descriptor) {
		// TODO Auto-generated method stub

	}

	@Override
	public Information getCapabilityInformation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setResource(IResource resource) {
		// TODO Auto-generated method stub

	}

	// @Override
	// public Object sendMessage(String idOperation, Object paramsModel) throws CapabilityException {
	// log.info("MOCK CAPABILITY: send message!!");
	// return null;
	// }

	@Override
	public String getCapabilityName() {
		return "mockCapability";
	}

}
