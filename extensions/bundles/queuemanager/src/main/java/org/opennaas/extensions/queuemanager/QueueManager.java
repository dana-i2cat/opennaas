package org.opennaas.extensions.queuemanager;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ResourceNotFoundException;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.ModifyParams;
import org.opennaas.core.resources.queue.QueueConstants;
import org.opennaas.core.resources.queue.QueueResponse;

public class QueueManager extends AbstractCapability implements
		IQueueManagerCapability {
	public final static String		CAPABILITY_TYPE	= "queue";
	public final static String		QUEUE			= CAPABILITY_TYPE;
	private final Log				log				= LogFactory.getLog(QueueManager.class);
	private String					resourceId		= "";
	private final Vector<IAction>	queue			= new Vector<IAction>();

	/**
	 * Constructor to test the component
	 * 
	 * @param queueDescriptor
	 */
	public QueueManager(CapabilityDescriptor queueDescriptor) {
		super(queueDescriptor);
	}

	/**
	 * @param descriptor
	 * @param resourceId
	 */
	public QueueManager(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Queue Capability");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.ICapability#getCapabilityName()
	 */
	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	/*
	 * @see org.opennaas.core.resources.capability.AbstractCapability#getActionSet()
	 */
	@Override
	public IActionSet getActionSet() throws CapabilityException {
		String name = this.descriptor
				.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String protocol = this.descriptor
				.getPropertyValue(ResourceDescriptorConstants.ACTION_PROTOCOL);
		String version = this.descriptor
				.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

		try {
			return Activator.getQueueActionSet(name, version, protocol);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#initialize()
	 */
	@Override
	public void initialize() throws CapabilityException {
		Dictionary<String, String> props = new Hashtable<String, String>();
		props.put(ResourceDescriptorConstants.CAPABILITY, "queue");
		props.put(ResourceDescriptorConstants.CAPABILITY_NAME, resourceId);
		registration = Activator.getContext().registerService(
				IQueueManagerCapability.class.getName(), this, props);
		// registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceName(), IQueueManagerCapability.class.getName());
		super.initialize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#shutdown()
	 */
	@Override
	public void shutdown() throws CapabilityException {
		registration.unregister();
		super.shutdown();
	}

	/*
	 * @see net.i2cat.mantychore.queuemanager.IQueueManagerCapability#clear()
	 */
	@Override
	public void clear() {
		log.info("Start of clear call");
		log.debug("Clearing the queue...");
		queue.clear();
		log.info("End of clear call");
	}

	/*
	 * @see net.i2cat.mantychore.queuemanager.IQueueManagerCapability#getActions()
	 */
	@Override
	public List<IAction> getActions() {
		log.info("Start of getActions call");
		log.debug("Get actions");
		List<IAction> actions = new ArrayList<IAction>();
		for (IAction action : queue) {
			actions.add(action);
		}
		log.info("End of getActions call");
		return actions;
	}

	/*
	 * @see net.i2cat.mantychore.queuemanager.IQueueManagerCapability#queueAction(org.opennaas.core.resources.action.IAction)
	 */
	@Override
	public void queueAction(IAction action) {
		log.info("Start of queueAction call");
		log.debug("Queue new action");
		queue.add(action);
		log.info("End of queueAction call");
	}

	/**
	 * @param params
	 *            to modify
	 * @return the response of modify the paramsor remove the action
	 * @throws CapabilityException
	 */
	@Override
	public void modify(ModifyParams modifyParams) throws CapabilityException {
		log.info("Start of modify call");
		if (modifyParams.getQueueOper() == ModifyParams.Operations.REMOVE) {
			remove(modifyParams.getPosAction());
		} else {
			throw new UnsupportedOperationException("Unsupported operation in modify: " + modifyParams.getQueueOper());
		}
		log.info("End of modify call");
	}

	/*
	 * @see net.i2cat.mantychore.queuemanager.IQueueManagerCapability#execute()
	 */
	@Override
	public QueueResponse execute() throws CapabilityException {
		log.info("Start of execute call");
		// initialize queue response
		long startTime = 0;
		long stopTime = 0;

		/* start time */
		startTime = java.lang.System.currentTimeMillis();

		QueueResponse queueResponse = QueueResponse.newQueueResponse(queue);

		/* get protocol session manager */
		IProtocolSessionManager protocolSessionManager = null;
		try {
			IProtocolManager protocolManager = Activator
					.getProtocolManagerService();
			protocolSessionManager = protocolManager
					.getProtocolSessionManager(resourceId);
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

		// note that restore should not be executed if there's been an error in
		// prepare
		if (!errorInPrepare) {
			boolean errorHappened = false;
			try {
				/* execute queued actions */
				queueResponse = executeQueuedActions(queueResponse,
						protocolSessionManager);

				/* Look for errors */
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
						queueResponse.setConfirmResponse(ActionResponse.errorResponse(QueueConstants.CONFIRM, e.getLocalizedMessage()));
						throw new CapabilityException(e);
					}

				}
			} catch (Exception e) {
				log.warn("Failed to execute queue", e);

				// restore action
				assert protocolSessionManager != null;
				try {
					log.debug("Restoring queue");
					ActionResponse restoreResponse = restore(protocolSessionManager);
					queueResponse.setRestoreResponse(restoreResponse);
					log.debug("Restored!");
					errorHappened = false;
				} catch (ActionException ex) {
					throw new CapabilityException(ex);
				}
			}

			if (errorHappened) {
				// restore action
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

		/* empty queue */
		clear();

		/* refresh operation */
		try {
			// FIXME WHAT CAN WE SO IF BOOTSTRAPPER IS NULL??
			if (resource.getBootstrapper() == null)
				throw new ResourceException("Null Bootstrapper found. Could not reset model");

			resource.getBootstrapper().resetModel(resource);
			sendRefresh();

		} catch (ResourceException resourceExcept) {
			log.warn("The resource couldn't reset its model...", resourceExcept);
		}

		try {
			ActionResponse refreshResponse = executeRefreshActions(protocolSessionManager);
			queueResponse.setRefreshResponse(refreshResponse);
		} catch (ActionException e) {
			throw new CapabilityException(e);
		}

		if (resource.getProfile() != null) {
			log.debug("Executing initModel from profile...");
			resource.getProfile().initModel(resource.getModel());
		}

		initVirtualResources();

		/* stop time */
		stopTime = java.lang.System.currentTimeMillis();
		queueResponse.setTotalTime(stopTime - startTime);

		clear();
		log.info("End of execute call");
		return queueResponse;
	}

	/**
	 * Refresh the actions of the queue
	 * 
	 * @throws CapabilityException
	 */
	private void sendRefresh() throws CapabilityException {
		for (ICapability capab : resource.getCapabilities()) {
			// abstract capabilities have to be initialized
			if (capab instanceof AbstractCapability) {
				log.debug("Executing capabilities startup...");
				try {
					((AbstractCapability) capab).sendRefreshActions();
				} catch (CapabilityException e) {
					throw new CapabilityException(
							"error in model refresh, when calling sendRefreshActions", e);
				}
			}
		}
	}

	/**
	 * @throws CapabilityException
	 */
	private void initVirtualResources() throws CapabilityException {
		String typeResource = resource.getResourceIdentifier().getType();
		List<String> nameLogicalRouters = resource.getModel().getChildren();

		IResourceManager manager;
		try {
			manager = Activator.getResourceManagerService();
		} catch (ActivatorException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw new CapabilityException("Can't get ResourceManagerService!");
		}

		// initialize each resource
		try {
			for (String nameResource : nameLogicalRouters) {
				try {
					manager.getIdentifierFromResourceName(typeResource,
							nameResource);
				} catch (ResourceNotFoundException e) {
					// TODO WHO IS RESPONSIBLE FOR CREATING A CHILD VIRTUAL RESOURCE?
					log.error(e.getMessage());
					log.info("Since this resource didn't exist, it has to be created.");
					ResourceDescriptor newResourceDescriptor = newResourceDescriptor(
							resource.getResourceDescriptor(), nameResource);
					// create new resources
					manager.createResource(newResourceDescriptor);

				}
			}
		} catch (ResourceException e) {
			throw new CapabilityException(e);
		}

	}

	// FIXME this parameters shouldn't be in the queue because it is an opennaas module <br>
	// Aux stuff from former Refresh as Karaf Command in OpenNaaS's Resources.

	/**
	 * Create a new resource descriptor with the name = nameResource <br>
	 * 
	 * 
	 * @param resourceDescriptor
	 * @param nameResource
	 * @return the resourceDescriptor
	 * @throws ResourceException
	 */
	private ResourceDescriptor newResourceDescriptor(
			ResourceDescriptor resourceDescriptor, String nameResource)
			throws ResourceException {
		try {
			ResourceDescriptor newResourceDescriptor = (ResourceDescriptor) resourceDescriptor
					.clone();

			// the profiles will not be cloned
			newResourceDescriptor.setProfileId("");
			// we delete chassis capability, a logical resource can't create new
			// logical devices or new interfaces
			newResourceDescriptor.removeCapabilityDescriptor("chassis");
			// Wet set the resource name
			newResourceDescriptor.getInformation().setName(nameResource);

			/* added virtual description */
			Map<String, String> properties = new HashMap<String, String>();
			properties.put(ResourceDescriptor.VIRTUAL, "true");
			newResourceDescriptor.setProperties(properties);

			return newResourceDescriptor;
		} catch (Exception e) {
			throw new ResourceException(e.getMessage());
		}
	}

	// end of Aux stuff from former Refresh as Karaf Command in OpenNaaS's
	// Resources.

	/**
	 * Executes actions in the queue.
	 * 
	 * Queue execution stops at the first action to return error. Both an error ActionResponse an an ActionException are interpreted as an error.
	 * 
	 * @param queueResponse
	 *            to complete with actionResponses
	 * @param protocolSessionManager
	 *            to use in actions execution
	 * @return given queueResponse with executed actions responses.
	 */
	private QueueResponse executeQueuedActions(QueueResponse queueResponse,
			IProtocolSessionManager protocolSessionManager) {

		int numAction = 0;
		for (IAction action : queue) {

			log.debug("Executing action: " + action.getActionID());
			log.debug("Trying to print params:" + action.getParams());
			ActionResponse actionResponse;
			try {
				actionResponse = action.execute(protocolSessionManager);
			} catch (ActionException e) {
				log.error("Error executing action " + action.getActionID(), e);
				actionResponse = ActionResponse.errorResponse(action.getActionID(), e.getLocalizedMessage());
			}
			queueResponse.getResponses().set(numAction, actionResponse);
			numAction++;

			// If an action returned error, queue should stop executing actions.
			// Restore mechanism should be activated in this case.
			if (actionResponse.getStatus() == ActionResponse.STATUS.ERROR)
				break;
		}
		return queueResponse;
	}

	/**
	 * Execute the actions of the queue
	 * 
	 * @param protocolSessionManager
	 * @return the action response
	 * @throws ActionException
	 */
	private ActionResponse executeRefreshActions(IProtocolSessionManager protocolSessionManager) throws ActionException {
		ActionResponse refreshResponse = ActionResponse.okResponse(QueueConstants.REFRESH);
		for (IAction action : queue) {
			/* use pool for get protocol session */
			log.debug("getting protocol session...");
			log.debug("Executing action: " + action.getActionID());
			log.debug("Trying to print params:" + action.getParams());
			ActionResponse actionResponse = action
					.execute(protocolSessionManager);

			// If an action returned error, queue should stop executing actions.
			// Restore mechanism should be activated in this case.
			if (actionResponse.getStatus() == ActionResponse.STATUS.ERROR) {
				return actionResponse;
			}
		}
		return refreshResponse;
	}

	/**
	 * Execute the confirm action of the queue
	 * 
	 * @param protocolSessionManager
	 * @return the action response
	 * @throws ActionException
	 * @throws CapabilityException
	 */
	private ActionResponse confirm(
			IProtocolSessionManager protocolSessionManager)
			throws ActionException, CapabilityException {
		IAction confirmAction = getActionSet().obtainAction(
				QueueConstants.CONFIRM);
		if (confirmAction == null) {
			throw new CapabilityException("Error obtaining ConfirmAction");
		}
		ActionResponse restoreResponse = confirmAction
				.execute(protocolSessionManager);
		return restoreResponse;
	}

	/**
	 * Execute the prepare action of the queue
	 * 
	 * @param protocolSessionManager
	 * @return the action response
	 * @throws ActionException
	 * @throws CapabilityException
	 */
	private ActionResponse prepare(
			IProtocolSessionManager protocolSessionManager)
			throws ActionException, CapabilityException {
		IActionSet actionSet = getActionSet();
		IAction prepareAction = actionSet.obtainAction(QueueConstants.PREPARE);
		return prepareAction.execute(protocolSessionManager);
	}

	/**
	 * Execute the restore action of the queue
	 * 
	 * @param protocolSessionManager
	 * @return the action response
	 * @throws ActionException
	 * @throws CapabilityException
	 */
	private ActionResponse restore(
			IProtocolSessionManager protocolSessionManager)
			throws ActionException, CapabilityException {
		IAction restoreAction = getActionSet().obtainAction(
				QueueConstants.RESTORE);
		return restoreAction.execute(protocolSessionManager);
	}

	/**
	 * @param posAction
	 *            position in the queue of the action to remove
	 * @return the response of remove the action
	 * @throws CapabilityException
	 *             if invalid position is given
	 */
	private void remove(int posAction) throws CapabilityException {
		try {
			queue.remove(posAction);
			log.debug("Removed action in pos: " + posAction);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new CapabilityException("Invalid index. Index " + posAction + " does not point to any action in the queue.");
		}
	}

}
