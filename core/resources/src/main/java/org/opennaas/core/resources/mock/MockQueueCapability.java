package org.opennaas.core.resources.mock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;

public class MockQueueCapability extends AbstractCapability {

	public boolean	sentStartUp	= false;
	public boolean	sentMessage	= false;

	public MockQueueCapability(CapabilityDescriptor descriptor) {
		super(descriptor);
		// TODO Auto-generated constructor stub
	}

	Log	log	= LogFactory.getLog(MockQueueCapability.class);

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendRefreshActions() {
		sentStartUp = true;
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		log.info("MOCK CAPABILITY: queued action!!");
		sentMessage = true;
	}

	@Override
	public String getCapabilityName() {
		// TODO Auto-generated method stub
		return null;
	}

}
