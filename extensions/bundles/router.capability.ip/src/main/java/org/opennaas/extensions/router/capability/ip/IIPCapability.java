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

	@POST
	@Path("/setIPv6")
	@Consumes(MediaType.APPLICATION_XML)
	public void setIPv6(SetIpAddressRequest request)
			throws CapabilityException;

	@POST
	@Path("/setIP")
	@Consumes(MediaType.APPLICATION_XML)
	public void setIP(SetIpAddressRequest request)
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

	@POST
	@Path("/addIPv4")
	@Consumes(MediaType.APPLICATION_XML)
	public void addIPv4(SetIpAddressRequest request)
			throws CapabilityException;

	@POST
	@Path("/addIPv6")
	@Consumes(MediaType.APPLICATION_XML)
	public void addIPv6(SetIpAddressRequest request)
			throws CapabilityException;

	@POST
	@Path("/addIP")
	@Consumes(MediaType.APPLICATION_XML)
	public void addIP(SetIpAddressRequest request)
			throws CapabilityException;

	@POST
	@Path("/removeIPv4")
	@Consumes(MediaType.APPLICATION_XML)
	public void removeIPv4(SetIpAddressRequest request)
			throws CapabilityException;

	@POST
	@Path("/removeIPv6")
	@Consumes(MediaType.APPLICATION_XML)
	public void removeIPv6(SetIpAddressRequest request)
			throws CapabilityException;

	@POST
	@Path("/removeIP")
	@Consumes(MediaType.APPLICATION_XML)
	public void removeIP(SetIpAddressRequest request)
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

	public void setIPv6(LogicalDevice logicalDevice, IPProtocolEndpoint ip) throws CapabilityException;

	public void setIP(LogicalDevice logicalDevice, IPProtocolEndpoint ip) throws CapabilityException;

	public void setIP(LogicalDevice logicalDevice, String ipAddress) throws CapabilityException;

	public void addIPv4(LogicalDevice logicalDevice, IPProtocolEndpoint ip)
			throws CapabilityException;

	public void addIPv6(LogicalDevice logicalDevice, IPProtocolEndpoint ip) throws CapabilityException;

	public void addIP(LogicalDevice logicalDevice, IPProtocolEndpoint ip) throws CapabilityException;

	public void addIP(LogicalDevice logicalDevice, String ipAddress) throws CapabilityException;

	public void removeIPv4(LogicalDevice logicalDevice, IPProtocolEndpoint ip)
			throws CapabilityException;

	public void removeIPv6(LogicalDevice logicalDevice, IPProtocolEndpoint ip) throws CapabilityException;

	public void removeIP(LogicalDevice logicalDevice, IPProtocolEndpoint ip) throws CapabilityException;

	public void removeIP(LogicalDevice logicalDevice, String ipAddress) throws CapabilityException;

}
