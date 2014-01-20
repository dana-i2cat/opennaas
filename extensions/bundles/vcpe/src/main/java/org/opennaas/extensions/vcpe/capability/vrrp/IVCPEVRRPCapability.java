package org.opennaas.extensions.vcpe.capability.vrrp;

/*
 * #%L
 * OpenNaaS :: vCPENetwork
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
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

@Path("/")
public interface IVCPEVRRPCapability extends ICapability {

	/**
	 * Update the VRRP virtual ip address
	 * 
	 * @param model
	 * @throws CapabilityException
	 */
	@Path("/updateVRRPIp")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void updateVRRPIp(VCPENetworkModel model) throws CapabilityException;

	/**
	 * Change the priority VRRP
	 * 
	 * @param model
	 * @return
	 * @throws CapabilityException
	 */
	@Path("/changeVRRPPriority")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public VCPENetworkModel changeVRRPPriority(VCPENetworkModel model) throws CapabilityException;

}
