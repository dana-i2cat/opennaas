package org.opennaas.extensions.ws.impl;

import java.util.List;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.bod.capability.l2bod.IL2BoDCapability;
import org.opennaas.extensions.bod.capability.l2bod.RequestConnectionParameters;
import org.opennaas.extensions.network.model.topology.Interface;
import org.opennaas.extensions.ws.services.IL2BoDCapabilityService;

/**
 * @author Jordi Puig
 */
@WebService(portName = "L2BoDCapabilityPort", serviceName = "L2BoDCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
public class L2BoDCapabilityServiceImpl extends GenericCapabilityService implements IL2BoDCapabilityService {

	Log	log	= LogFactory.getLog(L2BoDCapabilityServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IL2BoDCapabilityService#requestConnection(java.lang.String,
	 * org.opennaas.extensions.bod.capability.l2bod.RequestConnectionParameters)
	 */
	@Override
	public void requestConnection(String resourceId, RequestConnectionParameters parameters) throws CapabilityException {
		try {
			IL2BoDCapability iL2BoDCapability = (IL2BoDCapability) getCapability(resourceId, IL2BoDCapability.class);
			iL2BoDCapability.requestConnection(parameters);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IL2BoDCapabilityService#shutDownConnection(java.lang.String, java.util.List)
	 */
	@Override
	public void shutDownConnection(String resourceId, List<Interface> listInterfaces) throws CapabilityException {
		try {
			IL2BoDCapability iL2BoDCapability = (IL2BoDCapability) getCapability(resourceId, IL2BoDCapability.class);
			iL2BoDCapability.shutDownConnection(listInterfaces);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

}
