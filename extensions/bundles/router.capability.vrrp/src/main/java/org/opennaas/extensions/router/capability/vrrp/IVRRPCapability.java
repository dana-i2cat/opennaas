package org.opennaas.extensions.router.capability.vrrp;

/*
 * #%L
 * OpenNaaS :: Router :: VRRP Capability
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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.router.model.VRRPGroup;
import org.opennaas.extensions.router.model.VRRPProtocolEndpoint;

/**
 * @author Julio Carlos Barrera
 */
@Path("/")
public interface IVRRPCapability extends ICapability {

	/**
	 * Configure VRRP
	 * 
	 * @throws CapabilityException
	 */
	@Path("/configureVRRP")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void configureVRRP(VRRPProtocolEndpoint vrrpProtocolEndpoint) throws CapabilityException;

	/**
	 * Unconfigure VRRP
	 * 
	 * @throws CapabilityException
	 */
	@Path("/unconfigureVRRP")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void unconfigureVRRP(VRRPProtocolEndpoint vrrpProtocolEndpoint) throws CapabilityException;

	/**
	 * Update VRRP Virtual IP Address
	 * 
	 * @throws CapabilityException
	 */
	@Path("/updateVRRPVirtualIPAddress")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void updateVRRPVirtualIPAddress(VRRPProtocolEndpoint vrrpProtocolEndpoint) throws CapabilityException;

	/**
	 * Update VRRP Priority
	 * 
	 * @throws CapabilityException
	 */
	/*
	 * @Path("/updateVRRPPriority")
	 * 
	 * @POST
	 * 
	 * @Consumes(MediaType.APPLICATION_XML)
	 */
	public void updateVRRPPriority(VRRPProtocolEndpoint vrrpProtocolEndpoint) throws CapabilityException;

	@Path("/updateVRRPVirtualLinkAddress")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void updateVRRPVirtualLinkAddress(VRRPGroup vrrpGroup) throws CapabilityException;
}
