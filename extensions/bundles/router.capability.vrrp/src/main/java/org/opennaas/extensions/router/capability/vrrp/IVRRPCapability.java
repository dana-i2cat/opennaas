package org.opennaas.extensions.router.capability.vrrp;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.router.model.VRRPProtocolEndpoint;

/**
 * @author Julio Carlos Barrera
 */
@Path("/")
public interface IVRRPCapability extends ICapability {

	/**
	 * Configure VRRP
	 * 
	 * @throws CapabilityException
	 */
	@Path("/configureVRRP")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void configureVRRP(VRRPProtocolEndpoint vrrpProtocolEndpoint) throws CapabilityException;

	/**
	 * Unconfigure VRRP
	 * 
	 * @throws CapabilityException
	 */
	@Path("/unconfigureVRRP")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void unconfigureVRRP(VRRPProtocolEndpoint vrrpProtocolEndpoint) throws CapabilityException;

	/**
	 * Update VRRP Virtual IP Address
	 * 
	 * @throws CapabilityException
	 */
	@Path("/updateVRRPVirtualIPAddress")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void updateVRRPVirtualIPAddress(VRRPProtocolEndpoint vrrpProtocolEndpoint) throws CapabilityException;

	/**
	 * Update VRRP Priority
	 * 
	 * @throws CapabilityException
	 */
	/*
	 * @Path("/updateVRRPPriority")
	 * 
	 * @POST
	 * 
	 * @Consumes(MediaType.APPLICATION_XML)
	 */
	public void updateVRRPPriority(VRRPProtocolEndpoint vrrpProtocolEndpoint) throws CapabilityException;
}
