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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.ModelElementNotFoundException;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.router.capabilities.api.model.chassis.InterfacesNamesList;
import org.opennaas.extensions.router.capabilities.api.model.ip.IPAddresses;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.LogicalPort;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
@Path("/")
public interface IIPCapability extends ICapability {

	/**
	 * Returns a list of interfaces names
	 * 
	 * @return available interfaces names.
	 */
	@GET
	@Path("/interfaces")
	@Produces(MediaType.APPLICATION_XML)
	public InterfacesNamesList getInterfacesNames() throws CapabilityException;

	/**
	 * 
	 * @param interfaceName
	 *            identifying the interface this operation applies to
	 * @return IP addresses of given interface
	 * @throws ModelElementNotFoundException
	 *             if given interface is not in available
	 * @since 0.26
	 */
	@GET
	@Path("/interfaces/addresses")
	@Produces(MediaType.APPLICATION_XML)
	public IPAddresses getIPs(@QueryParam("interface") String interfaceName) throws ModelElementNotFoundException;

	/**
	 * Returns description for given interface
	 * 
	 * @param interfaceName
	 *            identifying the interface this operation applies to
	 * @return description for given interface.
	 * @throws ModelElementNotFoundException
	 *             if given interface is not in available
	 * @since 0.26
	 */
	@GET
	@Path("/interfaces/description")
	@Produces(MediaType.APPLICATION_XML)
	public String getDescription(@QueryParam("interface") String interfaceName) throws ModelElementNotFoundException;

	/**
	 * Sets given ipv4Address to the interface identified by given interfaceName.
	 * 
	 * This operation removes any other ipv4 address in given interface.
	 * 
	 * @param interfaceName
	 *            identifying the interface this operation applies to
	 * @param ipv4Address
	 *            to set
	 * @throws CapabilityException
	 * @since 0.26
	 */
	@POST
	@Path("/interfaces/addresses/ipv4")
	@Consumes(MediaType.APPLICATION_XML)
	public void setIPv4(@QueryParam("interface") String interfaceName, String ipv4Address) throws CapabilityException;

	/**
	 * Sets given ipv6Address to the interface identified by given interfaceName.
	 * 
	 * This operation removes any other ipv6 address in given interface.
	 * 
	 * @param interfaceName
	 *            identifying the interface this operation applies to
	 * @param ipv6Address
	 *            to set
	 * @throws CapabilityException
	 * @since 0.26
	 */
	@POST
	@Path("/interfaces/addresses/ipv6")
	@Consumes(MediaType.APPLICATION_XML)
	public void setIPv6(@QueryParam("interface") String interfaceName, String ipv6Address) throws CapabilityException;

	/**
	 * Sets given ipAddress to the interface identified by given interfaceName.
	 * 
	 * This operation removes any other ip address in given interface.
	 * 
	 * @param interfaceName
	 *            identifying the interface this operation applies to
	 * @param ipAddress
	 *            to set
	 * @throws CapabilityException
	 * @since 0.26
	 */
	@POST
	@Path("/interfaces/addresses/ip")
	@Consumes(MediaType.APPLICATION_XML)
	public void setIP(@QueryParam("interface") String interfaceName, String ipAddress) throws CapabilityException;

	/**
	 * Set the description for the given interface
	 * 
	 * @param interfaceName
	 *            identifying the interface this operation applies to
	 * @param description
	 * @throws CapabilityException
	 * @since 0.26
	 */
	@POST
	@Path("/interfaces/description")
	@Consumes(MediaType.APPLICATION_XML)
	public void setInterfaceDescription(@QueryParam("interface") String interfaceName, String description) throws CapabilityException;

	/**
	 * Adds given ipv4Address to the interface identified by given interfaceName.
	 * 
	 * This operation has no effect to any other ip address in given interface.
	 * 
	 * @param interfaceName
	 *            identifying the interface this operation applies to
	 * @param ipv4Address
	 *            to set
	 * @throws CapabilityException
	 * @since 0.26
	 */
	@PUT
	@Path("/interfaces/addresses/ipv4")
	@Consumes(MediaType.APPLICATION_XML)
	public void addIPv4(@QueryParam("interface") String interfaceName, String ipv4Address)
			throws CapabilityException;

	/**
	 * Adds given ipv6Address to the interface identified by given interfaceName.
	 * 
	 * This operation has no effect to any other ip address in given interface.
	 * 
	 * @param interfaceName
	 *            identifying the interface this operation applies to
	 * @param ipv6Address
	 *            to set
	 * @throws CapabilityException
	 * @since 0.26
	 */
	@PUT
	@Path("/interfaces/addresses/ipv6")
	@Consumes(MediaType.APPLICATION_XML)
	public void addIPv6(@QueryParam("interface") String interfaceName, String ipv6Address)
			throws CapabilityException;

	/**
	 * Adds given ipAddress to the interface identified by given interfaceName.
	 * 
	 * This operation has no effect to any other ip address in given interface.
	 * 
	 * @param interfaceName
	 *            identifying the interface this operation applies to
	 * @param ipAddress
	 *            to set
	 * @throws CapabilityException
	 * @since 0.26
	 */
	@PUT
	@Path("/interfaces/addresses/ip")
	@Consumes(MediaType.APPLICATION_XML)
	public void addIP(@QueryParam("interface") String interfaceName, String ipAddress)
			throws CapabilityException;

	/**
	 * Removes given ipv4Address from the interface identified by given interfaceName.
	 * 
	 * This operation has no effect to any other ip address in given interface.
	 * 
	 * @param interfaceName
	 *            identifying the interface this operation applies to
	 * @param ipv4Address
	 *            to remove
	 * @throws CapabilityException
	 * @since 0.26
	 */
	@DELETE
	@Path("/interfaces/addresses/ipv4")
	@Consumes(MediaType.APPLICATION_XML)
	public void removeIPv4(@QueryParam("interface") String interfaceName, @QueryParam("ip") String ipv4Address)
			throws CapabilityException;

	/**
	 * Removes given ipv6Address from the interface identified by given interfaceName.
	 * 
	 * This operation has no effect to any other ip address in given interface.
	 * 
	 * @param interfaceName
	 *            identifying the interface this operation applies to
	 * @param ipv6Address
	 *            to remove
	 * @throws CapabilityException
	 * @since 0.26
	 */
	@DELETE
	@Path("/interfaces/addresses/ipv6")
	@Consumes(MediaType.APPLICATION_XML)
	public void removeIPv6(@QueryParam("interface") String interfaceName, @QueryParam("ip") String ipv6Address)
			throws CapabilityException;

	/**
	 * Removes given ipAddress from the interface identified by given interfaceName.
	 * 
	 * This operation has no effect to any other ip address in given interface.
	 * 
	 * @param interfaceName
	 *            identifying the interface this operation applies to
	 * @param ipAddress
	 *            to remove
	 * @throws CapabilityException
	 * @since 0.26
	 */
	@DELETE
	@Path("/interfaces/addresses/ip")
	@Consumes(MediaType.APPLICATION_XML)
	public void removeIP(@QueryParam("interface") String interfaceName, @QueryParam("ip") String ipAddress)
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

	public void setInterfaceDescription(LogicalPort iface) throws CapabilityException;

}
