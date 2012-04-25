package org.opennaas.core.resources.tests.capability;

import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;

public class MockCapability extends AbstractCapability {

	public MockCapability(CapabilityDescriptor capabilityDescriptor) {
		super(capabilityDescriptor);
	}

	public String	internalCall	= null;

	public String getInternalCall() {
		return internalCall;
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setActionSet(IActionSet actionSet) {
		super.actionSet = actionSet;

	}

	@Override
	public void sendRefreshActions() {
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		// TODO Auto-generated method stub
	}

	@Override
	public String getCapabilityName() {
		return "mockCapability";
	}
}