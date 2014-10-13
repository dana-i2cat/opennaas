package org.opennaas.extensions.network.capability.topology;

/*
 * #%L
 * OpenNaaS :: Network :: Basic capability
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
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.network.capability.topology.api.NetworkTopology;

@Path("/")
public interface ITopologyDiscoveryCapability extends ICapability {

	/**
	 * Retrieves Network Topology model
	 * 
	 * @return Network Topology
	 * @throws CapabilityException
	 */
	@Path("/")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public NetworkTopology getNetworkTopology() throws CapabilityException;

	/**
	 * Adds a resource to network topology.
	 * 
	 * @param resourceId
	 *            the id of the resource
	 * @return NetworkModel updated with added resource
	 * @throws CapabilityException
	 *             if an error occurred.
	 */
	@Path("/resource")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void addResource(String resourceId) throws CapabilityException;

	/**
	 * Adds resource to network topology.
	 * 
	 * @param resourceToAdd
	 *            IResource to be added
	 * @throws CapabilityException
	 *             if an error occurred.
	 */
	public void addResource(IResource resourceToAdd) throws CapabilityException;

	/**
	 * Removes a resource from network topology.
	 * 
	 * @param resourceId
	 *            the id of the resource
	 * @return NetworkModel updated with added resource
	 * @throws CapabilityException
	 *             if an error occurred.
	 */
	@Path("/resource")
	@DELETE
	@Consumes(MediaType.APPLICATION_XML)
	public void removeResource(String resourceId) throws CapabilityException;

	/**
	 * Removes a resource from network topology.
	 * 
	 * @param resourceId
	 *            the id of the resource
	 * @return NetworkModel updated without resourceToRemove
	 * @throws CapabilityException
	 *             if an error occurred.
	 */
	public void removeResource(IResource resourceToRemove) throws CapabilityException;

}
