package org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch :: Ryu driver v3.14
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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.model.RyuOFFlow;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.wrappers.RyuOFFlowListWrapper;

/**
 * Ryu ofctl_rest client interface based on <a href=http://ryu.readthedocs.org/en/latest/app/ofctl_rest.html>official documentation</a>.
 * 
 * @author Julio Carlos Barrera
 *
 */
@Path("/stats")
public interface IRyuStatsClient {

	/**
	 * Adds a flow entry
	 * 
	 * @param flow
	 *            The flow to push
	 */
	@Path("/flowentry/add")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void addFlowEntry(RyuOFFlow flow) throws ProtocolException, Exception;

	/**
	 * Deletes a flow entry strictly
	 * 
	 * @param name
	 *            The name of the static flow to delete.
	 */
	@Path("/flowentry/delete_strict")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteFlowEntryStrictly(RyuOFFlow flow) throws ProtocolException, Exception;

	/**
	 * Deletes all flow entries for a particular DPID
	 * 
	 * @param dpid
	 *            The DPID of the switch to delete flows.
	 */
	@Path("clear/{dpid}/json")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteAllFlowsEntriesForDPID(@PathParam("dpid") String dpid) throws ProtocolException, Exception;

	/**
	 * Gets list of all flows for given DPID
	 * 
	 * @param dpid
	 *            The DPID of the switch to get flow entries.
	 */
	@Path("/flow/{dpid}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RyuOFFlowListWrapper getFlows(@PathParam("dpid") String dpid) throws ProtocolException, Exception;

}
