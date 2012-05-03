package org.opennaas.extensions.router.capability.staticroute;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;

/**
 * @author Jordi Puig
 */
public interface IStaticRouteCapability extends ICapability {


	/**
	 * Create a static route in the router
	 * @param netIdIpAdress
	 * @param maskIpAdress
	 * @param nextHopIpAddress   
	 * @throws CapabilityException
	 */
	public void createStaticRoute(String netIdIpAdress, String maskIpAdress, String nextHopIpAddress) throws CapabilityException;

}
