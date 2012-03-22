package org.opennaas.core.resources.tests.capability;

import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.command.Response;
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

	// @Override
	// public void setResource(IResource resource) {
	// }

	public Response sendMessage(String idOperation, Object paramsModel) {
		// TODO Auto-generated method stub
		return null;
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
	public Response sendRefreshActions() {
		return Response.okResponse("");
	}
}