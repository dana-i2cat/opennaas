package org.opennaas.extensions.router.capability.staticroute;

/*
 * #%L
 * OpenNaaS :: Router :: Static route capability
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
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.router.capabilities.api.model.staticroute.StaticRoute;
import org.opennaas.extensions.router.capabilities.api.model.staticroute.StaticRouteCollection;

/**
 * @author Jordi Puig
 * @author Adrian Rosello Rey (i2CAT)
 * @author Julio Carlos Barrera
 */
@Path("/")
public interface IStaticRouteCapability extends ICapability {

	/**
	 * Create a static route in the router
	 * 
	 * @param netIdIpAdress
	 * @param maskIpAdress
	 * @param nextHopIpAddress
	 * @throws CapabilityException
	 */
	@Deprecated
	public void createStaticRoute(String netIdIpAdress, String maskIpAdress, String nextHopIpAddress, String isDiscard) throws CapabilityException;

	/**
	 * Create a static route in the router
	 * 
	 * @param netIdIpAdress
	 * @param maskIpAdress
	 * @param nextHopIpAddress
	 * @throws CapabilityException
	 */
	public void createStaticRoute(String netIdIpAdress, String nextHopIpAddress, boolean isDiscard, int preference) throws CapabilityException;

	/**
	 * Create a static route in the router
	 * 
	 * @param staticRoute
	 * @throws CapabilityException
	 */
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_XML)
	public void createStaticRoute(StaticRoute staticRoute) throws CapabilityException;

	/**
	 * Deletes a static route in the router
	 * 
	 * @param netIdIpAdress
	 * @param maskIpAdress
	 * @param nextHopIpAddress
	 * @throws CapabilityException
	 */
	@Deprecated
	public void deleteStaticRoute(String netIdIpAdress, String maskIpAdress, String nextHopIpAddress) throws CapabilityException;

	/**
	 * Deletes a static route in the router
	 * 
	 * @param netIdIpAdress
	 * @param maskIpAdress
	 * @param nextHopIpAddress
	 * @throws CapabilityException
	 */
	@DELETE
	@Path("/")
	public void deleteStaticRoute(@QueryParam("netIdIpAdress") String netIdIpAdress, @QueryParam("nextHopIpAddress") String nextHopIpAddress)
			throws CapabilityException;

	/**
	 * Returns the list of static routes stored in the model.
	 * 
	 * @return
	 * @throws CapabilityException
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_XML)
	public StaticRouteCollection getStaticRoutes() throws CapabilityException;
}
