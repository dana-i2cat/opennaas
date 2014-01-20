package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.countersclient;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch :: Floodlight driver v0.90
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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.countersclient.wrappers.CountersMap;

/**
 * Floodlight counters API client
 * 
 * @author Julio Carlos Barrera
 * 
 */
@Path("/wm/core/counter")
public interface IFloodlightCountersClient {

	/**
	 * Get all counters for all switches
	 */
	@GET
	@Path("/all/json")
	@Produces(MediaType.APPLICATION_JSON)
	public CountersMap getAllCountersForAllSwitches() throws ProtocolException, Exception;

	/**
	 * Get all counters for one switch
	 */
	@GET
	@Path("/{switchId}/all/json")
	@Produces(MediaType.APPLICATION_JSON)
	public CountersMap getAllCounters(@PathParam("switchId") String switchId) throws ProtocolException, Exception;

	/**
	 * Get one counter for one switch
	 */
	@GET
	@Path("/{switchId}/{counterId}/json")
	@Produces(MediaType.APPLICATION_JSON)
	public CountersMap getCounter(@PathParam("switchId") String switchId, @PathParam("counterId") String counterId) throws ProtocolException,
			Exception;

}
