package org.opennaas.extensions.router.capability.vlanbridge;

/*
 * #%L
 * OpenNaaS :: Router :: VLAN bridge Capability
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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.ModelElementNotFoundException;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.router.capabilities.api.model.vlanbridge.BridgeDomain;
import org.opennaas.extensions.router.capabilities.api.model.vlanbridge.BridgeDomains;
import org.opennaas.extensions.router.capabilities.api.model.vlanbridge.InterfaceVLANOptions;

/**
 * This capability offers VLAN bridging by defining VLAN BridgeDomains. These domains represent a switching domain in accordance with 802.1Q standard.
 * For an explanation on these concepts, see http://www.cse.wustl.edu/~jain/cis788-97/ftp/virtual_lans/index.html#WhatVLAN
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
@Path("/")
public interface IVLANBridgeCapability extends ICapability {

	/**
	 * Retrieves existing BridgeDomain names
	 * 
	 * @return BridgeDomains with a list containing the name of existing BridgeDomains
	 */
	@Path("/")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public BridgeDomains getBridgeDomains();

	/**
	 * Retrieves existing BridgeDomain with given domainName
	 * 
	 * @param domainName
	 *            The name of the BridgeDomain to look for.
	 * @return BridgeDomain with given domainName
	 * @throws ModelElementNotFoundException
	 *             if there is no BridgeDomain with given domainName in the system.
	 * @throws CapabilityException
	 */
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public BridgeDomain getBridgeDomain(@PathParam("id") String domainName) throws ModelElementNotFoundException, CapabilityException;

	/**
	 * Prepares and queues an Action that, if executed, configures given BridgeDomain in the resource having this Capability.
	 * 
	 * @param bridgeDomain
	 * @throws CapabilityException
	 *             if failed to prepare action for creating given domain
	 */
	@Path("/")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void createBridgeDomain(BridgeDomain bridgeDomain) throws CapabilityException;

	/**
	 * Prepares and queues an Action that, if executed, updates the BridgeDomain with given domainName with the configuration in given BridgeDomain
	 * object.
	 * 
	 * @param bridgeDomain
	 * @throws ModelElementNotFoundException
	 *             if there is no BridgeDomain with given domainName in the system.
	 * @throws CapabilityException
	 *             if failed to prepare action for updating desired domain
	 */
	@Path("/{id}")
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public void updateBridgeDomain(@PathParam("id") String domainName, BridgeDomain bridgeDomain)
			throws ModelElementNotFoundException, CapabilityException;

	/**
	 * Prepares and queues an Action that, if executed, deletes a BridgeDomain with given ndomainName in the resource having this Capability.
	 * 
	 * @param bridgeDomain
	 * @throws ModelElementNotFoundException
	 *             if there is no BridgeDomain with given domainName in the system.
	 * @throws CapabilityException
	 *             if failed to prepare action for deleting given domain
	 */
	@Path("/{id}")
	@DELETE
	public void deleteBridgeDomain(@PathParam("id") String domainName)
			throws ModelElementNotFoundException, CapabilityException;

	/**
	 * Retrieves InterfaceVLANOptions for interface with given ifaceName.
	 * 
	 * @param ifaceName
	 *            of the interface to look for
	 * @return InterfaceVLANOptions for interface with given ifaceName
	 * @throws ModelElementNotFoundException
	 *             if there is no interface with given ifaceName in the system.
	 */
	@Path("/vlanoptions")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public InterfaceVLANOptions getInterfaceVLANOptions(@QueryParam("iface") String ifaceName)
			throws ModelElementNotFoundException;

	/**
	 * Prepares and queues an Action that, if executed, sets given InterfaceVLANOptions for interface with given ifaceName.
	 * 
	 * @param ifaceName
	 *            of the interface to look for
	 * @param vlanOptions
	 *            to set
	 * @throws ModelElementNotFoundException
	 *             if there is no interface with given ifaceName in the system.
	 * @throws CapabilityException
	 *             if failed to prepare action for setting given InterfaceVLANOptions in desired interface.
	 */
	@Path("/vlanoptions")
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public void setInterfaceVLANOptions(@QueryParam("iface") String ifaceName, InterfaceVLANOptions vlanOptions)
			throws ModelElementNotFoundException, CapabilityException;

}
