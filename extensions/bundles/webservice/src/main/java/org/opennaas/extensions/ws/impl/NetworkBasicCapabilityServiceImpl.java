package org.opennaas.extensions.ws.impl;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.network.capability.basic.INetworkBasicCapability;
import org.opennaas.extensions.network.model.topology.Interface;
import org.opennaas.extensions.ws.services.INetworkBasicCapabilityService;

/**
 * @author Jordi Puig
 */
@WebService(portName = "NetworkBasicCapabilityPort", serviceName = "NetworkBasicCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
public class NetworkBasicCapabilityServiceImpl extends GenericCapabilityService implements INetworkBasicCapabilityService {

	Log	log	= LogFactory.getLog(NetworkBasicCapabilityServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.INetworkBasicCapabilityService#addResource(java.lang.String, org.opennaas.core.resources.IResource)
	 */
	@Override
	public void addResource(String resourceId, String resourceToAdd) throws CapabilityException {
		try {
			INetworkBasicCapability iNetworkBasicCapability = (INetworkBasicCapability) getCapability(resourceId, INetworkBasicCapability.class);
			iNetworkBasicCapability.addResource(getResource(resourceToAdd));
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		} catch (ResourceException e) {
			log.error(e);
			new CapabilityException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.INetworkBasicCapabilityService#removeResource(java.lang.String, org.opennaas.core.resources.IResource)
	 */
	@Override
	public void removeResource(String resourceId, String resourceToRemove) throws CapabilityException {
		try {
			INetworkBasicCapability iNetworkBasicCapability = (INetworkBasicCapability) getCapability(resourceId, INetworkBasicCapability.class);
			iNetworkBasicCapability.removeResource(getResource(resourceToRemove));
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		} catch (ResourceException e) {
			log.error(e);
			new CapabilityException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.INetworkBasicCapabilityService#l2attach(java.lang.String,
	 * org.opennaas.extensions.network.model.topology.Interface, org.opennaas.extensions.network.model.topology.Interface)
	 */
	@Override
	public void l2attach(String resourceId, Interface interface1, Interface interface2) throws CapabilityException {
		try {
			INetworkBasicCapability iNetworkBasicCapability = (INetworkBasicCapability) getCapability(resourceId, INetworkBasicCapability.class);
			iNetworkBasicCapability.l2attach(interface1, interface2);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.INetworkBasicCapabilityService#l2detach(java.lang.String,
	 * org.opennaas.extensions.network.model.topology.Interface, org.opennaas.extensions.network.model.topology.Interface)
	 */
	@Override
	public void l2detach(String resourceId, Interface interface1, Interface interface2) throws CapabilityException {
		try {
			INetworkBasicCapability iNetworkBasicCapability = (INetworkBasicCapability) getCapability(resourceId, INetworkBasicCapability.class);
			iNetworkBasicCapability.l2detach(interface1, interface2);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

}
