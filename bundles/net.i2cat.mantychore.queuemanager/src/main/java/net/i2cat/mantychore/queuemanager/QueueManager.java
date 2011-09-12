package net.i2cat.mantychore.queuemanager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import net.i2cat.nexus.resources.ActivatorException;
import net.i2cat.nexus.resources.action.ActionException;
import net.i2cat.nexus.resources.action.ActionResponse;
import net.i2cat.nexus.resources.action.IAction;
import net.i2cat.nexus.resources.action.IActionSet;
import net.i2cat.nexus.resources.capability.AbstractCapability;
import net.i2cat.nexus.resources.capability.CapabilityException;
import net.i2cat.nexus.resources.command.Response;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptorConstants;
import net.i2cat.nexus.resources.protocol.IProtocolManager;
import net.i2cat.nexus.resources.protocol.IProtocolSessionManager;
import net.i2cat.nexus.resources.queue.ModifyParams;
import net.i2cat.nexus.resources.queue.QueueConstants;
import net.i2cat.nexus.resources.queue.QueueResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

		IProtocolSessionManager protocolSessionManager = null;
		try {
			IProtocolManager protocolManager = Activator.getProtocolManagerService();
			protocolSessionManager = protocolManager.getProtocolSessionManager(resourceId);

			/* Always i have to be one response */
			log.debug("Preparing queue");
			ActionResponse prepareResponse = prepare(protocolSessionManager);

			/* status for actionResponse */
			queueResponse.setPrepareResponse(prepareResponse);
			log.debug("Prepared!");

		} catch (Exception e1) {
			throw new CapabilityException(e1);
		}

		try {
			int numAction = 0;
			for (IAction action : queue) {
				/* use pool for get protocol session */
				log.debug("getting protocol session...");
				log.debug("Executing action: " + action.getActionID());
				log.debug("Trying to print params:" + action.getParams());
				ActionResponse actionResponse = action.execute(protocolSessionManager);
				queueResponse.getResponses().set(numAction, actionResponse);
				numAction++;
				/* The action response didn't work, we need to do a restore */
				if (actionResponse.getStatus() == ActionResponse.STATUS.ERROR)
					throw new Exception();

			}

			/* commit action */
			log.debug("Confirming actions");
			ActionResponse confirmResponse = confirm(protocolSessionManager);
			queueResponse.setConfirmResponse(confirmResponse);
			log.debug("Confirmed!");

			/* clear queue */
			queue.clear();
			log.debug("clearing queue");

			/* stop time */
			stopTime = java.lang.System.currentTimeMillis();
			queueResponse.setTotalTime(stopTime - startTime);

			return queueResponse;
		} catch (Exception e1) {
			/* FIXME, IT IS IMPOSSIBLE TO THROW THIS EXCEPTION WITHOUT BACKUP. IT COULD CORRUPT CONFIGURATION */
			assert protocolSessionManager != null;
			try {
				ActionResponse restoreResponse = restore(protocolSessionManager);
				queueResponse.setRestoreResponse(restoreResponse);
				empty();

				stopTime = java.lang.System.currentTimeMillis();
				queueResponse.setTotalTime(stopTime - startTime);

				return queueResponse;
			} catch (Exception e2) {
				throw new CapabilityException(e2);

			}

		}

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
	public QueueResponse dummyExecute(Object action) throws CapabilityException {
		empty();
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
	public Response sendStartUpActions() {
		// there is no need of startup actions, queue is operative just after activation.
		return Response.okResponse("");
	}

}
