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
public interface IVCPENetworkBuilderCapability extends ICapability {

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

}
