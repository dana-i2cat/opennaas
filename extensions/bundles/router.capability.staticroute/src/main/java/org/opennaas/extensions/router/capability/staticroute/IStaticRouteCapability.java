package org.opennaas.extensions.router.capability.staticroute;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;

/**
 * @author Jordi Puig
 */
@Path("/staticRoute")
public interface IStaticRouteCapability extends ICapability {

	/**
	 * Create a static route in the router
	 * 
	 * @param netIdIpAdress
	 * @param maskIpAdress
	 * @param nextHopIpAddress
	 * @throws CapabilityException
	 */
	@GET
	@Path("/create")
	public void createStaticRoute(@QueryParam("netIdIpAdress") String netIdIpAdress, @QueryParam("maskIpAdress") String maskIpAdress,
			@QueryParam("nextHopIpAddress") String nextHopIpAddress) throws CapabilityException;

}
