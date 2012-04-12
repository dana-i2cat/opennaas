package org.opennaas.extensions.queuemanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.command.Response.Status;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.ModifyParams;
import org.opennaas.core.resources.queue.QueueConstants;
import org.opennaas.core.resources.queue.QueueResponse;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;

public class QueueManager extends AbstractCapability implements
		IQueueManagerService {

	public final static String		QUEUE			= "queue";
	private final Log				log				= LogFactory.getLog(QueueManager.class);
	private String					resourceId		= "";
	private ServiceRegistration		registration	= null;
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
	 * @see net.i2cat.mantychore.queuemanager.IQueueManagerService#empty()
	 */
	@Override
	public void empty() {
		log.debug("Cleaning the queue...");
		queue.clear();
	}

	/*
	 * @see net.i2cat.mantychore.queuemanager.IQueueManagerService#execute()
	 */
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
		empty();

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

		empty();
		return queueResponse;
	}

	/*
	 * @see net.i2cat.mantychore.queuemanager.IQueueManagerService#getActions()
	 */
	@Override
	public List<IAction> getActions() {
		log.debug("Get actions");
		List<IAction> actions = new ArrayList<IAction>();
		for (IAction action : queue) {
			actions.add(action);
		}
		return actions;
	}

	/*
	 * @see net.i2cat.mantychore.queuemanager.IQueueManagerService#queueAction(org.opennaas.core.resources.action.IAction)
	 */
	@Override
	public void queueAction(IAction action) {
		log.debug("Queue new action");
		queue.add(action);

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
	 * @see org.opennaas.core.resources.capability.ICapability#sendMessage(java.lang.String, java.lang.Object)
	 */
	@Override
	public Object sendMessage(String idOperation, Object params)
			throws CapabilityException {
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

	/*
	 * @see org.opennaas.core.resources.capability.AbstractCapability#sendRefreshActions()
	 */
	@Override
	public Response sendRefreshActions() {
		// there is no need of startup actions, queue is operative just after
		// activation.
		return Response.okResponse("");
	}

	/*
	 * @see org.opennaas.core.resources.capability.AbstractCapability#activateCapability()
	 */
	@Override
	protected void activateCapability() throws CapabilityException {
	}

	/*
	 * @see org.opennaas.core.resources.capability.AbstractCapability#deactivateCapability()
	 */
	@Override
	protected void deactivateCapability() throws CapabilityException {
	}

	/*
	 *
	 * @see org.opennaas.core.resources.capability.AbstractCapability#initializeCapability()
	 */
	@Override
	protected void initializeCapability() throws CapabilityException {
		log.debug("Registering Queue Capability");
		registerQueueCapability();
		log.debug("Registered!");
	}

	/*
	 * @see org.opennaas.core.resources.capability.AbstractCapability#shutdownCapability()
	 */
	@Override
	protected void shutdownCapability() throws CapabilityException {
		log.debug("Unregistering Queue Capability");
		try {
			unregisterQueueCapability();
		} catch (InvalidSyntaxException e) {
			log.error(e.getMessage());
			throw new CapabilityException(e);
		} catch (BundleException e) {
			log.error(e.getMessage());
			throw new CapabilityException(e);
		}
		log.debug("Unregistered!");
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
				Response response = ((AbstractCapability) capab).sendRefreshActions();
				if (!response.getStatus().equals(Status.OK)) {
					throw new CapabilityException(
							"model refresh, when calling sendRefreshActions");
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
					// TODO WHO IS IT THE RESPONSIBLE TO CREATE A CHILD VIRTUAL RESOURCE
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
	 * Queue execution stops at the first action to return error.
	 * Both an error ActionResponse an an ActionException are interpreted as an error.
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
	 * Get the action list of the queue
	 *
	 * @return a list of IAction
	 */
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
	 * Implementation for the execution of a single action. <br>
	 * Clean the queue, add an action and send the message
	 *
	 * @param action
	 * @return the queue response
	 * @throws CapabilityException
	 */
	private QueueResponse dummyExecute(Object action)
			throws CapabilityException {
		queueAction((IAction) action);
		return execute();
	}

	/**
	 * @param params
	 *            to modify
	 * @return the response of modify the paramsor remove the action
	 */
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

	/**
	 * @param posAction
	 *            position in the queue of the action to remove
	 * @return the response of remove the action
	 */
	private Object remove(int posAction) {

		try {
			queue.remove(posAction);
			return Response.okResponse(QueueConstants.MODIFY,
					"Remove operation in pos: " + posAction);
		} catch (ArrayIndexOutOfBoundsException e) {
			Vector<String> errors = new Vector<String>(1);
			errors.add("Invalid index. Index " + posAction + " does not point to any action in the queue.");
			return Response.errorResponse(QueueConstants.MODIFY, errors);
		}

	}

	/**
	 * Register the capability
	 */
	private void registerQueueCapability() {
		Properties props = new Properties();
		props.setProperty(ResourceDescriptorConstants.CAPABILITY, "queue");
		props.setProperty(ResourceDescriptorConstants.CAPABILITY_NAME, resourceId);
		registration = Activator.getContext().registerService(
				IQueueManagerService.class.getName(), this, props);
	}

	/**
	 * Unregister the capability
	 *
	 * @throws InvalidSyntaxException
	 * @throws BundleException
	 */
	private void unregisterQueueCapability() throws InvalidSyntaxException, BundleException {
		if (registration != null) {
			registration.unregister();
		}
	}
}
