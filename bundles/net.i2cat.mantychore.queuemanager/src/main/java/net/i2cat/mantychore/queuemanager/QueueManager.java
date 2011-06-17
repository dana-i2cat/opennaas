package net.i2cat.mantychore.queuemanager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
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

	private void registerQueueCapability() {
		Properties props = new Properties();
		props.put(ResourceDescriptorConstants.CAPABILITY, "queue");
		props.put(ResourceDescriptorConstants.CAPABILITY_NAME, resourceId);
		Activator.getContext().registerService(IQueueManagerService.class.getName(), this, props);

	}

	private final BlockingQueue<IAction>	queue	= new LinkedBlockingQueue<IAction>();

	@Override
	public void empty() {
		log.debug("Cleaning the queue...");
		queue.clear();
	}

	@Override
	public List<ActionResponse> execute() throws CapabilityException {
		IProtocolSessionManager protocolSessionManager = null;
		try {
			IProtocolManager protocolManager = (IProtocolManager) Activator.getProtocolManagerService();
			protocolSessionManager = protocolManager.getProtocolSessionManager(resourceId);

			/* Always i have to be one response */
			log.debug("Preparing queue");
			prepare(protocolSessionManager);
			log.debug("Prepared!");

		} catch (Exception e1) {
			throw new CapabilityException(e1);
		}

		try {
			List<ActionResponse> responses = new Vector<ActionResponse>();

			/* execute action */
			for (IAction action : queue) {
				/* use pool for get protocol session */
				log.debug("getting protocol session...");
				log.debug("Executing action: " + action.getActionID());
				log.debug("Trying to print params:" + action.getParams());
				responses.add(action.execute(protocolSessionManager));
				log.debug("Executed!");
			}

			/* commit action */
			log.debug("Confirming actions");
			ActionResponse actionResponse = confirm(protocolSessionManager);
			log.debug("Confirmed!");

			responses.add(actionResponse);

			/* clear queue */
			queue.clear();
			log.debug("clearing queue");
			return responses;

		} catch (Exception e1) {
			/*
			 * FIXME, IT IS IMPOSSIBLE TO THROW THIS EXCEPTION WITHOUT BACKUP. IT COULD CORRUPT CONFIGURATION
			 */
			assert protocolSessionManager != null;
			restore(protocolSessionManager);
			throw new CapabilityException(e1);

		}

	}

	private ActionResponse confirm(IProtocolSessionManager protocolSessionManager) throws ActionException, CapabilityException {
		IAction confirmAction = getActionSet().obtainAction(ActionConstants.CONFIRM);
		ActionResponse restoreResponse = confirmAction.execute(protocolSessionManager);
		return restoreResponse;
	}

	private void prepare(IProtocolSessionManager protocolSessionManager) throws ActionException, CapabilityException {
		IAction prepareAction = getActionSet().obtainAction(ActionConstants.PREPARE);
		prepareAction.execute(protocolSessionManager);
	}

	private void restore(IProtocolSessionManager protocolSessionManager) throws ActionException, CapabilityException {
		IAction restoreAction = getActionSet().obtainAction(ActionConstants.RESTORE);
		restoreAction.execute(protocolSessionManager);
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

	@Override
	public Object sendMessage(String idOperation, Object paramsModel) throws CapabilityException {
		log.debug("Sending message to Queue Capability");
		try {

			if (idOperation.equals(QueueManagerConstants.EXECUTE)) {
				return execute();
			} else if (idOperation.equals(QueueManagerConstants.GETQUEUE)) {
				return getQueue();
			}

		} catch (CapabilityException e) {
			throw new CapabilityException(e);
		}

		return Response.okResponse(idOperation);
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

}
