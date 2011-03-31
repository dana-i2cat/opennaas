package net.i2cat.mantychore.queuemanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.i2cat.mantychore.commons.Action;
import net.i2cat.mantychore.commons.ActionResponse;
import net.i2cat.mantychore.commons.CommandException;
import net.i2cat.mantychore.queuemanager.wrappers.ProtocolNetconfWrapper;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSessionManager;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueManager implements IQueueManagerService {

	Logger			log			= LoggerFactory.getLogger(QueueManager.class);

	private String	resourceId	= "";

	public QueueManager(String resourceId) {
		this.resourceId = resourceId;
	}

	private final BlockingQueue<ActionInQueue>	queue	= new LinkedBlockingQueue<ActionInQueue>();

	public void empty() {
		queue.clear();

	}

	/*
	 * This execute uses two params: commit and rollback. These actions use THE
	 * SAME PROTOCOL SESSION CONTEXT. It will not be able to do a commit or
	 * rollback with different protocols
	 */
	public List<ActionResponse> execute() throws ProtocolException,
			CommandException {

		List<ActionResponse> responses = new Vector<ActionResponse>();

		for (ActionInQueue actionInQueue : queue) {

			/* use pool for get protocol session */
			log.info("getting protocol session...");
			responses.add(executeActionWithProtocol(actionInQueue.getAction(),
					actionInQueue.getParams()));

			queue.remove(actionInQueue);
		}

		// Get commit action
		Action commit = getCommit();
		// FIXME It is necessary to send params in a commit
		executeActionWithProtocol(getCommit(), null);

		return responses;

	}

	private Action getCommit() {
		// TODO
		return null;
	}

	public ActionResponse executeActionWithProtocol(Action action, Object params)
			throws ProtocolException, CommandException {
		ProtocolNetconfWrapper protocolWrapper = new ProtocolNetconfWrapper();

		IProtocolSessionManager protocolSessionManager = protocolWrapper
				.getProtocolSessionManager(resourceId);

		ActionResponse response = action.execute(Activator.getContext(),
				protocolSessionManager, params);

		return response;

	}

	public List<Action> getActions() {
		List<Action> actions = new ArrayList<Action>();
		for (ActionInQueue action : queue) {
			actions.add(action.getAction());
		}
		return actions;
	}

	public void queueAction(Action action, Object params) {
		ActionInQueue actionInQueue = new ActionInQueue(action, params);
		queue.add(actionInQueue);

	}

	class ActionInQueue {
		private final Action	action;
		private final Object	params;

		public ActionInQueue(Action action, Object params) {
			this.action = action;
			this.params = params;
		}

		public Action getAction() {
			return action;
		}

		public Object getParams() {
			return params;
		}
	}

}
