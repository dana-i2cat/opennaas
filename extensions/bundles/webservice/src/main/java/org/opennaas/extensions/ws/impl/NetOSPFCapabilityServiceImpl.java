package org.opennaas.extensions.ws.impl;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.network.capability.ospf.INetOSPFCapability;
import org.opennaas.extensions.network.capability.ospf.NetOSPFCapability;
import org.opennaas.extensions.ws.services.INetOSPFCapabilityService;

/**
 * 
 * @author Adrian Rosello
 * 
 */
@WebService(portName = "NetOSPFCapabilityPort", serviceName = "NetOSPFCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
public class NetOSPFCapabilityServiceImpl extends GenericCapabilityServiceImpl implements INetOSPFCapabilityService {

	Log	log	= LogFactory.getLog(NetOSPFCapabilityServiceImpl.class);

	@Override
	public void activateOSPF(String resourceId) throws CapabilityException {
		try {
			INetOSPFCapability iNetOSPFCapability = (INetOSPFCapability) getCapability(resourceId, NetOSPFCapability.CAPABILITY_NAME);
			iNetOSPFCapability.activateOSPF();
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}

	}

	@Override
	public void deactivateOSPF(String resourceId) throws CapabilityException {
		try {
			INetOSPFCapability iNetOSPFCapability = (INetOSPFCapability) getCapability(resourceId, NetOSPFCapability.CAPABILITY_NAME);
			iNetOSPFCapability.deactivateOSPF();
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}

	}
}