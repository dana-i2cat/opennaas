package org.opennaas.extensions.network.capability.queue;

/*
 * #%L
 * OpenNaaS :: Network :: Queue Capability
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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;

/**
 * @author Jordi Puig
 */
@Path("/")
public interface IQueueCapability extends ICapability {

	/**
	 * This action will execute each resource queue
	 * 
	 * @return the queue response
	 * @throws CapabilityException
	 */
	@Path("/execute")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response execute() throws CapabilityException;

}