package net.i2cat.mantychore.queuemanager;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class QueueManager implements Capability, QueueManagerService {

	public QueueManager(String resourceId) {
		// TODO Auto-generated constructor stub
	}

	private BlockingQueue	queue	= new LinkedBlockingQueue();

	@Override
	public void handleMessage(String message) {
		// TODO Auto-generated method stub
	}

	@Override
	public void empty() {
		// TODO Auto-generated method stub

	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Action> getActions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void queueAction(Action action) {
		// TODO Auto-generated method stub

	}

}
