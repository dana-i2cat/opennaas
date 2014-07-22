package org.opennaas.extensions.quantum.capability.extensions.l3.shell;

/*
 * #%L
 * OpenNaaS :: Quantum
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

import javax.ws.rs.Path;

import org.opennaas.core.resources.capability.ICapability;

/**
 * Quantum Extension L3 Networking API<br />
 * Based on <a href="http://docs.openstack.org/api/openstack-network/2.0/content/router_ext.html">OpenStack L3 Networking Extension Documentation -
 * Router API Operations</a>
 * 
 * @author Julio Carlos Barrera
 * 
 */
@Path("/")
public interface IQuantumL3Capability extends ICapability {
	// TODO
}
