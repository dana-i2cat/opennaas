package org.opennaas.router.capability.gretunnel;

import net.i2cat.mantychore.model.GRETunnelService;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.command.Response;

/**
 * @author Jordi Puig
 */
public interface IGRETunnelService {

	/**
	 * Create a GRETunnel on the router
	 * 
	 * @return
	 * @throws CapabilityException
	 */
	public Response createGRETunnel(GRETunnelService greTunnelService) throws CapabilityException;

	/**
	 * Delete GRETunnel.
	 * 
	 * @return
	 * @throws CapabilityException
	 */
	public Response deleteGRETunnel(GRETunnelService greTunnelService) throws CapabilityException;

	/**
	 * Get the GRETunnel configuration.
	 * 
	 * @param ospfService
	 * @return
	 * @throws CapabilityException
	 */
	public Response getGRETunnelConfiguration(GRETunnelService greTunnelService) throws CapabilityException;

	/**
	 * Show the GRETunnel configuration.
	 * 
	 * @return GRETunnelService
	 * @throws CapabilityException
	 */
	public GRETunnelService showGRETunnelConfiguration() throws CapabilityException;
}
