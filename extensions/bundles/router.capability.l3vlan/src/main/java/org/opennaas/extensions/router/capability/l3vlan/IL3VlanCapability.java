package org.opennaas.extensions.router.capability.l3vlan;

/*
 * #%L
 * OpenNaaS :: Router :: L3 VLAN Capability
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
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.router.capabilities.api.model.vlanbridge.BridgeDomains;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
@Path("/")
public interface IL3VlanCapability extends ICapability {

	@PUT
	@Path("/{domainName}/ip")
	public void addIpAddressToBridgedDomain(@PathParam("domainName") String domainName, @QueryParam("ipAddress") String ipAddress)
			throws CapabilityException;

	@DELETE
	@Path("/{domainName}/ip")
	public void removeIpAddressfromBridgedDomain(@PathParam("domainName") String domainName, @QueryParam("ipAddress") String ipAddress)
			throws CapabilityException;

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_XML)
	public BridgeDomains getL3Vlans() throws CapabilityException;
}
