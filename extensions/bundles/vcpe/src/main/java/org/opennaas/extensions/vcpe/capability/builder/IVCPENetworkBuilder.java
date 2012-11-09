package org.opennaas.extensions.vcpe.capability.builder;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

@Path("/")
public interface IVCPENetworkBuilder extends ICapability {

	/**
	 * Build the VCPENetworkModel from the desired scenario
	 * 
	 * @param desiredScenario
	 * @return VCPENetworkModel
	 * @throws CapabilityException
	 */
	@Path("/build")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public VCPENetworkModel buildVCPENetwork(VCPENetworkModel desiredScenario) throws CapabilityException;

	/**
	 * Destroy the VCPENetworkModel
	 * 
	 * @throws CapabilityException
	 */
	@Path("/destroy")
	@POST
	public void destroyVCPENetwork() throws CapabilityException;

	/**
	 * Update the Ip's of the VCPENetworkModel
	 * 
	 * @param vcpeNetworkModel
	 * @throws CapabilityException
	 */
	@Path("/updateIps")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void updateIps(VCPENetworkModel vcpeNetworkModel) throws CapabilityException;

	/**
	 * Update the VRRP virtual ip address
	 * 
	 * @param model
	 * @throws CapabilityException
	 */
	@Path("/updateVRRPIp")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void updateVRRPIp(VCPENetworkModel model) throws CapabilityException;

	/**
	 * Change the priority VRRP
	 * 
	 * @param model
	 * @return
	 * @throws CapabilityException
	 */
	@Path("/changeVRRPPriority")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public VCPENetworkModel changeVRRPPriority(VCPENetworkModel model) throws CapabilityException;

}
