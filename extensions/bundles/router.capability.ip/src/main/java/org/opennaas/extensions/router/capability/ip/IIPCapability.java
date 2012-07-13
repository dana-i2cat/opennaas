package org.opennaas.extensions.router.capability.ip;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.wrappers.SetIpAddressRequest;

@Path("/")
public interface IIPCapability extends ICapability {

	@POST
	@Path("/setIPv4")
	@Consumes(MediaType.APPLICATION_XML)
	public void setIPv4(SetIpAddressRequest request)
			throws CapabilityException;

	/**
	 * Set the given ip to the logical device
	 * 
	 * @param params
	 * @throws CapabilityException
	 */
	// cannot have a POST method with two params
	// only a single object can go in a POST body (using setIPv4(SetIpAddressRequest) instead) :)
	public void setIPv4(LogicalDevice logicalDevice, IPProtocolEndpoint ip)
			throws CapabilityException;

	/**
	 * Set the description for the given interface
	 * 
	 * @param iface
	 * @param ip
	 * @throws CapabilityException
	 */
	@POST
	@Path("/setInterfaceDescription")
	@Consumes(MediaType.APPLICATION_XML)
	public void setInterfaceDescription(LogicalPort iface) throws CapabilityException;

}
