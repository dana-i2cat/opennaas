package org.opennaas.extensions.router.capability.ospf;

/*
 * #%L
 * OpenNaaS :: Router :: OSPF capability
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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.router.capabilities.api.model.ospf.OSPFServiceWrapper;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.OSPFArea;
import org.opennaas.extensions.router.model.OSPFAreaConfiguration;
import org.opennaas.extensions.router.model.OSPFProtocolEndpoint;
import org.opennaas.extensions.router.model.OSPFService;
import org.opennaas.extensions.router.model.wrappers.AddInterfacesInOSPFAreaRequest;
import org.opennaas.extensions.router.model.wrappers.RemoveInterfacesInOSPFAreaRequest;

/**
 * @author Jordi Puig
 * @author Isart Canyameres
 */
@Path("/")
public interface IOSPFCapability extends ICapability {

	/**
	 * Enable OSPF on the router.
	 * 
	 * @throws CapabilityException
	 */
	@Path("/activateOSPF")
	@POST
	public void activateOSPF() throws CapabilityException;

	/**
	 * Disable OSPF on the router.
	 * 
	 * @throws CapabilityException
	 */
	@Path("/deactivateOSPF")
	@POST
	public void deactivateOSPF() throws CapabilityException;

	public void configureOSPF(OSPFService ospfService) throws CapabilityException;

	/**
	 * Configure OSPF service.
	 * 
	 * This configuration applies to the OSPF Service itself, but does not affect areas nor interfaces.
	 * 
	 * @param ospfService
	 * @throws CapabilityException
	 */
	@Path("/")
	@Consumes(MediaType.APPLICATION_XML)
	@POST
	public void configureOSPF(OSPFServiceWrapper ospfServiceWrapper) throws CapabilityException;

	/**
	 * Removes all OSPF configuration.
	 * 
	 * @param ospfService
	 * @throws CapabilityException
	 */
	@Path("/clearOSPFconfiguration")
	@Consumes(MediaType.APPLICATION_XML)
	@POST
	public void clearOSPFconfiguration(OSPFService ospfService) throws CapabilityException;

	/**
	 * Configures an OSPF area.
	 * 
	 * @param ospfAreaConfiguration
	 * @throws CapabilityException
	 */
	@Path("/configureOSPFArea")
	@Consumes(MediaType.APPLICATION_XML)
	@POST
	public void configureOSPFArea(OSPFAreaConfiguration ospfAreaConfiguration) throws CapabilityException;

	/**
	 * Remove an OSPF area.
	 * 
	 * @param ospfAreaConfiguration
	 * @throws CapabilityException
	 */
	@Path("/removeOSPFArea")
	@Consumes(MediaType.APPLICATION_XML)
	@POST
	public void removeOSPFArea(OSPFAreaConfiguration ospfAreaConfiguration) throws CapabilityException;

	/**
	 * Adds given interfaces to given OSPF area
	 * 
	 * @param addInterfacesOSPFRequest
	 * @throws CapabilityException
	 */
	@Path("/addInterfacesInOSPFArea")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void addInterfacesInOSPFArea(AddInterfacesInOSPFAreaRequest addInterfacesInOSPFAreaRequest)
			throws CapabilityException;

	/**
	 * Adds given interfaces to given OSPF area
	 * 
	 * @param interfaces
	 * @param ospfArea
	 * @throws CapabilityException
	 */
	public void addInterfacesInOSPFArea(List<LogicalPort> interfaces, OSPFArea ospfArea)
			throws CapabilityException;

	/**
	 * Remove given interfaces from given OSPF area
	 * 
	 * @param removeInterfacesOSPFRequest
	 * @throws CapabilityException
	 */
	@Path("/removeInterfacesInOSPFArea")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void removeInterfacesInOSPFArea(RemoveInterfacesInOSPFAreaRequest removeInterfacesInOSPFAreaRequest)
			throws CapabilityException;

	/**
	 * Remove given interfaces from given OSPF area
	 * 
	 * @param interfaces
	 * @param ospfArea
	 * @throws CapabilityException
	 */
	public void removeInterfacesInOSPFArea(List<LogicalPort> interfaces, OSPFArea ospfArea)
			throws CapabilityException;

	/**
	 * Enable OSPF in given interfaces, if they are already configured.
	 * 
	 * @param interfaces
	 * @throws CapabilityException
	 */
	@Path("/enableOSPFInterfaces")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void enableOSPFInterfaces(List<OSPFProtocolEndpoint> interfaces) throws CapabilityException;

	/**
	 * Disable OSPF in given interfaces, if they are already configured.
	 * 
	 * @param interfaces
	 * @throws CapabilityException
	 */
	@Path("/disableOSPFInterfaces")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void disableOSPFInterfaces(List<OSPFProtocolEndpoint> interfaces) throws CapabilityException;

	/**
	 * Returns OSPF full configuration from the router
	 * 
	 * This includes service, areas, and interface status.
	 * 
	 * @throws CapabilityException
	 */
	@Path("/getOSPFConfiguration")
	@POST
	public void getOSPFConfiguration() throws CapabilityException;

	/**
	 * Returns OSPF full configuration from the model
	 * 
	 * This includes service, areas, and interface status.
	 * 
	 * @return ospfService
	 * @throws CapabilityException
	 */
	// TODO: export this method using rest too
	public OSPFService showOSPFConfiguration() throws CapabilityException;

	@Path("/")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public OSPFServiceWrapper readOSPFConfiguration() throws CapabilityException;

}
