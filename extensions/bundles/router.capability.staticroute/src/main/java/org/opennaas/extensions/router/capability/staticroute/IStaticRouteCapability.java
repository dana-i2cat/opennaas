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

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;

/**
 * @author Jordi Puig
 * @author Adrian Rosello Rey (i2CAT)
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
	public void createStaticRoute(@QueryParam("netIdIpAdress") String netIdIpAdress, @QueryParam("maskIpAdress") String maskIpAdress,
			@QueryParam("nextHopIpAddress") String nextHopIpAddress, @QueryParam("isDiscard") String isDiscard) throws CapabilityException;

	/**
	 * Create a static route in the router
	 * 
	 * @param netIdIpAdress
	 * @param maskIpAdress
	 * @param nextHopIpAddress
	 * @throws CapabilityException
	 */
	@POST
	@Path("/")
	public void createStaticRoute(@QueryParam("netIdIpAdress") String netIdIpAdress,
			@QueryParam("nextHopIpAddress") String nextHopIpAddress, @QueryParam("isDiscard") String isDiscard) throws CapabilityException;

	/**
	 * Deletes a static route in the router
	 * 
	 * @param netIdIpAdress
	 * @param maskIpAdress
	 * @param nextHopIpAddress
	 * @throws CapabilityException
	 */
	@Deprecated
	public void deleteStaticRoute(@QueryParam("netIdIpAdress") String netIdIpAdress, @QueryParam("maskIpAdress") String maskIpAdress,
			@QueryParam("nextHopIpAddress") String nextHopIpAddress) throws CapabilityException;

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

}
