package net.i2cat.mantychore.queuemanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.i2cat.mantychore.commons.Action;
import net.i2cat.mantychore.commons.ActionResponse;
import net.i2cat.mantychore.commons.CommandException;
import net.i2cat.mantychore.commons.ICapability;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSession;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolException;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolSessionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueManager implements IQueueManagerService {

	Logger			log			= LoggerFactory
										.getLogger(QueueManager.class);

	private String	resourceId	= "";

	public QueueManager(String resourceId) {
		this.resourceId = resourceId;
	}

	private BlockingQueue<ActionInQueue>	queue	= new LinkedBlockingQueue<ActionInQueue>();


	public void empty() {
		queue.clear();

	}


	public List<ActionResponse> execute() throws ProtocolException, CommandException {

		List<ActionResponse> responses = new Vector<ActionResponse>();

		ProtocolNetconfWrapper protocolWrapper = new ProtocolNetconfWrapper();
		for (ActionInQueue actionInQueue : queue) {

			/* use pool for get protocol session */
			log.info("getting protocol session...");

			// TODO BUG, IT HAVE TO SPECIFY THE RESOURCE ID
			String sessionId = protocolWrapper.createProtocolSession(resourceId, actionInQueue.getProtocolSessionContext());

			IProtocolSession protocol = protocolWrapper.getProtocolSession(sessionId);

			responses.add(actionInQueue.getAction().execute(protocol,actionInQueue.getParams()));

			/* restore the connection */
			protocolWrapper.releaseProtocolSession(sessionId);
		}
		return responses;

	}

	public List<Action> getActions() {
		List<Action> actions = new ArrayList<Action>();
		for (ActionInQueue action : queue) {
			actions.add(action.getAction());
		}
		return actions;
	}

	public void queueAction(Action action, ProtocolSessionContext protocolSessionContext, Object params) {
		ActionInQueue actionInQueue = new ActionInQueue(action, protocolSessionContext,params);
		queue.add(actionInQueue);

	}

	class ActionInQueue {
		private Action					action;
		private ProtocolSessionContext	protocolSessionContext;
		private Object					params;


		public ActionInQueue(Action action, ProtocolSessionContext protocolSessionContext, Object params) {
			this.action = action;
			this.protocolSessionContext = protocolSessionContext;
			this.params = params;
		}

		public Action getAction() {
			return action;
		}

		public ProtocolSessionContext getProtocolSessionContext() {
			return protocolSessionContext;
		}
		

		public Object getParams() {
			return params;
		}
	}


}
