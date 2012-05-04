package org.opennaas.core.resources.mock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;

public class MockCapability extends AbstractCapability {

	public boolean	sentStartUp	= false;
	public boolean	sentMessage	= false;

	public MockCapability(CapabilityDescriptor descriptor) {
		super(descriptor);
		// TODO Auto-generated constructor stub
	}

	Log	log	= LogFactory.getLog(MockCapability.class);

	@Override
	public Object sendMessage(String idOperation, Object paramsModel) throws CapabilityException {
		log.info("MOCK CAPABILITY: send message!!");
		sentMessage = true;
		return Response.okResponse(idOperation);
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		return super.actionSet;
	}

	public void setActionSet(IActionSet actionSet) {
		super.actionSet = actionSet;

	}

	@Override
	public Response sendRefreshActions() {
		sentStartUp = true;
		return Response.okResponse("");
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

}
