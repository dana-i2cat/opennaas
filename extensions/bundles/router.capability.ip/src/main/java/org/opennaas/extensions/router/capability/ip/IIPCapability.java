package org.opennaas.extensions.router.capability.ip;

/*
 * #%L
 * OpenNaaS :: Router :: IP Capability
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.ModelElementNotFoundException;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.router.capability.ip.api.IPAddresses;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.wrappers.InterfacesNamesList;
import org.opennaas.extensions.router.model.wrappers.SetIpAddressRequest;

@Path("/")
public interface IIPCapability extends ICapability {

	/*
	 * Interfaces
	 */
	/**
	 * Returns a list of interfaces names
	 * 
	 * @return
	 */
	@GET
	@Path("/getInterfaces")
	@Produces(MediaType.APPLICATION_XML)
	public InterfacesNamesList getInterfacesNames() throws CapabilityException;

	/**
	 * 
	 * @param interfaceName
	 * @return IP addresses of given interface
	 * @throws ModelElementNotFoundException
	 *             if given interface is not in available
	 */
	@GET
	@Path("/getIPs/{interfaceName}")
	@Produces(MediaType.APPLICATION_XML)
	public IPAddresses getIPs(@QueryParam("interfaceName") String interfaceName) throws ModelElementNotFoundException;

	/**
	 * 
	 * @param interfaceName
	 * @return
	 * @throws ModelElementNotFoundException
	 *             if given interface is not in available
	 */
	@GET
	@Path("/getDescription/{interfaceName}")
	@Produces(MediaType.APPLICATION_XML)
	public String getDescription(@QueryParam("interfaceName") String interfaceName) throws ModelElementNotFoundException;

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
