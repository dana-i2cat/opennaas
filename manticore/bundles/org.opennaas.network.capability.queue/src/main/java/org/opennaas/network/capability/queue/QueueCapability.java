package org.opennaas.network.capability.queue;

import java.util.Vector;

import net.i2cat.mantychore.queuemanager.IQueueManagerService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.queue.QueueResponse;

/**
 * @author Jordi Puig
 */
public class QueueCapability extends AbstractCapability implements IQueueService {

	public static String	CAPABILITY_NAME	= "netqueue";

	Log						log				= LogFactory.getLog(QueueCapability.class);

	private String			resourceId		= "";

	/**
	 * QueueCapability constructor
	 * 
	 * @param descriptor
	 * @param resourceId
	 */
	public QueueCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new queue capability");
	}

	/*
	 * Execute the action defined in the idOperation param (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.ICapability#sendMessage(java.lang.String, java.lang.Object)
	 */
	@Override
	public Object sendMessage(String idOperation, Object params) {
		log.debug("Sending message to queue capability");
		try {
			IQueueManagerService queueManager = Activator.getQueueManagerService(resourceId);
			IAction action = createAction(idOperation);
			action.setParams(params);
			action.setModelToUpdate(resource.getModel());
			queueManager.queueAction(action);

		} catch (Exception e) {
			Vector<String> errorMsgs = new Vector<String>();
			errorMsgs
					.add(e.getMessage() + ":" + '\n' + e.getLocalizedMessage());
			return Response.errorResponse(idOperation, errorMsgs);
		}

		return Response.queuedResponse(idOperation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#getActionSet()
	 */
	@Override
	public IActionSet getActionSet() throws CapabilityException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.network.capability.queue.IQueueService#execute()
	 */
	@Override
	public QueueResponse execute() throws CapabilityException {
		// TODO Dummy method. Need to implement the functionality
		QueueResponse queueResponse = new QueueResponse();
		queueResponse.getConfirmResponse().setStatus(STATUS.OK);
		queueResponse.getRefreshResponse().setStatus(STATUS.OK);
		return queueResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#activateCapability()
	 */
	@Override
	protected void activateCapability() throws CapabilityException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#deactivateCapability()
	 */
	@Override
	protected void deactivateCapability() throws CapabilityException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#initializeCapability()
	 */
	@Override
	protected void initializeCapability() throws CapabilityException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#shutdownCapability()
	 */
	@Override
	protected void shutdownCapability() throws CapabilityException {
		throw new UnsupportedOperationException();
	}

}
