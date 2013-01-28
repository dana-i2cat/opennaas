package org.opennaas.extensions.vcpe.capability.ip;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

@Path("/")
public interface IVCPEIPCapability extends ICapability {

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

}
