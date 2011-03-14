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

	/*
	 * This execute uses two params: commit and rollback. These actions use THE SAME PROTOCOL SESSION CONTEXT. It will not be able to do a commit or rollback
	 * with different  protocols
	 */
	public List<ActionResponse> execute(Action commit, Action rollback, ProtocolSessionContext protocolSessionContext) throws ProtocolException, CommandException {

		List<ActionResponse> responses = new Vector<ActionResponse>();


		for (ActionInQueue actionInQueue : queue) {

			/* use pool for get protocol session */
			log.info("getting protocol session...");
			responses.add(executeActionWithProtocol(actionInQueue.getAction(),actionInQueue.getProtocolSessionContext(),actionInQueue.getParams()));

			queue.remove(actionInQueue);
		}
		
		//FIXME It is necessary to send params in a commit
		executeActionWithProtocol(commit,protocolSessionContext,null);
		
		return responses;

	}
	
	public ActionResponse executeActionWithProtocol(Action action, ProtocolSessionContext protocolSessionContext, Object params) throws ProtocolException, CommandException  {
		ProtocolNetconfWrapper protocolWrapper = new ProtocolNetconfWrapper();
		/* create a protocol session for resource id*/
		String sessionId = protocolWrapper.createProtocolSession(resourceId, protocolSessionContext);

		IProtocolSession protocol = protocolWrapper.getProtocolSession(sessionId);
		ActionResponse response = action.execute(protocol,params);
		
		/* restore the connection */
		protocolWrapper.releaseProtocolSession(sessionId);


		return response;

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
