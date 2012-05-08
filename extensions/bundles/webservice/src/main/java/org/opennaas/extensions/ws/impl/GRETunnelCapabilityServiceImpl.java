package org.opennaas.extensions.ws.impl;

import java.util.List;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.router.capability.gretunnel.GRETunnelCapability;
import org.opennaas.extensions.router.capability.gretunnel.IGRETunnelCapability;
import org.opennaas.extensions.router.model.GRETunnelService;
import org.opennaas.extensions.ws.services.IGRETunnelCapabilityService;

/**
 * @author Jordi Puig
 */
@WebService(portName = "GRETunnelCapabilityPort", serviceName = "GRETunnelCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
public class GRETunnelCapabilityServiceImpl extends GenericCapabilityServiceImpl implements IGRETunnelCapabilityService {

	Log	log	= LogFactory.getLog(GRETunnelCapabilityServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IGRETunnelCapabilityService#createGRETunnel(java.lang.String,
	 * org.opennaas.extensions.router.model.GRETunnelService)
	 */
	@Override
	public void createGRETunnel(String resourceId, GRETunnelService greTunnelService) throws CapabilityException {
		try {
			IGRETunnelCapability iOSPFCapability = (IGRETunnelCapability) getCapability(resourceId, GRETunnelCapability.CAPABILITY_TYPE);
			iOSPFCapability.createGRETunnel(greTunnelService);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IGRETunnelCapabilityService#deleteGRETunnel(java.lang.String,
	 * org.opennaas.extensions.router.model.GRETunnelService)
	 */
	@Override
	public void deleteGRETunnel(String resourceId, GRETunnelService greTunnelService) throws CapabilityException {
		try {
			IGRETunnelCapability iOSPFCapability = (IGRETunnelCapability) getCapability(resourceId, GRETunnelCapability.CAPABILITY_TYPE);
			iOSPFCapability.deleteGRETunnel(greTunnelService);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IGRETunnelCapabilityService#showGRETunnelConfiguration(java.lang.String)
	 */
	@Override
	public List<GRETunnelService> showGRETunnelConfiguration(String resourceId) throws CapabilityException {
		try {
			IGRETunnelCapability iOSPFCapability = (IGRETunnelCapability) getCapability(resourceId, GRETunnelCapability.CAPABILITY_TYPE);
			return iOSPFCapability.showGRETunnelConfiguration();
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

}
