package org.opennaas.extensions.ofnetwork.capability.ofprovision;

/*
 * #%L
 * OpenNaaS :: OF Network
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
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.ofnetwork.model.NetOFFlow;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
@Path("/")
public interface IOFProvisioningNetworkCapability extends ICapability {

	/**
	 * Allocates given flows
	 * 
	 * @param flows
	 *            to be allocated
	 * @throws CapabilityException
	 */
	@Path("/allocateFlows")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public void allocateFlows(List<NetOFFlow> flows) throws CapabilityException;

	/**
	 * Deallocates given allocated flows
	 * 
	 * @param flows
	 * @throws CapabilityException
	 */
	@Path("/deallocateFlows/")
	@POST
	public void deallocateFlows(List<NetOFFlow> flows) throws CapabilityException;

	/**
	 * 
	 * @return allocated flows in the network
	 * @throws CapabilityException
	 */
	@Path("/getAllocatedFlows")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Set<NetOFFlow> getAllocatedFlows() throws CapabilityException;

	/**
	 * Replaces given current flows with desired ones.
	 * 
	 * @param current
	 *            flows to be replaced
	 * @param desired
	 *            replacement flows
	 * @return
	 * @throws CapabilityException
	 */
	@Path("/replaceFlows/")
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public void replaceFlows(List<NetOFFlow> current, List<NetOFFlow> desired) throws CapabilityException;

}
