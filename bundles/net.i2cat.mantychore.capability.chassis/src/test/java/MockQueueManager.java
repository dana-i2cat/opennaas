import java.util.List;

import net.i2cat.mantychore.commons.Action;
import net.i2cat.mantychore.commons.ActionResponse;
import net.i2cat.mantychore.commons.Response;
import net.i2cat.mantychore.queuemanager.QueueManager;
import net.i2cat.nexus.resources.IResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockQueueManager extends QueueManager {
	Logger	log	= LoggerFactory
						.getLogger(MockQueueManager.class);

	public MockQueueManager(List<String> actionIds, IResource resource) {
		super(actionIds, resource);
	}

	public ActionResponse executeActionWithProtocol(Action action) {
		log.info("Executing action in queue....");
		ActionResponse actionResponse = new ActionResponse();
		Response response = Response.okResponse("Mock Queue Manager did the operation correctly!!");
		actionResponse.addResponse(response);
		log.info("Executed action in queue!!");

		return actionResponse;

	}

}
