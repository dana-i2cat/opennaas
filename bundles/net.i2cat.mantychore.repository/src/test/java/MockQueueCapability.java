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
	public Response sendRefreshActions() {
		sentStartUp = true;
		return Response.okResponse("");
	}

}
