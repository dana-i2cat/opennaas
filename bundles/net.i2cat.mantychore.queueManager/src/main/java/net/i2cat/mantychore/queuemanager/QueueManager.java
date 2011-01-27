package net.i2cat.mantychore.queuemanager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.i2cat.mantychore.commons.Action;
import net.i2cat.mantychore.commons.ICapability;
import net.i2cat.mantychore.protocols.sessionmanager.IProtocolSession;
import net.i2cat.mantychore.protocols.sessionmanager.ProtocolException;
import net.i2cat.mantychore.protocols.sessionmanager.ProtocolSessionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueManager implements ICapability, IQueueManagerService {

	Logger			log			= LoggerFactory
										.getLogger(QueueManager.class);

	private String	resourceId	= "";

	public QueueManager(String resourceId) {
		this.resourceId = resourceId;
	}

	private BlockingQueue<ActionInQueue>	queue	= new LinkedBlockingQueue<ActionInQueue>();

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

		ProtocolNetconfWrapper protocolWrapper = new ProtocolNetconfWrapper();
		for (ActionInQueue actionInQueue : queue) {

			/* use pool for get protocol session */
			log.info("getting protocol session...");

			// TODO BUG, IT HAVE TO SPECIFY THE RESOURCE ID
			String sessionId = protocolWrapper.createProtocolSession(resourceId, actionInQueue.getProtocolSessionContext());

			IProtocolSession protocol = protocolWrapper.getProtocolSession(sessionId);
			actionInQueue.getAction().execute(protocol);

			/* restore the connection */
			protocolWrapper.releaseProtocolSession(sessionId);
		}

	}

	@Override
	public List<Action> getActions() {
		List<Action> actions = new ArrayList<Action>();
		for (ActionInQueue action : queue) {
			actions.add(action.getAction());
		}
		return actions;
	}

	@Override
	public void queueAction(Action action, ProtocolSessionContext protocolSessionContext) {
		ActionInQueue actionInQueue = new ActionInQueue(action, protocolSessionContext);
		queue.add(actionInQueue);

	}

	class ActionInQueue {
		private Action					action;
		private ProtocolSessionContext	protocolSessionContext;

		public ActionInQueue(Action action, ProtocolSessionContext protocolSessionContext) {
			this.action = action;
			this.protocolSessionContext = protocolSessionContext;
		}

		public Action getAction() {
			return action;
		}

		public ProtocolSessionContext getProtocolSessionContext() {
			return protocolSessionContext;
		}
	}

}
