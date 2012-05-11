package org.opennaas.extensions.ws.impl;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.ModifyParams;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.queuemanager.QueueManager;
import org.opennaas.extensions.ws.services.IQueueManagerCapabilityService;

/**
 * 
 * @author Eli Rigol
 * 
 */
@WebService(portName = "QueueManagerCapabilityPort", serviceName = "QueueManagerCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
public class QueueManagerCapabilityServiceImpl extends GenericCapabilityService
		implements IQueueManagerCapabilityService {

	Log	log	= LogFactory.getLog(QueueManagerCapabilityServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IQueueManagerCapabilityService#execute(java.lang.String)
	 */
	@Override
	public void execute(String resourceId) throws ProtocolException, CapabilityException, ActionException {
		try {
			IQueueManagerCapability iQueueManagerCapability = (IQueueManagerCapability) getCapability(resourceId, QueueManager.class);
			iQueueManagerCapability.execute();
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IQueueManagerCapabilityService#clear(java.lang.String)
	 */
	@Override
	public void clear(String resourceId) throws CapabilityException {
		try {
			IQueueManagerCapability iQueueManagerCapability = (IQueueManagerCapability) getCapability(resourceId, QueueManager.class);
			iQueueManagerCapability.clear();
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IQueueManagerCapabilityService#getActions(java.lang.String)
	 */

	@Override
	public List<String> getActions(String resourceId) throws CapabilityException {
		try {
			IQueueManagerCapability iQueueManagerCapability = (IQueueManagerCapability) getCapability(resourceId, QueueManager.class);
			List<IAction> queueActions = iQueueManagerCapability.getActions();

			// returns the queue action names
			List<String> qActionNames = new ArrayList<String>();
			for (IAction action : queueActions) {
				qActionNames.add(action.getActionID());
			}
			return qActionNames;
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IQueueManagerCapabilityService#modify(java.lang.String,
	 * org.opennaas.core.resources.queue.ModifyParams)
	 */
	@Override
	public void modify(String resourceId, ModifyParams modifyParams) throws CapabilityException {
		try {
			IQueueManagerCapability iQueueManagerCapability = (IQueueManagerCapability) getCapability(resourceId, QueueManager.class);
			iQueueManagerCapability.modify(modifyParams);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

}
