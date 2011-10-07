package net.i2cat.mantychore.queuemanager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.queue.ModifyParams;
import org.opennaas.core.resources.queue.QueueConstants;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.core.resources.protocol.ProtocolException;

public class QueueManager extends AbstractCapability implements IQueueManagerService {

	public final static String	QUEUE		= "queue";

	Log							log			= LogFactory
													.getLog(QueueManager.class);
	String						resourceId	= "";

	public QueueManager(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Queue Capability");
		log.debug("Registering Queue Capability");
		registerQueueCapability();
		log.debug("Registered!");
	}

	/**
	 * Constructor to test the component
	 * 
	 * @param queueDescriptor
	 */
	public QueueManager(CapabilityDescriptor queueDescriptor) {
		super(queueDescriptor);
	}

	private void registerQueueCapability() {
		Properties props = new Properties();
		props.put(ResourceDescriptorConstants.CAPABILITY, "queue");
		props.put(ResourceDescriptorConstants.CAPABILITY_NAME, resourceId);
		Activator.getContext().registerService(IQueueManagerService.class.getName(), this, props);

	}

	private final Vector<IAction>	queue	= new Vector<IAction>();

	@Override
	public void empty() {
		log.debug("Cleaning the queue...");
		queue.clear();
	}

	@Override
	public QueueResponse execute() throws CapabilityException {
		// initialize queue response
		long startTime = 0;
		long stopTime = 0;

		/* start time */
		startTime = java.lang.System.currentTimeMillis();

		QueueResponse queueResponse = QueueResponse.newQueueResponse(queue);

		/* get protocol session manager */
		IProtocolSessionManager protocolSessionManager = null;
		try {
			IProtocolManager protocolManager = Activator.getProtocolManagerService();
			protocolSessionManager = protocolManager.getProtocolSessionManager(resourceId);
		} catch (ProtocolException e) {
			throw new CapabilityException(e);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}

		/* prepare action */
		try {
			log.debug("Preparing queue");
			ActionResponse prepareResponse = prepare(protocolSessionManager);
			queueResponse.setPrepareResponse(prepareResponse);
			log.debug("Prepared!");
		} catch (ActionException e1) {
			throw new CapabilityException(e1);
		}

		boolean errorInPrepare = false;
		if (queueResponse.getPrepareResponse().getStatus() == ActionResponse.STATUS.ERROR) {
			errorInPrepare = true;
		}

		// note that restore should not be executed if there's been an error in prepare
		if (!errorInPrepare) {

			/* execute queued actions */
			try {
				queueResponse = executeQueuedActions(queueResponse, protocolSessionManager);
			} catch (ActionException e) {
				throw new CapabilityException(e);
			}

			/* Look for errors */
			boolean errorHappened = false;
			for (ActionResponse actionResponse : queueResponse.getResponses()) {
				if (actionResponse.getStatus() == ActionResponse.STATUS.ERROR) {
					errorHappened = true;
					break;
				}
			}

			if (!errorHappened) {
				try {
					/* commit action */
					log.debug("Confirming actions");
					ActionResponse confirmResponse = confirm(protocolSessionManager);
					queueResponse.setConfirmResponse(confirmResponse);
					log.debug("Confirmed!");

					if (confirmResponse.getStatus() == ActionResponse.STATUS.ERROR)
						errorHappened = true;

				} catch (ActionException e) {
					throw new CapabilityException(e);
				}
			}

			if (errorHappened) {
				/* restore action */
				assert protocolSessionManager != null;
				try {
					log.debug("Restoring queue");
					ActionResponse restoreResponse = restore(protocolSessionManager);
					queueResponse.setRestoreResponse(restoreResponse);
					log.debug("Restored!");
				} catch (ActionException e) {
					throw new CapabilityException(e);
				}
			}

		}

		empty();

		/* stop time */
		stopTime = java.lang.System.currentTimeMillis();
		queueResponse.setTotalTime(stopTime - startTime);

		return queueResponse;
	}

