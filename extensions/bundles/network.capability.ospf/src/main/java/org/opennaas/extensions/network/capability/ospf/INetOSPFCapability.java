package org.opennaas.extensions.network.capability.ospf;

/*
 * #%L
 * OpenNaaS :: Network :: OSPF Capability
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

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;

@Path("/")
public interface INetOSPFCapability extends ICapability {

	/**
	 * Enable OSPF on the network.
	 * 
	 * 1) Configures OSPF in all routers of the network. </b> 2) Configures a backbone area in them. </b> 3) Add all interfaces on this routers to
	 * this area </b> 4) Enables OSPF in all routers </b>
	 * 
	 * @throws CapabilityException
	 */
	@POST
	@Path("/activate")
	public void activateOSPF() throws CapabilityException;

	/**
	 * Disable OSPF on the network.
	 * 
	 * @return
	 * @throws CapabilityException
	 */
	@POST
	@Path("/deactivate")
	public void deactivateOSPF() throws CapabilityException;
}