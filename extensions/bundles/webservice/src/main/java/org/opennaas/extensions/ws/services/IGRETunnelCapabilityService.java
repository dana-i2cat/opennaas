package org.opennaas.extensions.ws.services;

import java.util.List;

import javax.jws.WebService;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.router.model.GRETunnelService;

/**
 * @author Jordi Puig
 */

@WebService(portName = "GRETunnelCapabilityPort", serviceName = "GRETunnelCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
public interface IGRETunnelCapabilityService {

	/**
	 * Create a GRETunnel on the router
	 * 
	 * @param resourceId
	 * @throws CapabilityException
	 */
	public void createGRETunnel(String resourceId, GRETunnelService greTunnelService) throws CapabilityException;

	/**
	 * Delete GRETunnel.
	 * 
	 * @param resourceId
	 * @throws CapabilityException
	 */
	public void deleteGRETunnel(String resourceId, GRETunnelService greTunnelService) throws CapabilityException;

	/**
	 * Show the GRETunnel configuration.
	 * 
	 * @param resourceId
	 * @return GRETunnelService
	 * @throws CapabilityException
	 */
	public List<GRETunnelService> showGRETunnelConfiguration(String resourceId) throws CapabilityException;

}