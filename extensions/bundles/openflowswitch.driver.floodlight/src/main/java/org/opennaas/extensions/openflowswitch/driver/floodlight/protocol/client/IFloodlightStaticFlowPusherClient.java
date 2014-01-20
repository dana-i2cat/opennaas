package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client;

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

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.wrappers.FloodlightOFFlowsWrapper;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;

@Path("/wm/staticflowentrypusher")
public interface IFloodlightStaticFlowPusherClient {

	/**
	 * Adds a static flow.
	 * 
	 * @param flow
	 *            The flow to push.
	 */
	@Path("/json")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void addFlow(FloodlightOFFlow flow) throws ProtocolException, Exception;

	/**
	 * Deletes a static flow
	 * 
	 * @param name
	 *            The name of the static flow to delete.
	 */
	@Path("/json")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteFlow(FloodlightOFFlow flow) throws ProtocolException, Exception;

	/**
	 * Deletes all static flows for a particular switch
	 * 
	 * @param dpid
	 *            The DPID of the switch to delete flows for.
	 */
	@Path("clear/{switchId}/json")
	public void deleteFlowsForSwitch(@PathParam("switchId") String dpid) throws ProtocolException, Exception;

	/**
	 * Deletes all flows.
	 */
	@Path("clear/all/json")
	public void deleteAllFlows() throws ProtocolException, Exception;

	/**
	 * Gets all list of all flows
	 */
	@Path("list/all/json")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, List<FloodlightOFFlow>> getFlows() throws ProtocolException, Exception;

	/**
	 * Gets a list of flows by switch
	 */
	@GET
	@Path("list/{switchId}/json")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public FloodlightOFFlowsWrapper getFlows(@PathParam("switchId") String dpid) throws ProtocolException, Exception;

}
