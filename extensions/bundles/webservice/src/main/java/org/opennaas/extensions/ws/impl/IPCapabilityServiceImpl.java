package org.opennaas.extensions.ws.impl;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.router.capability.ip.IIPCapability;
import org.opennaas.extensions.router.capability.ip.IPCapability;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.ws.services.IIPCapabilityService;

/**
 * @author Jordi Puig
 */
@WebService(portName = "IPCapabilityPort", serviceName = "IPCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
public class IPCapabilityServiceImpl extends GenericCapabilityServiceImpl implements IIPCapabilityService {

	Log	log	= LogFactory.getLog(IPCapabilityServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IIPCapabilityService#setIPv4(java.lang.String, org.opennaas.extensions.router.model.LogicalDevice,
	 * org.opennaas.extensions.router.model.IPProtocolEndpoint)
	 */
	@Override
	public void setIPv4(String resourceId, LogicalDevice logicalDevice, IPProtocolEndpoint ip) throws CapabilityException {
		try {
			IIPCapability iIPCapability = (IIPCapability) getCapability(resourceId, IPCapability.CAPABILITY_TYPE);
			iIPCapability.setIPv4(logicalDevice, ip);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IIPCapabilityService#setInterfaceDescription(java.lang.String,
	 * org.opennaas.extensions.router.model.LogicalPort)
	 */
	@Override
	public void setInterfaceDescription(String resourceId, LogicalPort iface) throws CapabilityException {
		try {
			IIPCapability iIPCapability = (IIPCapability) getCapability(resourceId, IPCapability.CAPABILITY_TYPE);
			iIPCapability.setInterfaceDescription(iface);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

}
