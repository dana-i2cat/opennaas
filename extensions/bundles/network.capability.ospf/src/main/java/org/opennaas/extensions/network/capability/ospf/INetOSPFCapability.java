package org.opennaas.extensions.network.capability.ospf;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;

@Path("/")
public interface INetOSPFCapability extends ICapability {

	/**
	 * Enable OSPF on the network.
	 * 
	 * 1) Configures OSPF in all routers of the network. </b> 2) Configures a backbone area in them. </b> 3) Add all interfaces on this routers to
	 * this area </b> 4) Enables OSPF in all routers </b>
	 * 
	 * @throws CapabilityException
	 */
	@POST
	@Path("/activate")
	public void activateOSPF() throws CapabilityException;

	/**
	 * Disable OSPF on the network.
	 * 
	 * @return
	 * @throws CapabilityException
	 */
	@POST
	@Path("/deactivate")
	public void deactivateOSPF() throws CapabilityException;
}