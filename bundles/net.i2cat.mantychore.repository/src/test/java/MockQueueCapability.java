import net.i2cat.nexus.resources.action.ActionResponse;
import net.i2cat.nexus.resources.action.IActionSet;
import net.i2cat.nexus.resources.capability.AbstractCapability;
import net.i2cat.nexus.resources.capability.CapabilityException;
import net.i2cat.nexus.resources.command.Response;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.queue.QueueResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MockQueueCapability extends AbstractCapability {

	public boolean	sentStartUp	= false;
	public boolean	sentMessage	= false;

	public MockQueueCapability(CapabilityDescriptor descriptor) {
		super(descriptor);
		// TODO Auto-generated constructor stub
	}

	Log	log	= LogFactory.getLog(MockQueueCapability.class);

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
		QueueResponse response = new QueueResponse();
		response.setConfirmResponse(ActionResponse.newOkAction(""));
		return response;
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
