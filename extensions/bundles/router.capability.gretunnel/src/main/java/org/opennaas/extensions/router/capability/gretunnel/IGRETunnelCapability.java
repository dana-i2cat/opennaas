package org.opennaas.extensions.router.capability.gretunnel;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.router.model.GRETunnelService;

/**
 * @author Jordi Puig
 */
@Path("/")
public interface IGRETunnelCapability extends ICapability {

	/**
	 * Create a GRETunnel on the router
	 * 
	 * @throws CapabilityException
	 */
	@POST
	@Path("/createGRETunnel")
	@Consumes(MediaType.APPLICATION_XML)
	public void createGRETunnel(GRETunnelService greTunnelService) throws CapabilityException;

	/**
	 * Delete GRETunnel.
	 * 
	 * @throws CapabilityException
	 */
	@POST
	@Path("/deleteGRETunnel")
	@Consumes(MediaType.APPLICATION_XML)
	public void deleteGRETunnel(GRETunnelService greTunnelService) throws CapabilityException;

	/**
	 * Show the GRETunnel configuration.
	 * 
	 * @return GRETunnelService
	 * @throws CapabilityException
	 */
	@POST
	@Path("/showGRETunnelConfiguration")
	@Produces(MediaType.APPLICATION_XML)
	public List<GRETunnelService> showGRETunnelConfiguration() throws CapabilityException;
}
