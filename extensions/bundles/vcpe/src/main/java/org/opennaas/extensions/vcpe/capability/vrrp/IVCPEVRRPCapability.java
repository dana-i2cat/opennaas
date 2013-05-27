package org.opennaas.extensions.vcpe.capability.vrrp;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

@Path("/")
public interface IVCPEVRRPCapability extends ICapability {

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
