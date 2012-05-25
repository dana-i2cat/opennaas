package org.opennaas.extensions.ws.impl;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.router.capability.ip.IIPCapability;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.ws.services.IIPCapabilityService;

/**
 * @author Jordi Puig
 */
@WebService(portName = "IPCapabilityPort", serviceName = "IPCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
public class IPCapabilityServiceImpl extends GenericCapabilityService implements IIPCapabilityService {

	Log	log	= LogFactory.getLog(IPCapabilityServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IIPCapabilityService#setIPv4(java.lang.String, org.opennaas.extensions.router.model.NetworkPort,
	 * org.opennaas.extensions.router.model.IPProtocolEndpoint)
	 */
	@Override
	public void setIPv4(String resourceId, NetworkPort networkPort, IPProtocolEndpoint ip) throws CapabilityException {
		try {
			log.info("Start of setIPv4 call");
			IIPCapability iIPCapability = (IIPCapability) getCapability(resourceId, IIPCapability.class);
			iIPCapability.setIPv4(networkPort, ip);
			log.info("End of setIPv4 call");
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
			log.info("Start of setInterfaceDescription call");
			IIPCapability iIPCapability = (IIPCapability) getCapability(resourceId, IIPCapability.class);
			iIPCapability.setInterfaceDescription(iface);
			log.info("End of setInterfaceDescription call");
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

}
