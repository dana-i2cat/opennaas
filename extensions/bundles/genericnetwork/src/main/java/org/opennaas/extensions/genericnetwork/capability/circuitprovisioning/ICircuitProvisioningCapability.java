package org.opennaas.extensions.genericnetwork.capability.circuitprovisioning;

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

import java.util.List;

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
import org.opennaas.extensions.genericnetwork.capability.circuitprovisioning.api.CircuitsList;
import org.opennaas.extensions.genericnetwork.capability.circuitprovisioning.api.OldAndNewCircuits;
import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;

/**
 * Circuit Provisioning Capability interface
 * 
 * @author Julio Carlos Barrera
 * 
 */
@Path("/")
public interface ICircuitProvisioningCapability extends ICapability {

	/**
	 * Retrieves current circuits
	 * 
	 * @return
	 * @throws CapabilityException
	 */
	@Path("/")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public CircuitsList getCircuitsAPI() throws CapabilityException;

	public List<Circuit> getCircuits() throws CapabilityException;

	/**
	 * Replaces existent circuits by given new ones
	 * 
	 * @param oldCircuits
	 * @param newCircuits
	 * @throws CapabilityException
	 */
	@Path("/replace")
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public void replace(OldAndNewCircuits oldAndNewCircuits) throws CapabilityException;

	public void replace(List<Circuit> oldCircuits, List<Circuit> newCircuits) throws CapabilityException;

	/**
	 * Allocates a new circuit
	 * 
	 * @param circuit
	 * @throws CapabilityException
	 */
	@Path("/")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void allocate(Circuit circuit) throws CapabilityException;

	/**
	 * Deallocates an existent circuit
	 * 
	 * @param circuit
	 * @throws CapabilityException
	 */
	@Path("/{circuitId}")
	@DELETE
	public void deallocate(@PathParam("circuitId") String circuitId) throws CapabilityException;

}
