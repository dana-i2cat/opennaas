package org.opennaas.extensions.router.capability.bgp;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.router.model.ComputerSystem;

@Path("/")
public interface IBGPCapability extends ICapability {

	@POST
	@Path("/configureBGP")
	@Consumes(MediaType.APPLICATION_XML)
	public void configureBGP(ComputerSystem systemWithBGPAndPolicies) throws CapabilityException;

	@POST
	@Path("/unconfigureBGP")
	@Consumes(MediaType.APPLICATION_XML)
	public void unconfigureBGP(ComputerSystem systemWithBGPAndPolicies) throws CapabilityException;

}
