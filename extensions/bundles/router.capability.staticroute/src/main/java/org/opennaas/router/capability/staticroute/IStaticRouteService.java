package org.opennaas.router.capability.staticroute;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.command.Response;

/**
 * @author Jordi Puig
 */
public interface IStaticRouteService {

	/**
	 * Create a static route in the router
	 * 
	 * @return Response
	 * @throws CapabilityException
	 */
	public Response create(String netIdIpAdress, String maskIpAdress, String nextHopIpAddress) throws CapabilityException;

}
