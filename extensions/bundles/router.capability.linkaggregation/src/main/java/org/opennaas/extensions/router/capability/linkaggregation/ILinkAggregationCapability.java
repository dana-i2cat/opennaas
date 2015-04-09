package org.opennaas.extensions.router.capability.linkaggregation;

/*
 * #%L
 * OpenNaaS :: Router :: Link Aggregation Capability
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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.router.capabilities.api.model.chassis.InterfacesNamesList;
import org.opennaas.extensions.router.capability.linkaggregation.api.AggregatedInterface;

/**
 * Link Aggregation Capability interface
 * 
 * @author Julio Carlos Barrera
 * 
 */
@Path("/")
public interface ILinkAggregationCapability extends ICapability {

	/**
	 * Returns a list of aggregated interfaces names
	 * 
	 * @return available aggregated interfaces names.
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_XML)
	public InterfacesNamesList getAggregatedInterfaces() throws CapabilityException;

	/**
	 * Returns aggregated interface with aggregatedInterfaceId
	 */
	@GET
	@Path("/{aggregatedInterfaceId}")
	@Produces(MediaType.APPLICATION_XML)
	public AggregatedInterface getAggregatedInterface(@PathParam("aggregatedInterfaceId") String aggregatedInterfaceId);

	/**
	 * Creates an aggregated interface
	 * 
	 * 
	 * @param aggregatedInterface Description of the interface to create
	 * @param force when true this operation will erase any configuration subinterfaces may have.
	 * @throws CapabilityException
	 */
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_XML)
	public void createAggregatedInterface(AggregatedInterface aggregatedInterface, @QueryParam("force") boolean force) throws CapabilityException;

	/**
	 * Removes an Aggregated Interface given the aggregatedInterfaceId
	 * 
	 */
	@DELETE
	@Path("/{aggregatedInterfaceId}")
	@Consumes(MediaType.APPLICATION_XML)
	public void removeAggregatedInterface(@PathParam("aggregatedInterfaceId") String aggregatedInterfaceId) throws CapabilityException;
}
