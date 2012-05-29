package org.opennaas.extensions.router.capability.gretunnel;

import java.util.List;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.router.model.GRETunnelService;

/**
 * @author Jordi Puig
 */
public interface IGRETunnelCapability extends ICapability {

	/**
	 * Create a GRETunnel on the router
	 * 
	 * @throws CapabilityException
	 */
	public void createGRETunnel(GRETunnelService greTunnelService) throws CapabilityException;

	/**
	 * Delete GRETunnel.
	 * 
	 * @throws CapabilityException
	 */
	public void deleteGRETunnel(GRETunnelService greTunnelService) throws CapabilityException;

	/**
	 * Show the GRETunnel configuration.
	 * 
	 * @return GRETunnelService
	 * @throws CapabilityException
	 */
	public List<GRETunnelService> showGRETunnelConfiguration() throws CapabilityException;
}
