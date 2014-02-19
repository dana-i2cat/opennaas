package org.opennaas.extensions.genericnetwork.capability.nclprovisioner;

/*
 * #%L
 * OpenNaaS :: Generic Network
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
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.api.CircuitCollection;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.api.CircuitId;
import org.opennaas.extensions.genericnetwork.model.path.PathRequest;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
@Path("/")
public interface INCLProvisionerCapability extends ICapability {

	/**
	 * Allocates a circuit.
	 * 
	 * @param qosPolicyRequest
	 * @return circuitId of allocated circuit
	 * @throws CapabilityException
	 */
	public String allocateCircuit(PathRequest pathRequest) throws CapabilityException;

	/**
	 * Allocates a circuit.
	 * 
	 * @param qosPolicyRequest
	 * @return circuitId of allocated circuit
	 * @throws CapabilityException
	 */
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public CircuitId allocateCircuitAPI(PathRequest pathRequest) throws CapabilityException;

	/**
	 * Deallocates an allocated circuit.
	 * 
	 * @param qosPolicyRequest
	 * @return circuitId of allocated circuit
	 * @throws CapabilityException
	 */
	@DELETE
	@Path("/{id}")
	public void deallocateCircuit(@PathParam("id") String circuitId) throws CapabilityException;

	/**
	 * Returns currently allocated circuits
	 * 
	 * @return Currently allocated circuits.
	 * @throws ProvisionerException
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_XML)
	public CircuitCollection getAllocatedCircuits() throws CapabilityException;

	@PUT
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public void updateCircuit(@PathParam("{id}") String circuitId, PathRequest pathRequest) throws CapabilityException;

}
