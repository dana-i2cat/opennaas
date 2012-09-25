package org.opennaas.extensions.network.capability.basic.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.network.capability.basic.INetworkBasicCapability;
import org.opennaas.extensions.network.capability.basic.ws.wrapper.AddResourceRequest;
import org.opennaas.extensions.network.capability.basic.ws.wrapper.L2AttachRequest;
import org.opennaas.extensions.network.capability.basic.ws.wrapper.L2DettachRequest;
import org.opennaas.extensions.network.capability.basic.ws.wrapper.RemoveResourceRequest;

@Path("/")
public interface INetworkBasicCapabilityService extends INetworkBasicCapability {

	/**
	 * Adds resource to network topology.
	 * 
	 * @param request
	 *            the id of the resource
	 * 
	 * @return NetworkModel updated with added resource
	 * @throws CapabilityException
	 *             if an error occurred.
	 */
	@Path("/addResource")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response addResource(AddResourceRequest request) throws CapabilityException;

	/**
	 * Removes a resource from network topology.
	 * 
	 * @param request
	 *            the id of the resource
	 * @return NetworkModel updated with added resource
	 * @throws CapabilityException
	 *             if an error occurred.
	 */
	@Path("/removeResource")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response removeResource(RemoveResourceRequest request) throws CapabilityException;

	/**
	 * Creates a L2 connection between given interfaces in network topology.
	 * 
	 * This method does not interact with real interfaces, connection is only created in topology, despite having real connectivity or not.
	 * 
	 * @param request
	 *            , link
	 * @return NetworkConnection linking given interfaces.
	 * @throws CapabilityException
	 *             if an error occurred.
	 */
	@Path("/l2attach")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response l2attach(L2AttachRequest request) throws CapabilityException;

	/**
	 * Removes a L2 connection between given interfaces in network topology.
	 * 
	 * This method does not interact with real interfaces, connection is only removed from topology, despite having real connectivity or not.
	 * 
	 * @param request
	 *            , link
	 * @throws CapabilityException
	 *             if an error occurred.
	 */
	@Path("/l2detach")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response l2detach(L2DettachRequest request) throws CapabilityException;

}
