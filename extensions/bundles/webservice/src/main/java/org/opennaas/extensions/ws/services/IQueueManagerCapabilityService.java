package org.opennaas.extensions.ws.services;

import java.util.List;

import javax.jws.WebService;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.ModifyParams;

/**
 * 
 * @author Eli Rigol
 * 
 */
@WebService(portName = "QueueManagerCapabilityPort", serviceName = "QueueManagerCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
public interface IQueueManagerCapabilityService {

	/**
	 * Execute the queue for the given resource
	 * 
	 * @param resourceId
	 * @throws ProtocolException
	 * @throws CapabilityException
	 * @throws ActionException
	 */

	// TODO return QueueResponse
	public void execute(String resourceId) throws ProtocolException,
			CapabilityException, ActionException;

	/**
	 * 
	 * @param resourceId
	 * @throws CapabilityException
	 */
	public void clear(String resourceId) throws CapabilityException;

	/**
	 * 
	 * @param resourceId
	 * @return the name of the capabilities in the queue
	 * @throws CapabilityException
	 */

	public List<String> getActions(String resourceId) throws CapabilityException;

	/**
	 * 
	 * @param resourceId
	 * @param modifyParams
	 * @throws CapabilityException
	 */
	public void modify(String resourceId, ModifyParams modifyParams) throws CapabilityException;
}
