package net.i2cat.mantychore.queuemanager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.iaasframework.protocolsessionmanager.ProtocolException;

public class QueueManager implements ICapability, IQueueManagerService {

	private String	resourceId	= "";

	public QueueManager(String resourceId) {
		this.resourceId = resourceId;
	}

	private BlockingQueue<Action>	queue	= new LinkedBlockingQueue<Action>();

	@Override
	public void handleMessage(String message) {
		// TODO Auto-generated method stub
	}

	@Override
	public void empty() {
		queue.clear();

	}

	@Override
	public void execute() throws ProtocolException {

		for (Action action : queue) {
			action.execute();
		}

	}

	@Override
	public List<Action> getActions() {
		List<Action> actions = new ArrayList<Action>();
		for (Action action : queue) {
			actions.add(action);
		}
		return actions;
	}

	@Override
	public void queueAction(Action action) {
		queue.add(action);

	}

}
