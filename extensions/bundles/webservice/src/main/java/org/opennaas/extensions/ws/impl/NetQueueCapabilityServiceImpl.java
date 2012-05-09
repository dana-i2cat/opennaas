package org.opennaas.extensions.ws.impl;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.network.capability.queue.IQueueCapability;
import org.opennaas.extensions.network.capability.queue.QueueCapability;
import org.opennaas.extensions.ws.services.INetQueueCapabilityService;

/**
 * 
 * @author Adrian Rosello
 * 
 */
@WebService(portName = "NetQueueCapabilityPort", serviceName = "NetQueueCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
public class NetQueueCapabilityServiceImpl extends GenericCapabilityServiceImpl implements INetQueueCapabilityService {

	Log	log	= LogFactory.getLog(NetQueueCapabilityServiceImpl.class);

	@Override
	public void execute(String resourceId) throws CapabilityException {
		try {
			IQueueCapability iQueueCapability = (IQueueCapability) getCapability(resourceId, QueueCapability.CAPABILITY_TYPE);
			iQueueCapability.execute();
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}
}