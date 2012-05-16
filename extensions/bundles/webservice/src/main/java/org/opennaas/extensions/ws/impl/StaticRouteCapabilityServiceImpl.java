package org.opennaas.extensions.ws.impl;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.router.capability.staticroute.IStaticRouteCapability;
import org.opennaas.extensions.ws.services.IStaticRouteCapabilityService;

/**
 * 
 * @author Adrian Rosello
 * 
 */

@WebService(portName = "StaticRouteCapabilityPort", serviceName = "StaticRouteCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
public class StaticRouteCapabilityServiceImpl extends GenericCapabilityService implements IStaticRouteCapabilityService {

	Log	log	= LogFactory.getLog(StaticRouteCapabilityServiceImpl.class);

	@Override
	public void createStaticRoute(String resourceId, String netIdIpAdress, String maskIpAdress, String nextHopIpAddress) throws CapabilityException {
		try {
			log.info("Start of createStaticRoute call");
			IStaticRouteCapability iStaticRouteCapability = (IStaticRouteCapability) getCapability(resourceId, IStaticRouteCapability.class);
			iStaticRouteCapability.createStaticRoute(netIdIpAdress, maskIpAdress, nextHopIpAddress);
			log.info("End of createStaticRoute call");

		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}
}