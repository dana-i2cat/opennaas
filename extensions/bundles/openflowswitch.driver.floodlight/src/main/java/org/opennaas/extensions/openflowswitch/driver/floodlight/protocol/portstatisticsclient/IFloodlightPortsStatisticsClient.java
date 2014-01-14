package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.portstatisticsclient;

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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.portstatisticsclient.wrappers.SwitchStatisticsMap;

/**
 * Floodlight ports statistics API client
 * 
 * @author Julio Carlos Barrera
 * 
 */
@Path("/wm/core")
public interface IFloodlightPortsStatisticsClient {

	/**
	 * Get ports statistics for all switches
	 */
	@GET
	@Path("/switch/all/port/json")
	@Produces(MediaType.APPLICATION_JSON)
	public SwitchStatisticsMap getPortsStatisticsForAllSwitches() throws ProtocolException, Exception;

	/**
	 * Get ports statistics for one switch
	 */
	@GET
	@Path("/switch/{switchId}/port/json")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public SwitchStatisticsMap getPortsStatistics(@PathParam("switchId") String switchId) throws ProtocolException, Exception;

}