	/**
	 * @param queueResponse
	 *            to complete with actionResponses
	 * @param protocolSessionManager
	 *            to use in actions execution
	 * @return given queueResponse with executed actions responses.
	 * @throws ActionException
	 *             if an Action throws it during its execution
	 */
	private QueueResponse executeQueuedActions(QueueResponse queueResponse, IProtocolSessionManager protocolSessionManager) throws ActionException {

		int numAction = 0;
		for (IAction action : queue) {
			/* use pool for get protocol session */
			log.debug("getting protocol session...");
			log.debug("Executing action: " + action.getActionID());
			log.debug("Trying to print params:" + action.getParams());
			ActionResponse actionResponse = action.execute(protocolSessionManager);
			queueResponse.getResponses().set(numAction, actionResponse);
			numAction++;

			// If an action returned error, queue should stop executing actions.
			// Restore mechanism should be activated in this case.
			if (actionResponse.getStatus() == ActionResponse.STATUS.ERROR)
				break;
		}
		return queueResponse;
	}

	private ActionResponse confirm(IProtocolSessionManager protocolSessionManager) throws ActionException, CapabilityException {
		IAction confirmAction = getActionSet().obtainAction(QueueConstants.CONFIRM);
		if (confirmAction == null) {
			throw new CapabilityException("Error obtaining ConfirmAction");
		}
		ActionResponse restoreResponse = confirmAction.execute(protocolSessionManager);
		return restoreResponse;
	}

	private ActionResponse prepare(IProtocolSessionManager protocolSessionManager) throws ActionException, CapabilityException {
		IActionSet actionSet = getActionSet();
		IAction prepareAction = actionSet.obtainAction(QueueConstants.PREPARE);
		return prepareAction.execute(protocolSessionManager);
	}

	private ActionResponse restore(IProtocolSessionManager protocolSessionManager) throws ActionException, CapabilityException {
		IAction restoreAction = getActionSet().obtainAction(QueueConstants.RESTORE);
		return restoreAction.execute(protocolSessionManager);
	}

	@Override
	public List<IAction> getActions() {
		log.debug("Get actions");
		List<IAction> actions = new ArrayList<IAction>();
		for (IAction action : queue) {
			actions.add(action);
		}
		return actions;
	}

	@Override
	public void queueAction(IAction action) {
		log.debug("Queue new action");
		queue.add(action);

	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String protocol = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_PROTOCOL);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

		try {
			return Activator.getQueueActionSet(name, version, protocol);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
	}

	private List<IAction> getQueue() {
		log.debug("Getting queue");
		List<IAction> actions = new ArrayList<IAction>();
		Iterator<IAction> actionQueue = queue.iterator();
		while (actionQueue.hasNext()) {
			actions.add(actionQueue.next());
		}
		return actions;
	}

	/**
	 * Implementation for the execution of a single action. Clean the queue, add an action and send the message
	 */
	private QueueResponse dummyExecute(Object action) throws CapabilityException {
		queueAction((IAction) action);
		return execute();
	}

	@Override
	public Object sendMessage(String idOperation, Object params) throws CapabilityException {
		log.debug("Sending message to Queue Capability");
		try {

			if (idOperation.equals(QueueConstants.EXECUTE)) {
				return execute();
			} else if (idOperation.equals(QueueConstants.GETQUEUE)) {
				return getQueue();
			} else if (idOperation.equals(QueueConstants.MODIFY)) {
				return modify(params);
			} else if (idOperation.equals(QueueConstants.DUMMYEXECUTE)) {
				return dummyExecute(params);
			}

		} catch (CapabilityException e) {
			throw new CapabilityException(e);
		}
		// TODO ADD NECESSARY INFORMATION
		return Response.okResponse(idOperation);
	}

	private Object modify(Object params) {
		if (params instanceof ModifyParams) {
			ModifyParams modifyParams = (ModifyParams) params;
			if (modifyParams.getQueueOper() == ModifyParams.Operations.REMOVE) {
				return remove(modifyParams.getPosAction());
			}
		}
		// TODO ADD NECESSARY INFORMATION
		return Response.okResponse(QueueConstants.MODIFY);
	}

	private Object remove(int posAction) {
		queue.remove(posAction);
		return Response.okResponse(QueueConstants.MODIFY, "Remove operation in pos: " + posAction);

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
	protected void initializeCapability() throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void shutdownCapability() throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public Response sendRefreshActions() {
		// there is no need of startup actions, queue is operative just after activation.
		return Response.okResponse("");
	}

}
