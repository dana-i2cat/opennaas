package org.opennaas.extensions.router.capability.ospfv3;

/*
 * #%L
 * OpenNaaS :: Router :: OSPFv3 Capability
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
import org.opennaas.extensions.router.capabilities.api.model.ospf.AddInterfacesInOSPFAreaRequest;
import org.opennaas.extensions.router.capabilities.api.model.ospf.OSPFServiceWrapper;
import org.opennaas.extensions.router.capabilities.api.model.ospf.RemoveInterfacesInOSPFAreaRequest;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.OSPFArea;
import org.opennaas.extensions.router.model.OSPFAreaConfiguration;
import org.opennaas.extensions.router.model.OSPFProtocolEndpoint;
import org.opennaas.extensions.router.model.OSPFService;

/**
 * @author Jordi Puig
 * @author Isart Canyameres
 */
@Path("/")
public interface IOSPFv3Capability extends ICapability {

	/**
	 * Enable OSPFv3 on the router.
	 * 
	 * @throws CapabilityException
	 */
	@Path("/activateOSPFv3")
	@POST
	public void activateOSPFv3() throws CapabilityException;

	/**
	 * Disable OSPFv3 on the router.
	 * 
	 * @throws CapabilityException
	 */
	@Path("/deactivateOSPFv3")
	@POST
	public void deactivateOSPFv3() throws CapabilityException;

	/**
	 * Configure OSPFv3 service.
	 * 
	 * This configuration applies to the OSPF Service itself, but does not affect areas nor interfaces.
	 * 
	 * @param ospfService
	 * @throws CapabilityException
	 */
	@Path("/configureOSPFv3")
	@Consumes(MediaType.APPLICATION_XML)
	@POST
	public void configureOSPFv3(OSPFService ospfService) throws CapabilityException;

	/**
	 * Removes all OSPFv3 configuration.
	 * 
	 * @param ospfService
	 * @throws CapabilityException
	 */
	@Path("/clearOSPFv3configuration")
	@Consumes(MediaType.APPLICATION_XML)
	@POST
	public void clearOSPFv3configuration(OSPFService ospfService) throws CapabilityException;

	/**
	 * Configures an OSPFv3 area.
	 * 
	 * @param ospfAreaConfiguration
	 * @throws CapabilityException
	 */
	@Path("/configureOSPFv3Area")
	@Consumes(MediaType.APPLICATION_XML)
	@POST
	public void configureOSPFv3Area(OSPFAreaConfiguration ospfAreaConfiguration) throws CapabilityException;

	/**
	 * Remove an OSPFv3 area.
	 * 
	 * @param ospfAreaConfiguration
	 * @throws CapabilityException
	 */
	@Path("/removeOSPFv3Area")
	@Consumes(MediaType.APPLICATION_XML)
	@POST
	public void removeOSPFv3Area(OSPFAreaConfiguration ospfAreaConfiguration) throws CapabilityException;

	/**
	 * Adds given interfaces to given OSPFv3 area
	 * 
	 * @param addInterfacesOSPFRequest
	 * @throws CapabilityException
	 */
	@Path("/addInterfacesInOSPFv3Area")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void addInterfacesInOSPFv3Area(AddInterfacesInOSPFAreaRequest addInterfacesInOSPFAreaRequest)
			throws CapabilityException;

	/**
	 * Adds given interfaces to given OSPFv3 area
	 * 
	 * @param interfaces
	 * @param ospfArea
	 * @throws CapabilityException
	 */
	public void addInterfacesInOSPFv3Area(List<LogicalPort> interfaces, OSPFArea ospfArea)
			throws CapabilityException;

	/**
	 * Remove given interfaces from given OSPFv3 area
	 * 
	 * @param removeInterfacesOSPFRequest
	 * @throws CapabilityException
	 */
	@Path("/removeInterfacesInOSPFv3Area")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void removeInterfacesInOSPFv3Area(RemoveInterfacesInOSPFAreaRequest removeInterfacesInOSPFAreaRequest)
			throws CapabilityException;

	/**
	 * Remove given interfaces from given OSPFv3 area
	 * 
	 * @param interfaces
	 * @param ospfArea
	 * @throws CapabilityException
	 */
	public void removeInterfacesInOSPFv3Area(List<LogicalPort> interfaces, OSPFArea ospfArea)
			throws CapabilityException;

	/**
	 * Enable OSPFv3 in given interfaces, if they are already configured.
	 * 
	 * @param interfaces
	 * @throws CapabilityException
	 */
	@Path("/enableOSPFv3Interfaces")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void enableOSPFv3Interfaces(List<OSPFProtocolEndpoint> interfaces) throws CapabilityException;

	/**
	 * Disable OSPFv3 in given interfaces, if they are already configured.
	 * 
	 * @param interfaces
	 * @throws CapabilityException
	 */
	@Path("/disableOSPFInterfaces")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void disableOSPFv3Interfaces(List<OSPFProtocolEndpoint> interfaces) throws CapabilityException;

	/**
	 * Returns OSPFv3 full configuration from the router
	 * 
	 * This includes service, areas, and interface status.
	 * 
	 * @throws CapabilityException
	 */
	@Path("/getOSPFv3Configuration")
	@POST
	public void getOSPFv3Configuration() throws CapabilityException;

	/**
	 * Returns OSPFv3 full configuration from the model
	 * 
	 * This includes service, areas, and interface status.
	 * 
	 * @return ospfService
	 * @throws CapabilityException
	 */
	// TODO: export this method using rest too
	public OSPFService showOSPFv3Configuration() throws CapabilityException;

	@Path("/readOSPFv3Configuration")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public OSPFServiceWrapper readOSPFv3Configuration() throws CapabilityException;

}
