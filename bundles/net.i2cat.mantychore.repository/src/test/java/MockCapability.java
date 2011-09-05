import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MockCapability extends AbstractCapability {

	public boolean	sentStartUp	= false;
	public boolean	sentMessage	= false;

	public MockCapability(CapabilityDescriptor descriptor) {
		super(descriptor);
		// TODO Auto-generated constructor stub
	}

	Log	log	= LogFactory.getLog(MockCapability.class);

	// @Override
	// public State getState() {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public void setState(State state) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void initialize() {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void activate() {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void deactivate() {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void shutdown() {
	// // TODO Auto-generated method stub
	//
	// }

	// @Override
	// public CapabilityDescriptor getCapabilityDescriptor() {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public void setCapabilityDescriptor(CapabilityDescriptor descriptor) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public Information getCapabilityInformation() {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public void setResource(IResource resource) {
	// // TODO Auto-generated method stub
	//
	// }

	@Override
	public Object sendMessage(String idOperation, Object paramsModel) throws CapabilityException {
		log.info("MOCK CAPABILITY: send message!!");
		sentMessage = true;
		return Response.okResponse(idOperation);
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void initializeCapability() throws CapabilityException {
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
	protected void shutdownCapability() throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public Response sendStartUpActions() {
		sentStartUp = true;
		return Response.okResponse("");
	}

}
