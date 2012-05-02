package org.opennaas.extensions.router.capability.gretunnel;

import java.util.List;

import org.opennaas.extensions.router.model.GRETunnelService;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.command.Response;

/**
 * @author Jordi Puig
 */
public interface IGRETunnelService extends ICapability {

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
	 * Show the GRETunnel configuration.
	 * 
	 * @return GRETunnelService
	 * @throws CapabilityException
	 */
	public List<GRETunnelService> showGRETunnelConfiguration() throws CapabilityException;
}
